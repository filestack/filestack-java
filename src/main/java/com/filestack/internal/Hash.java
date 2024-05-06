package org.filestack.internal;

import okio.ByteString;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hash {
  private Hash() {

  }

  /**
   * Computes MD5 hash out of provided array of bytes.
   * @param bytes - the array of bytes
   */
  @Nullable
  public static byte[] md5(byte[] bytes) {
    return md5(bytes, 0, bytes.length);
  }

  /**
   * Computes MD5 hash out of provided array of bytes.
   * @param bytes - the array of bytes
   * @param offset - the offset to start from in the array of bytes.
   * @param length - the number of bytes to use, starting at offset.
   */
  @Nullable
  public static byte[] md5(byte[] bytes, int offset, int length) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(bytes, offset, length);
      return md.digest();
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }

  /**
   * Computes hash using HmacSHA256 algorithm.
   * @param key - key used to sign the message
   * @param message - message to hash
   */
  @Nullable
  public static String hmacSha256(byte[] key, byte[] message) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(key, "HmacSHA256"));
      byte[] hash = mac.doFinal(message);
      return ByteString.of(hash).hex();
    } catch (InvalidKeyException e) {
      return null;
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}
