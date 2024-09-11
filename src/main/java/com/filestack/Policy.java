package org.filestack;

import org.filestack.internal.Hash;
import org.filestack.internal.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Determines what access a user is allowed (if account security is enabled). A policy sets access
 * and a signature validates the policy. This class should only be used in server-side code. Do not
 * include your app secret in client-side code.
 * @see <a href="https://www.filestack.com/docs/security/creating-policies">Filestack Docs - Creating policies</a>
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

  private final String encodedPolicy;
  private final String signature;

  private Policy(String encodedPolicy, String signature) {
    this.encodedPolicy = encodedPolicy;
    this.signature = signature;
  }

  // Javadoc comments adapted from
  // https://www.filestack.com/docs/security/creating-policies

  /**
   * Builds new {@link Policy}.
   */
  public static class Builder {
    private Long expiry;
    private String[] calls;
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
     * @param calls can be pick, read, stat, write, writeUrl, store, convert, remove, or exif
     */
    public Builder calls(String... calls) {
      this.calls = calls;
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
      calls = new String[] { CALL_PICK, CALL_READ, CALL_STAT, CALL_WRITE, CALL_WRITE_URL,
          CALL_STORE, CALL_CONVERT, CALL_REMOVE, CALL_EXIF };

      return this;
    }

    /**
     * Encodes the json policy and signs it using the app secret.
     * Do not include the app secret in client-side code.
     */
    public Policy build(String appSecret) {
      String jsonPolicy = buildJsonPolicy();
      String encodedPolicy = Util.base64Url(jsonPolicy.getBytes());
      String signature = Hash.hmacSha256(
          appSecret.getBytes(Charset.forName("UTF-8")),
          encodedPolicy.getBytes(Charset.forName("UTF-8"))
      );
      return new Policy(encodedPolicy, signature);
    }

    private String buildJsonPolicy() {
      JsonObject json = new JsonObject();
      Util.addIfNotNull(json, "expiry", expiry);
      if (calls != null) {
        JsonArray callsArray = new JsonArray();
        for (String call : calls) {
          callsArray.add(call);
        }
        json.add("calls", callsArray);
      }
      Util.addIfNotNull(json, "handle", handle);
      Util.addIfNotNull(json, "url", url);
      Util.addIfNotNull(json, "maxSize", maxSize);
      Util.addIfNotNull(json, "minSize", minSize);
      Util.addIfNotNull(json, "path", path);
      Util.addIfNotNull(json, "container", container);
      return json.toString();
    }
  }

  public String getEncodedPolicy() {
    return encodedPolicy;
  }

  public String getSignature() {
    return signature;
  }
}
