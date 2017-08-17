package com.filestack.util;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.Security;
import com.filestack.UploadOptions;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.errors.ValidationException;
import com.filestack.responses.CompleteResponse;
import com.filestack.responses.StartResponse;
import com.filestack.responses.UploadResponse;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/** Holds upload state and request logic. */
public class Upload {
  private static final int MIN_CHUNK_SIZE = 32 * 1024;

  private final FilestackClient fsClient;
  private final FilestackUploadService fsUploadService;

  private final int delayBase;
  private final long filesize;
  private final MediaType mediaType;
  private final String filepath;

  private boolean intelligent;
  private int chunkSize = 1024 * 1024;
  private int partSize;
  private Map<String, RequestBody> baseParams;
  private String[] etags;

  /**
   * Construct an instance using global networking singletons.
   *
   * @param pathname path to the file to upload
   * @param fsClient client used to make this upload
   * @param options  for how to store the file
   * @throws ValidationException if the pathname doesn't exist or isn't a regular file
   */
  public Upload(String pathname, FilestackClient fsClient, UploadOptions options)
      throws ValidationException {
    this(pathname, fsClient, options, Networking.getFsUploadService(), 2);
  }

  /**
   * Construct an instance using provided networking objects.
   *
   * @param pathname        for the file to upload
   * @param fsClient        client used to make this upload
   * @param options         for storing the file
   * @param fsUploadService client to make Filestack API calls
   * @param delayBase       base for exponential backoff, delay (seconds) == base ^ retryCount
   * @throws ValidationException if the pathname doesn't exist or isn't a regular file
   */
  public Upload(String pathname, FilestackClient fsClient, UploadOptions options,
                FilestackUploadService fsUploadService, int delayBase)
      throws ValidationException {

    this.filepath = pathname;
    this.fsClient = fsClient;
    this.fsUploadService = fsUploadService;
    this.delayBase = delayBase;

    // Setup base parameters
    baseParams = new HashMap<>();
    baseParams.put("apikey", Util.createStringPart(fsClient.getApiKey()));
    baseParams.putAll(options.getMap());

    // Open file and check if it exists
    File file = new File(pathname);
    if (!file.exists()) {
      throw new ValidationException("File doesn't exist: " + file.getPath());
    } else if (file.isDirectory()) {
      throw new ValidationException("Can't upload directory: " + file.getPath());
    } else if (!Files.isRegularFile(file.toPath())) {
      throw new ValidationException("Can't upload special file: " + file.getPath());
    }

    filesize = file.length();
    String mimeType = URLConnection.guessContentTypeFromName(file.getName());
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
   *
   * @return reference to new file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink run()
      throws IOException, PolicySignatureException, InvalidParameterException, InternalException {

    String handle = null;

    try {
      // These alter state on this upload object, don't need to check return
      multipartStart();
      multipartUpload();
      handle = multipartComplete().getHandle();
    } catch (Exception e) {
      try {
        Util.castExceptionAndThrow(e);
      } catch (ResourceNotFoundException unexpected) {
        // We shouldn't get one of these, so recast it if we do
        throw new InternalException(unexpected);
      }
    }

    return new FileLink(fsClient.getApiKey(), handle, fsClient.getSecurity());
  }

  /** Get initial upload parameters from Filestack. */
  private void multipartStart() throws Exception {

    StartResponse response = new RetryNetworkFunc<StartResponse>(0, 5, delayBase) {

      @Override
      Response<com.filestack.responses.StartResponse> work() throws Exception {
        return fsUploadService.start(baseParams).execute();
      }
    }
        .call();

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
  private void multipartUpload() throws Exception {

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
      } catch (InterruptedException e) {
        throw new InternalException(e);
      } catch (ExecutionException e) {
        throw (Exception) e.getCause();
      }
    }
  }

  /** Callable that does actual uploading work. We send one callable to each thread. */
  private class UploadCallable implements Callable<Void> {
    private int start;
    private int count;

    public UploadCallable(int start, int count) {
      this.start = start;
      this.count = count;
    }

