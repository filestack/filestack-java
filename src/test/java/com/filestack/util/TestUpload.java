package com.filestack.util;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
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

  private static MockUploadService createMockUploadService(NetworkBehavior behavior) {

    Retrofit retrofit = new Retrofit.Builder()
        .client(new OkHttpClient())
        .baseUrl(FilestackUploadService.URL)
        .build();

    MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
        .networkBehavior(behavior)
        .build();

    BehaviorDelegate<FilestackUploadService> delegate =
        mockRetrofit.create(FilestackUploadService.class);
    return new MockUploadService(delegate);
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
  public void test() throws Exception {
    Path path = createRandomFile(10 * 1024 * 1024);

    NetworkBehavior behavior = NetworkBehavior.create();
    MockUploadService mockUploadService = createMockUploadService(behavior);

    FilestackClient fsClient = new FilestackClient("apiKey");
    UploadOptions options = new UploadOptions.Builder().build();
    Upload upload = new Upload(path.toString(), fsClient, options, mockUploadService, 0);

    behavior.setDelay(0, TimeUnit.SECONDS);

    FileLink fileLink = upload.run();

    Assert.assertTrue(fileLink.getHandle().equals("handle"));

    Files.delete(path);
  }
}
