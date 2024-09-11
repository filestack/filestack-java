package org.filestack.internal.responses;

import com.google.gson.Gson;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class TestImageTagResponse {

  @Test
  public void test() {
    Gson gson = new Gson();

    String jsonString = "{"
        + "'tags': {"
        + "'auto': {"
        + "'accipitriformes': 58,"
        + "'beak': 90,"
        + "'bird': 97,"
        + "'bird of prey': 95,"
        + "'fauna': 84,"
        + "'great grey owl': 89,"
        + "'hawk': 66,"
        + "'owl': 97,"
        + "'vertebrate': 92,"
        + "'wildlife': 81"
        + "},"
        + "'user': null"
        + "}"
        + "}";

    ImageTagResponse imageTags = gson.fromJson(jsonString, ImageTagResponse.class);

    Assert.assertNotNull(imageTags.getAuto());
    Assert.assertNull(imageTags.getUser());
    Map<String, Integer> tags = imageTags.getAuto();
    Assert.assertEquals(10, tags.size());
    Assert.assertEquals((Integer) 58, tags.get("accipitriformes"));
  }
}
