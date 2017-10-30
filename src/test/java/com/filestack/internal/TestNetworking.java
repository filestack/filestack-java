package com.filestack.internal;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;

public class TestNetworking extends Networking {

  @Test
  public void testGets() {
    Networking.invalidate();
    Assert.assertNotNull(Networking.getHttpClient());
    Networking.invalidate();
    Assert.assertNotNull(Networking.getBaseService());
    Networking.invalidate();
    Assert.assertNotNull(Networking.getCdnService());
    Networking.invalidate();
    Assert.assertNotNull(Networking.getUploadService());
  }

  @Test
  public void testInvalidate() {
    OkHttpClient okHttpClient = Networking.getHttpClient();
    Networking.invalidate();
    Assert.assertNotSame(okHttpClient, Networking.getHttpClient());

    BaseService baseService = Networking.getBaseService();
    Networking.invalidate();
    Assert.assertNotSame(baseService, Networking.getBaseService());

    CdnService cdnService = Networking.getCdnService();
    Networking.invalidate();
    Assert.assertNotSame(cdnService, Networking.getCdnService());

    UploadService uploadService = Networking.getUploadService();
    Networking.invalidate();
    Assert.assertNotSame(uploadService, Networking.getUploadService());
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
