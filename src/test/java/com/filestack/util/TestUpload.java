package com.filestack.util;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.Policy;
import com.filestack.Security;
import com.filestack.UploadOptions;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class TestUpload {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static MockFsUploadService createMockUploadService(NetworkBehavior behavior) {

    Retrofit retrofit = new Retrofit.Builder()
        .client(new OkHttpClient())
        .baseUrl(FsUploadService.URL)
        .build();

    MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
        .networkBehavior(behavior)
        .build();

    BehaviorDelegate<FsUploadService> delegate =
        mockRetrofit.create(FsUploadService.class);
    return new MockFsUploadService(delegate);
  }

  private static Path createRandomFile(long size) throws IOException {
    Path path = Paths.get("/tmp/" + UUID.randomUUID().toString() + ".txt");
    RandomAccessFile file = new RandomAccessFile(path.toString(), "rw");
    file.writeChars("test content\n");
    file.setLength(size);
    file.close();
    return path;
  }

  @Test
  public void testInstantiation() throws Exception {
    Path path = createRandomFile(10 * 1024 * 1024);

    NetworkBehavior behavior = NetworkBehavior.create();
    MockFsUploadService mockUploadService = createMockUploadService(behavior);

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");
    FilestackClient fsClient = new FilestackClient("apiKey", security);
    UploadOptions options = new UploadOptions.Builder().build();

    Upload upload = new Upload(path.toString(), fsClient, options);
    Assert.assertNotNull(upload);
    upload = new Upload(path.toString(), fsClient, options, mockUploadService, 0);
    Assert.assertNotNull(upload);

    Files.delete(path);
  }

  @Test
  public void testRun() throws Exception {
    Path path = createRandomFile(10 * 1024 * 1024);

    NetworkBehavior behavior = NetworkBehavior.create();
    MockFsUploadService mockUploadService = createMockUploadService(behavior);

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");
    FilestackClient fsClient = new FilestackClient("apiKey", security);
    UploadOptions options = new UploadOptions.Builder().build();
    Upload upload = new Upload(path.toString(), fsClient, options, mockUploadService, 0);

    behavior.setFailurePercent(0);
    behavior.setDelay(0, TimeUnit.SECONDS);

    FileLink fileLink = upload.run();

    Assert.assertTrue(fileLink.getHandle().equals("handle"));

    Files.delete(path);
  }
}
