package com.filestack.util.responses;

import java.util.Map;

/**
 * Tags returned from Google Vision API.
 *
 * @see <a href="https://www.filestack.com/docs/tagging"></a>
 */
public class ImageTagResponse {
  Tags tags;

  class Tags {
    Map<String, Integer> auto;
    Map<String, Integer> user;
  }

  public Map<String, Integer> getAuto() {
    return tags.auto;
  }

  public Map<String, Integer> getUser() {
    return tags.user;
  }
}
