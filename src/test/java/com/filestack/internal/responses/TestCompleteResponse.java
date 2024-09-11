package org.filestack.internal.responses;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

public class TestCompleteResponse {

  @Test
  public void test() {
    Gson gson = new Gson();

    String jsonString = "{"
        + "'url' : '<url>',"
        + "'handle' : '<handle>',"
        + "'filename' : '<filename>',"
        + "'size' : '0',"
        + "'mimetype' : '<mimetype>'"
        + "}";

    CompleteResponse response = gson.fromJson(jsonString, CompleteResponse.class);

    Assert.assertEquals("<url>", response.getUrl());
    Assert.assertEquals("<handle>", response.getHandle());
    Assert.assertEquals("<filename>", response.getFilename());
    Assert.assertEquals(0, response.getSize());
    Assert.assertEquals("<mimetype>", response.getMimetype());
  }
}
