package com.filestack;

import com.filestack.responses.CompleteResponse;
import com.filestack.responses.StartResponse;
import com.filestack.responses.UploadResponse;
import com.filestack.util.FsService;
import com.filestack.util.FsUploadService;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

/**
 * Tests {@link FilestackClient FilestackClient} class.
 */
public class TestFilestackClient {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static Path createRandomFile(long size) throws IOException {
    Path path = Paths.get("/tmp/" + UUID.randomUUID().toString() + ".txt");
    RandomAccessFile file = new RandomAccessFile(path.toString(), "rw");
    file.writeChars("test content\n");
    file.setLength(size);
    file.close();
    return path;
  }

  private static void setupStartMock(FsUploadService mockUploadService) {
    String jsonString = "{"
        + "'uri' : '/bucket/apikey/filename',"
        + "'region' : 'region',"
        + "'upload_id' : 'id',"
        + "'location_url' : 'url',"
        + "'upload_type' : 'intelligent_ingestion'"
        + "}";

    Gson gson = new Gson();
    StartResponse response = gson.fromJson(jsonString, StartResponse.class);
    Call call = Calls.response(response);
    Mockito
        .doReturn(call)
        .when(mockUploadService)
        .start(Mockito.<String, RequestBody>anyMap());
  }

  private static void setupUploadMock(FsUploadService mockUploadService) {
    String jsonString = "{"
        + "'url' : 'https://s3.amazonaws.com/path',"
        + "'headers' : {"
        + "'Authorization' : 'auth_value',"
        + "'Content-MD5' : 'md5_value',"
        + "'x-amz-content-sha256' : 'sha256_value',"
        + "'x-amz-date' : 'date_value',"
        + "'x-amz-acl' : 'acl_value'"
        + "},"
        + "'location_url' : 'url'"
        + "}";

    Gson gson = new Gson();
    final UploadResponse response = gson.fromJson(jsonString, UploadResponse.class);
    Mockito
        .doAnswer(new Answer() {
          @Override
          public Object answer(InvocationOnMock invocation) throws Throwable {
            return Calls.response(response);
          }
        })
        .when(mockUploadService)
        .upload(Mockito.<String, RequestBody>anyMap());
  }

  private static void setupUploadS3Mock(FsUploadService mockUploadService) {
    MediaType mediaType = MediaType.parse("text/xml");
    ResponseBody responseBody = ResponseBody.create(mediaType, "");
    final Response<ResponseBody> response = Response.success(responseBody,
        Headers.of("ETag", "test-etag"));
    Mockito
        .doAnswer(new Answer() {
          @Override
          public Object answer(InvocationOnMock invocation) throws Throwable {
            return Calls.response(response);
          }
        })
        .when(mockUploadService)
        .uploadS3(Mockito.<String, String>anyMap(), Mockito.anyString(),
            Mockito.any(RequestBody.class));
  }

  private static void setupCommitMock(FsUploadService mockUploadService) {
    MediaType mediaType = MediaType.parse("text/plain");
    final ResponseBody response = ResponseBody.create(mediaType, "");
    Mockito
        .doAnswer(new Answer() {
          @Override
          public Object answer(InvocationOnMock invocation) throws Throwable {
            return Calls.response(response);
          }
        })
        .when(mockUploadService)
        .commit(Mockito.<String, RequestBody>anyMap());
  }

  private static void setupCompleteMock(FsUploadService mockUploadService) {
    String jsonString = "{"
        + "'handle' : 'handle',"
        + "'url' : 'url',"
        + "'filename' : 'filename',"
        + "'size' : '0',"
        + "'mimetype' : 'mimetype'"
        + "}";

    Gson gson = new Gson();
    CompleteResponse response = gson.fromJson(jsonString, CompleteResponse.class);
    Call call = Calls.response(response);
    Mockito
        .doReturn(call)
        .when(mockUploadService)
        .complete(Mockito.<String, RequestBody>anyMap());
  }

  @Test
  public void testConstructors() {
    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");

    FilestackClient client1 = new FilestackClient("apiKey");
    FilestackClient client2 = new FilestackClient("apiKey", security);
  }

  @Test
  public void testExceptionPassing() throws Exception {
    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");

    FilestackClient client = new FilestackClient("apiKey", security);
    thrown.expect(FileNotFoundException.class);
    client.upload("/does_not_exist.txt", true);
  }

  @Test
  public void testUpload() throws Exception {
    FsUploadService mockUploadService = Mockito.mock(FsUploadService.class);

    setupStartMock(mockUploadService);
    setupUploadMock(mockUploadService);
    setupUploadS3Mock(mockUploadService);
    setupCommitMock(mockUploadService);
    setupCompleteMock(mockUploadService);

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");

    FsService mockFsService = new FsService(null, null, mockUploadService, null);
    FilestackClient client = new FilestackClient("apiKey", security, mockFsService);

    Path path = createRandomFile(10 * 1024 * 1024);

    FileLink fileLink = client.upload(path.toString(), false);

    Assert.assertEquals("handle", fileLink.getHandle());

    Files.delete(path);
  }
}
