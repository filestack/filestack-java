package org.filestack.internal.responses;

import com.google.gson.Gson;

import java.util.Map;

import okhttp3.RequestBody;
import org.junit.Assert;
import org.junit.Test;

public class TestStartResponse {

  @Test
  public void test() {
    Gson gson = new Gson();

    String jsonString = "{"
        + "'uri' : '<uri>',"
        + "'region' : '<region>',"
        + "'location_url' : '<location_url>',"
        + "'upload_id' : '<upload_id>',"
        + "'upload_type' : 'intelligent_ingestion'"
        + "}";

    StartResponse response = gson.fromJson(jsonString, StartResponse.class);
    Map<String, RequestBody> params = response.getUploadParams();

    Assert.assertTrue(params.containsKey("uri"));
    Assert.assertTrue(params.containsKey("region"));
    Assert.assertTrue(params.containsKey("upload_id"));
    Assert.assertTrue(response.isIntelligent());
  }
}
