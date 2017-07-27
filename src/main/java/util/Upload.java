package util;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import model.Client;
import model.FileLink;
import model.Security;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.*;

import static util.FilestackService.Upload.*;

public class Upload {
    public static final int CHUNK_SIZE = 5 * 1024 * 1024;
    public static final int NUM_THREADS = 4;

    private final Client client;

    private final HashMap<String, RequestBody> baseParams;

    private final String filepath;
    private final String filename;
    private final String mimeType;
    private final MediaType mediaType;
    private final long size;

    private StartResponse startResponse;
    private String[] parts;
    private CompleteResponse completeResponse;

    public Upload(Client client, String filepath, UploadOptions options) throws IOException {
        this.client = client;
        this.filepath = filepath;

        baseParams = new HashMap<>();
        baseParams.put("apikey", Util.createStringPart(client.getApiKey()));
        baseParams.putAll(options.getMap());

        File file = new File(filepath);
        if (!file.exists()) throw new FileNotFoundException();

        filename = file.getName();
        mimeType = new Tika().detect(file);
        mediaType = MediaType.parse(mimeType);
        size = file.length();
    }

    public FileLink run() throws IOException {
        multipartStart();
        multipartUpload();
        multipartComplete();

        return new FileLink(client.getApiKey(), completeResponse.getHandle(), client.getSecurity());
    }

    private void multipartStart() throws IOException {
        HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);

        params.put("filename", Util.createStringPart(filename));
        params.put("size", Util.createStringPart(Long.toString(size)));
        params.put("mimetype", Util.createStringPart(mimeType));
        Security security = client.getSecurity();
        if (security != null) {
            params.put("policy", Util.createStringPart(security.getPolicy()));
            params.put("signature", Util.createStringPart(security.getSignature()));
        }

        startResponse = Networking.getUploadService().start(params).execute().body();
    }

    private void multipartUpload() throws IOException {
        int numParts = (int) Math.ceil(size / (double) CHUNK_SIZE);
        int partsPerThread = (int) Math.ceil(numParts / (double) NUM_THREADS);

        parts = new String[numParts];

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CompletionService<Void> service = new ExecutorCompletionService<Void>(executor);

        // Launch uploading tasks in threads
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * partsPerThread;
            int count = Math.min(partsPerThread, numParts);
            service.submit(new UploadCallable(start, count));
            numParts -= count;
        }

        // Wait for threads to finish and pass up any exceptions
        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                service.take().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new FilestackIOException("Upload failed", e);
            }
        }
    }

    private class UploadCallable implements Callable<Void> {
        private int start;
        private int count;

        public UploadCallable(int start, int count) {
            this.start = start;
            this.count = count;
        }

        @Override
        public Void call() throws IOException {
            if (count == 0)
                return null;

            RandomAccessFile file = new RandomAccessFile(filepath, "r");
            file.seek(start * CHUNK_SIZE);

            byte[] bytes = new byte[CHUNK_SIZE];
            int bytesRead;
            UploadResponse params;
            String etag;

            for (int i = 0; i < count; i++) {
                bytesRead = file.read(bytes);
                params = getUploadParams(start + i + 1, bytes, bytesRead);
                etag = uploadToS3(params, bytes, bytesRead);
                parts[start+i] = etag;
            }

            return null;
        }
    }

    private UploadResponse getUploadParams(int part, byte[] bytes, int size) throws IOException {
        @SuppressWarnings("deprecation")
        HashCode hc = Hashing.md5().newHasher(size).putBytes(bytes, 0, size).hash();
        String md5 = BaseEncoding.base64().encode(hc.asBytes());

        HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);
        params.putAll(startResponse.getUploadParams());
        params.put("part", Util.createStringPart(Integer.toString(part)));
        params.put("size", Util.createStringPart(Integer.toString(size)));
        params.put("md5", Util.createStringPart(md5));

        return Networking.getUploadService().upload(params).execute().body();
    }

    private String uploadToS3(UploadResponse params, byte[] bytes, int size) throws IOException {
        RequestBody body = RequestBody.create(mediaType, bytes, 0, size);

        Request request = new Request.Builder()
                .url(params.getUrl())
                .headers(params.getS3Headers())
                .put(body)
                .build();

        Response response = Networking.getHttpClient().newCall(request).execute();
        String etag = response.header("ETag");
        response.close();
        return etag;
    }

    private void multipartComplete() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++)
            builder.append(i+1).append(':').append(parts[i]).append(';');
        builder.deleteCharAt(builder.length()-1);
        String parts = builder.toString();

        HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);
        params.putAll(startResponse.getUploadParams());
        params.put("filename", Util.createStringPart(filename));
        params.put("size", Util.createStringPart(Long.toString(size)));
        params.put("mimetype", Util.createStringPart(mimeType));
        params.put("parts", Util.createStringPart(parts));

        completeResponse = Networking.getUploadService().complete(params).execute().body();
    }
}
