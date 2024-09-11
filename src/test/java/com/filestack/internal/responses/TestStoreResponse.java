package org.filestack.internal.responses;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

public class TestStoreResponse {

  @Test
  public void test() {
    Gson gson = new Gson();

    String jsonString = "{"
        + "'url' : '<url>',"
        + "'filename' : '<filename>',"
        + "'type' : '<type>',"
        + "'container' : '<container>',"
        + "'key' : '<key>',"
        + "'width' : '0',"
        + "'height' : '0',"
        + "'size' : '0'"
        + "}";

    StoreResponse response = gson.fromJson(jsonString, StoreResponse.class);

    Assert.assertEquals("<url>", response.getUrl());
    Assert.assertEquals("<filename>", response.getFilename());
    Assert.assertEquals("<type>", response.getType());
    Assert.assertEquals("<container>", response.getContainer());
    Assert.assertEquals("<key>", response.getKey());
    Assert.assertEquals(0, response.getWidth());
    Assert.assertEquals(0, response.getHeight());
    Assert.assertEquals(0, response.getSize());
  }
}
