package com.filestack.util;

import com.filestack.model.FilestackClient;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.filestack.model.FileLink;
import com.filestack.model.Security;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.tika.Tika;
import retrofit2.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static com.filestack.util.FilestackService.Upload.*;

public class Upload {
    private static final int MIN_CHUNK_SIZE = 32 * 1024;

    private final FilestackClient fsClient;
    private final FilestackService.Upload restService;

    private final int delayBase;
    private final long filesize;
    private final MediaType mediaType;
    private final String filepath;

    private boolean intelligent;
    private int chunkSize = 1024 * 1024;
    private int partSize;
    private Map<String, RequestBody> baseParams;
    private String[] etags;

    // Use global upload service
    public Upload(String filepath, FilestackClient fsClient, UploadOptions options) throws IOException {
        this(filepath, fsClient, options, Networking.getUploadService(), 2);
    }

    // Use provided upload service, used for mocking
    public Upload(String filepath, FilestackClient fsClient, UploadOptions options,
                  FilestackService.Upload restService, int delayBase) throws IOException {

        this.filepath = filepath;
        this.fsClient = fsClient;
        this.restService = restService;
        this.delayBase = delayBase;

        // Setup base parameters
        baseParams = new HashMap<>();
        baseParams.put("apikey", Util.createStringPart(fsClient.getApiKey()));
        baseParams.putAll(options.getMap());

        // Open file and check if it exists
        File file = new File(filepath);
        if (!file.exists()) throw new FileNotFoundException();

        filesize = file.length();
        String mimeType = new Tika().detect(file);
        mediaType = MediaType.parse(mimeType);

        baseParams.put("filename", Util.createStringPart(file.getName()));
        baseParams.put("size", Util.createStringPart(Long.toString(file.length())));
        baseParams.put("mimetype", Util.createStringPart(mimeType));
        baseParams.put("multipart", Util.createStringPart("true"));

        Security security = fsClient.getSecurity();
        if (security != null) {
            baseParams.put("policy", Util.createStringPart(security.getPolicy()));
            baseParams.put("signature", Util.createStringPart(security.getSignature()));
        }
    }

    /**
     * Starts upload.
     */
    public FileLink run() throws IOException {
        // These alter state on this upload object, don't need to check return
        multipartStart();
        multipartUpload();

        CompleteResponse completeResponse = multipartComplete();

        return new FileLink(fsClient.getApiKey(), completeResponse.getHandle(), fsClient.getSecurity());
    }

    /**
     * Get initial upload parameters from Filestack.
     */
    private void multipartStart() throws IOException {
        StartResponse response = new RetryNetworkFunc<StartResponse>(0, 5, delayBase) {

            @Override
            Response<StartResponse> work() throws IOException {
                return restService.start(baseParams).execute();
            }
        }.call();

        baseParams.putAll(response.getUploadParams());
        intelligent = response.isIntelligent();
        if (intelligent) {
            partSize = 8 * 1024 * 1024;
        } else {
            baseParams.remove("multipart");
            partSize = 5 * 1024 * 1024;
        }
    }

    /**
     * Just sets things up, doesn't perform actual uploading
     * Calculates the total number of parts and parts per thread.
     * Sets up a pool of threads then sends each one a callable that does the actual upload work.
     */
    private void multipartUpload() throws IOException {
        int numParts = (int) Math.ceil(filesize / (double) partSize);
        int numThreads = 4;
        int partsPerThread = (int) Math.ceil(numParts / (double) numThreads);

        etags = new String[numParts];

        // Create a pool of threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        // Used to consume the result from each thread
        // In our case this is just, did it except or did it succeed
        CompletionService<Void> service = new ExecutorCompletionService<>(executor);

        // Send a callable to each thread with parameters to upload a range of parts of the file
        for (int i = 0; i < numThreads; i++) {
            int start = i * partsPerThread;
            int count = Math.min(partsPerThread, numParts);
            service.submit(new UploadCallable(start, count));
            numParts -= count;
        }

        // Wait for threads to finish and catch any exceptions
        // If uploading on any thread fails, we'll immediately get the failure and stop uploading
        // No threads will keep running after a single thread fails
        for (int i = 0; i < numThreads; i++) {
            try {
                service.take().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new FilestackIOException("Upload failed: ", e);
            }
        }
    }

    /**
     * Callable that does actual uploading work.
     * We send one callable to each thread.
     */
    private class UploadCallable implements Callable<Void> {
        private int start;
        private int count;

        public UploadCallable(int start, int count) {
            this.start = start;
            this.count = count;
        }

