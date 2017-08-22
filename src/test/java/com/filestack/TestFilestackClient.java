package com.filestack;

import com.filestack.util.FsUploadService;
import com.filestack.util.MockFsUploadService;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

/**
 * Tests {@link FilestackClient FilestackClient} class.
 */
public class TestFilestackClient {

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
  public void testConstructors() {
    NetworkBehavior behavior = NetworkBehavior.create();
    MockFsUploadService mockUploadService = createMockUploadService(behavior);

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");

    FilestackClient client1 = new FilestackClient("apiKey");
    FilestackClient client2 = new FilestackClient("apiKey", mockUploadService);
    FilestackClient client3 = new FilestackClient("apiKey", security);
    FilestackClient client4 = new FilestackClient("apiKey", security, mockUploadService);
  }

  @Test
  public void testUpload() throws Exception {
    NetworkBehavior behavior = NetworkBehavior.create();
    MockFsUploadService mockUploadService = createMockUploadService(behavior);

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");

    FilestackClient client = new FilestackClient("apiKey", security, mockUploadService);

    behavior.setFailurePercent(0);
    behavior.setDelay(0, TimeUnit.SECONDS);

    Path path = createRandomFile(10 * 1024 * 1024);
    UploadOptions options = new UploadOptions.Builder().build();

    Assert.assertEquals("handle", client.upload(path.toString(), 0).getHandle());
    Assert.assertEquals("handle", client.upload(path.toString(), options, 0).getHandle());

    Files.delete(path);
  }
}
