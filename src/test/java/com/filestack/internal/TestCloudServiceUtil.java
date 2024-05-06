package org.filestack.internal;

import org.filestack.Config;
import org.filestack.StorageOptions;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test that the structure of the request JSON is correct.
 */
public class TestCloudServiceUtil {

  @Test
  public void testBuildBaseJson() {
    Config config = new Config("a", "b", "c", "d");
    String session = "e";
    JsonObject json = CloudServiceUtil.buildBaseJson(config, session, null);

    assertEquals("a", json.get("apikey").getAsString());
    assertEquals("c", json.get("policy").getAsString());
    assertEquals("d", json.get("signature").getAsString());
    assertEquals("e", json.get("token").getAsString());

    assertEquals("mobile", json.get("flow").getAsString());
    assertTrue(json.has("clouds"));
  }

  @Test
  public void testAddCloudJson() {
    Config config = new Config("a", "b", "c", "d");
    String session = "e";

    JsonObject json = CloudServiceUtil.buildBaseJson(config, session, null);

    CloudServiceUtil.addCloudJson(json, "f", "g", "h");

    JsonObject cloud = json.getAsJsonObject("clouds").getAsJsonObject("f");
    assertEquals("g", cloud.get("path").getAsString());
    assertEquals("h", cloud.get("next").getAsString());
  }

  @Test
  public void testAddStorageJson() {
    Config config = new Config("a", "b", "c", "d");
    String session = "e";

    JsonObject json = CloudServiceUtil.buildBaseJson(config, session, null);
    CloudServiceUtil.addCloudJson(json, "f", "g", null);

    JsonObject cloud = json.getAsJsonObject("clouds").getAsJsonObject("f");
    assertFalse(cloud.has("next"));

    StorageOptions options = new StorageOptions.Builder()
        .container("i")
        .region("j")
        .build();
    CloudServiceUtil.addStorageJson(json, "f", options);

    JsonObject storeJson = json.getAsJsonObject("clouds")
        .getAsJsonObject("f")
        .getAsJsonObject("store");
    assertEquals("i", storeJson.get("container").getAsString());
    assertEquals("j", storeJson.get("region").getAsString());
  }
}
