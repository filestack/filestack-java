package com.filestack.util;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;

public class TestNetworking extends Networking {

  @Test
  public void testGets() {
    Networking.invalidate();
    Assert.assertNotNull(Networking.getHttpClient());
    Networking.invalidate();
    Assert.assertNotNull(Networking.getFsApiService());
    Networking.invalidate();
    Assert.assertNotNull(Networking.getFsCdnService());
    Networking.invalidate();
    Assert.assertNotNull(Networking.getFsUploadService());
  }

  @Test
  public void testInvalidate() {
    OkHttpClient okHttpClient = Networking.getHttpClient();
    Networking.invalidate();
    Assert.assertNotSame(okHttpClient, Networking.getHttpClient());

    FsApiService fsApiService = Networking.getFsApiService();
    Networking.invalidate();
    Assert.assertNotSame(fsApiService, Networking.getFsApiService());

    FsCdnService fsCdnService = Networking.getFsCdnService();
    Networking.invalidate();
    Assert.assertNotSame(fsCdnService, Networking.getFsCdnService());

    FsUploadService fsUploadService = Networking.getFsUploadService();
    Networking.invalidate();
    Assert.assertNotSame(fsUploadService, Networking.getFsUploadService());
  }

  @Test
  public void testSetClient() {
    OkHttpClient okHttpClient = new OkHttpClient();
    Networking.setCustomClient(okHttpClient);
    Assert.assertSame(okHttpClient, Networking.getHttpClient());
  }

  @Test
  public void testSetClientNull() {
    OkHttpClient okHttpClient = Networking.getHttpClient();
    Networking.setCustomClient(null);
    Assert.assertSame(okHttpClient, Networking.getHttpClient());
  }

  @Test
  public void testRemoveClient() {
    OkHttpClient okHttpClient = new OkHttpClient();
    Networking.setCustomClient(okHttpClient);
    Networking.invalidate();
    Assert.assertNotSame(okHttpClient, Networking.getHttpClient());
  }
}
