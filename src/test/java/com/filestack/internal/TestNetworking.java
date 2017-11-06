package com.filestack.internal;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;

public class TestNetworking extends Networking {

  @Test
  public void testCreation() {
    Assert.assertNotNull(Networking.getHttpClient());
    Assert.assertNotNull(Networking.getBaseService());
    Assert.assertNotNull(Networking.getCdnService());
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

    CloudService cloudService = Networking.getCloudService();
    Networking.invalidate();
    Assert.assertNotSame(cloudService, Networking.getCloudService());
  }
}