        @Override
        public Void call() throws IOException {
            // No work for this thread
            if (count == 0)
                return null;

            RandomAccessFile file = new RandomAccessFile(filepath, "r");
            file.seek(start * partSize);

            byte[] bytes;
            if (intelligent)
                bytes = new byte[chunkSize];
            else
                bytes = new byte[partSize];

            int bytesLeft;
            int bytesRead;
            int bytesSent;
            int offset;
            int part;

            // Loop through parts assigned to this thread
            for (int i = 0; i < count; i++) {
                bytesLeft = partSize;
                offset = 0;
                part = start + i + 1;

                // Loop through bytes of a single part
                // If standard multipart upload, we upload in one partSize chunk
                // If intelligent ingestion upload, we upload in multiple chunkSize chunks
                while (bytesLeft != 0) {

                    if (intelligent)
                        bytesRead = file.read(bytes, 0, chunkSize);
                    else
                        bytesRead = file.read(bytes, 0, partSize);

                    if (bytesRead == -1)
                        break;

                    bytesSent = uploadToS3(part, offset, bytesRead, bytes);

                    if (bytesSent < bytesRead) {
                        if (bytesSent < MIN_CHUNK_SIZE)
                            throw new IOException("Upload failed: Network unusable");
                        chunkSize = bytesSent;
                        // Seek backwards in the file to the byte after where we've successfully sent
                        // Otherwise we'd skip bytes when we reduce the chunkSize
                        file.seek(((start + i) * partSize) + offset + bytesSent);
                    }

                    offset += bytesSent;
                    bytesLeft -= bytesSent;
                }

                if (intelligent)
                    multipartCommit(part);
            }

            return null;
        }
    }

    /**
     * Get parameters from Filestack for the upload to S3.
     */
    private UploadResponse getUploadParams(int part, int offset, int size, byte[] bytes) throws IOException {

        // Deprecated because MD5 is insecure not because this is unmaintained
        @SuppressWarnings("deprecation")
        HashCode hc = Hashing.md5().newHasher(size).putBytes(bytes, 0, size).hash();
        String md5 = BaseEncoding.base64().encode(hc.asBytes());

        final HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);
        params.put("part", Util.createStringPart(Integer.toString(part)));
        params.put("size", Util.createStringPart(Integer.toString(size)));
        params.put("md5", Util.createStringPart(md5));
        if (intelligent)
            params.put("offset", Util.createStringPart(Integer.toString(offset)));

        return new RetryNetworkFunc<UploadResponse>(5, 5, delayBase) {

            @Override
            Response<UploadResponse> work() throws IOException {
                return restService.upload(params).execute();
            }
        }.call();
    }

    /**
     * Upload a chunk to S3.
     * Makes calls to {@link #getUploadParams(int, int, int, byte[])}.
     */
    private int uploadToS3(final int part, final int offset, final int size, final byte[] bytes) throws IOException {

        return new RetryNetworkFunc<Integer>(5, 5, delayBase) {
            private int attemptSize = size;

            @Override
            Response<ResponseBody> work() throws IOException {
                UploadResponse params = getUploadParams(part, offset, attemptSize, bytes);
                Map<String, String> headers = params.getS3Headers();
                String url = params.getUrl();

                RequestBody requestBody = RequestBody.create(mediaType, bytes, 0, attemptSize);
                return restService.uploadS3(headers, url, requestBody).execute();
            }

            @Override
            Response retryNetwork() throws IOException {
                if (intelligent)
                    attemptSize /= 2;
                return super.retryNetwork();
            }

            @Override
            Integer process(Response response) {
                if (!intelligent) {
                    String etag = response.headers().get("ETag");
                    etags[part-1] = etag;
                }
                return attemptSize;
            }
        }.call();
    }

    /**
     * For intelligent ingestion mode only.
     * Sent after uploading all the chunks of a part.
     * Sends request to Filestack to start processing chunks.
     */
    private void multipartCommit(int part) throws IOException {
        final HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);
        params.put("part", Util.createStringPart(Integer.toString(part)));

        new RetryNetworkFunc<ResponseBody>(5, 5, delayBase) {

            @Override
            Response<ResponseBody> work() throws IOException {
                return restService.commit(params).execute();
            }
        }.call();
    }

    /**
     * Called when upload is complete to get Filestack metadata for the final file.
     * In intelligent ingestion mode we poll this endpoint until the file is done processing.
     */
    private CompleteResponse multipartComplete() throws IOException {
        final HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);

        if (!intelligent) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < etags.length; i++)
                builder.append(i+1).append(':').append(etags[i]).append(';');
            builder.deleteCharAt(builder.length()-1);
            String parts = builder.toString();
            params.put("parts", Util.createStringPart(parts));
        }

        return new RetryNetworkFunc<CompleteResponse>(5, 5, delayBase) {

            @Override
            Response<CompleteResponse> work() throws IOException {
                return restService.complete(params).execute();
            }
        }.call();
    }
}
