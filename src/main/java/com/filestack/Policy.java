package com.filestack;

import java.util.ArrayList;
import java.util.Date;

/**
 * Convenience class to make creating policies easier.
 * See https://www.filestack.com/docs/security/creating-policies for information about policies themselves.
 */
public class Policy {
  public static final String CALL_PICK = "pick";
  public static final String CALL_READ = "read";
  public static final String CALL_STAT = "stat";
  public static final String CALL_WRITE = "write";
  public static final String CALL_WRITE_URL = "writeUrl";
  public static final String CALL_STORE = "store";
  public static final String CALL_CONVERT = "convert";
  public static final String CALL_REMOVE = "remove";
  public static final String CALL_EXIF = "exif";

  private Long expiry;
  private String[] call;
  private String handle;
  private String url;
  private Integer maxSize;
  private Integer minSize;
  private String path;
  private String container;

  private Policy() {

  }

  public static class Builder {
    private Long expiry;
    private ArrayList<String> calls;
    private String handle;
    private String url;
    private Integer maxSize;
    private Integer minSize;
    private String path;
    private String container;

    public Builder expiry(Long expiry) {
      this.expiry = expiry;
      return this;
    }

    /**
     * Permits the policy to make the given call.
     * For example you can add a permission to "read" or "remove".
     *
     * @param call name of call to allow
     */
    public Builder addCall(String call) {
      if (calls == null) {
        calls = new ArrayList<>();
      }
      calls.add(call);
      return this;
    }

    public Builder handle(String handle) {
      this.handle = handle;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder maxSize(Integer maxSize) {
      this.maxSize = maxSize;
      return this;
    }

    public Builder minSize(Integer minSize) {
      this.minSize = minSize;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder container(String container) {
      this.container = container;
      return this;
    }

    /**
     * Convenience method to create a policy with full access and one year expiry.
     */
    public Builder giveFullAccess() {
      // Set expiry to one year from now
      Date date = new Date();
      expiry = date.getTime() / 1000 + 60 * 60 * 24 * 365;

      // Add all calls
      calls = new ArrayList<>();
      calls.add(CALL_PICK);
      calls.add(CALL_READ);
      calls.add(CALL_STAT);
      calls.add(CALL_WRITE);
      calls.add(CALL_WRITE_URL);
      calls.add(CALL_STORE);
      calls.add(CALL_CONVERT);
      calls.add(CALL_REMOVE);
      calls.add(CALL_EXIF);

      return this;
    }

    /**
     * Construct and return the policy.
     */
    public Policy build() {
      Policy policy = new Policy();
      policy.expiry = expiry;
      if (calls != null) {
        policy.call = calls.toArray(new String[0]);
      }
      policy.handle = handle;
      policy.url = url;
      policy.maxSize = maxSize;
      policy.minSize = minSize;
      policy.path = path;
      policy.container = container;
      return policy;
    }
  }
}
