package com.filestack;

import java.util.ArrayList;
import java.util.Date;

/**
 * Determines what access a user is allowed (if account security is enabled).
 * A policy sets access and a signature validates the policy.
 * @see <a href="https://www.filestack.com/docs/security">Filestack Security Docs</a>
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

  // Javadoc comments adapted from
  // https://www.filestack.com/docs/security/creating-policies

  /**
   * Builds new {@link Policy}.
   */
  public static class Builder {
    private Long expiry;
    private ArrayList<String> calls;
    private String handle;
    private String url;
    private Integer maxSize;
    private Integer minSize;
    private String path;
    private String container;

    /**
     * Set when the policy will expire.
     *
     * @param expiry in UNIX time
     */
    public Builder expiry(Long expiry) {
      this.expiry = expiry;
      return this;
    }

    /**
     * Add a call that this policy is allowed to make. A policy without any call permissions
     * specified is permitted to make all calls except for exif, which needs to be explicitly
     * included in a policy in order to be allowed.
     *
     * @param call can be pick, read, stat, write, writeUrl, store, convert, remove, or exif
     */
    public Builder addCall(String call) {
      if (calls == null) {
        calls = new ArrayList<>();
      }
      calls.add(call);
      return this;
    }

    /**
     * Restrict access to a single file handle for all calls that act on a specific handle.
     */
    public Builder handle(String handle) {
      this.handle = handle;
      return this;
    }

    /**
     * Restrict external transformations to certain URLs.
     *
     * @param regex regular expression matching allowed urls
     */
    public Builder url(String regex) {
      this.url = regex;
      return this;
    }

    /**
     * Set the max file size in bytes that can be uploaded. Default is no limit.
     */
    public Builder maxSize(Integer maxSize) {
      this.maxSize = maxSize;
      return this;
    }

    /**
     * Set the min file size in bytes that can be uploaded. Defaults to 0.
     */
    public Builder minSize(Integer minSize) {
      this.minSize = minSize;
      return this;
    }

    /**
     * Restrict paths a file can be uploaded to. Prevents a user from storing files to different
     * paths. Does not prevent a user from reading content from different paths.
     * Defaults to allowing any path ('.*').
     *
     * @param path regular expression matching allowed paths
     */
    public Builder path(String path) {
      this.path = path;
      return this;
    }

    /**
     * Restrict containers a file can be uploaded to. Prevents a user from storing files to
     * different containers. Does not prevent a user from reading content from different
     * containers. Defaults to allowing any path ('.*').
     *
     * @param container regular expression matching allowed paths
     */
    public Builder container(String container) {
      this.container = container;
      return this;
    }

    /**
     * Give the policy full access and a one year expiry.
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
     * Create the {@link Policy} instance using the configured values.
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