    @Override
    public Void call() throws Exception {

      // No work for this thread
      if (count == 0) {
        return null;
      }

      RandomAccessFile file = new RandomAccessFile(filepath, "r");
      file.seek(start * partSize);

      byte[] bytes;
      if (intelligent) {
        bytes = new byte[chunkSize];
      } else {
        bytes = new byte[partSize];
      }

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

          if (intelligent) {
            bytesRead = file.read(bytes, 0, chunkSize);
          } else {
            bytesRead = file.read(bytes, 0, partSize);
          }

          if (bytesRead == -1) {
            break;
          }

          bytesSent = uploadToS3(part, offset, bytesRead, bytes);

          if (bytesSent < bytesRead) {
            if (bytesSent < MIN_CHUNK_SIZE) {
              throw new IOException();
            }
            chunkSize = bytesSent;
            // Seek backwards to the byte after where we've successfully sent
            // Otherwise we'd skip bytes when we reduce the chunkSize
            file.seek(((start + i) * partSize) + offset + bytesSent);
          }

          offset += bytesSent;
          bytesLeft -= bytesSent;
        }

        if (intelligent) {
          multipartCommit(part);
        }
      }

      return null;
    }
  }

  /** Get parameters from Filestack for the upload to S3. */
  private UploadResponse getUploadParams(int part, int offset, int size, byte[] bytes)
      throws Exception {

    // Deprecated because MD5 is insecure not because this is unmaintained
    @SuppressWarnings("deprecation")
    HashCode hc = Hashing.md5().newHasher(size).putBytes(bytes, 0, size).hash();
    String md5 = BaseEncoding.base64().encode(hc.asBytes());

    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(baseParams);
    params.put("part", Util.createStringPart(Integer.toString(part)));
    params.put("size", Util.createStringPart(Integer.toString(size)));
    params.put("md5", Util.createStringPart(md5));
    if (intelligent) {
      params.put("offset", Util.createStringPart(Integer.toString(offset)));
    }

    return new RetryNetworkFunc<UploadResponse>(5, 5, delayBase) {

      @Override
      Response<UploadResponse> work() throws Exception {
        return fsUploadService.upload(params).execute();
      }
    }
        .call();
  }

  /**
   * Upload a chunk to S3. Makes calls to {@link #getUploadParams(int, int, int, byte[])}.
   */
  private int uploadToS3(final int part, final int offset, final int size, final byte[] bytes)
      throws Exception {

    return new RetryNetworkFunc<Integer>(5, 5, delayBase) {
      private int attemptSize = size;

      @Override
      Response<ResponseBody> work() throws Exception {
        UploadResponse params = getUploadParams(part, offset, attemptSize, bytes);
        Map<String, String> headers = params.getS3Headers();
        String url = params.getUrl();

        RequestBody requestBody = RequestBody.create(mediaType, bytes, 0, attemptSize);
        return fsUploadService.uploadS3(headers, url, requestBody).execute();
      }

      @Override
      Response retryNetwork() throws Exception {
        if (intelligent) {
          attemptSize /= 2;
        }
        return super.retryNetwork();
      }

      @Override
      Integer process(Response response) {
        if (!intelligent) {
          String etag = response.headers().get("ETag");
          etags[part - 1] = etag;
        }
        return attemptSize;
      }
    }
        .call();
  }

  /**
   * For intelligent ingestion mode only. Sent after uploading all the chunks of a part.
   * Sends request to Filestack to start processing chunks.
   */
  private void multipartCommit(int part) throws Exception {

    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(baseParams);
    params.put("part", Util.createStringPart(Integer.toString(part)));

    new RetryNetworkFunc<ResponseBody>(5, 5, delayBase) {

      @Override
      Response<ResponseBody> work() throws Exception {
        return fsUploadService.commit(params).execute();
      }
    }
        .call();
  }

  /**
   * Called when upload is complete to get Filestack metadata for the final file.
   * In intelligent ingestion mode we poll this endpoint until the file is done processing.
   */
  private CompleteResponse multipartComplete() throws Exception {

    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(baseParams);

    if (!intelligent) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < etags.length; i++) {
        builder.append(i + 1).append(':').append(etags[i]).append(';');
      }
      builder.deleteCharAt(builder.length() - 1);
      String parts = builder.toString();
      params.put("parts", Util.createStringPart(parts));
    }

    return new RetryNetworkFunc<CompleteResponse>(5, 5, delayBase) {

      @Override
      Response<CompleteResponse> work() throws Exception {
        return fsUploadService.complete(params).execute();
      }
    }
        .call();
  }
}
