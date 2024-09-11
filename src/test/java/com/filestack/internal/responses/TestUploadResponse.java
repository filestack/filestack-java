package org.filestack.internal.responses;

import com.google.gson.Gson;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestUploadResponse {

  @Test
  public void test() {
    Gson gson = new Gson();

    String jsonString = "{"
        + "'url' : '<url>',"
        + "'headers' : {"
        + "'Authorization' : '<authorization>',"
        + "'Content-MD5' : '<content_md5>',"
        + "'x-amz-content-sha256' : '<x_amz_content_sha256>',"
        + "'x-amz-date' : '<x_amz_date>',"
        + "'x-amz-acl' : '<x_amz_acl>'"
        + "},"
        + "'location_url' : '<location_url>'"
        + "}";

    UploadResponse response = gson.fromJson(jsonString, UploadResponse.class);
    Map<String, String> headers = response.getS3Headers();

    Assert.assertEquals("<url>", response.getUrl());
    Assert.assertEquals("<location_url>", response.getLocationUrl());
    Assert.assertEquals("<authorization>", headers.get("Authorization"));
    Assert.assertEquals("<content_md5>", headers.get("Content-MD5"));
    Assert.assertEquals("<x_amz_content_sha256>", headers.get("x-amz-content-sha256"));
    Assert.assertEquals("<x_amz_date>", headers.get("x-amz-date"));
    Assert.assertEquals("<x_amz_acl>", headers.get("x-amz-acl"));
  }
}
