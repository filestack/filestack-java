package com.filestack.util;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import java.io.IOException;
import retrofit2.Response;

/**
 * Abstract class to generalize retry logic of a network call.
 *
 * @param <T> type of object to return
 */
public abstract class RetryNetworkFunc<T> {
  private final int maxNetworkRetries;
  private final int maxServerRetries;
  private final int delayBase;

  private int networkRetries;
  private int serverRetries;

  /**
   * Constructs an instance following the given settings.
   * The network call is made in {@link #work()}.
   * Override {@link #process(Response)} to customize parsing the response.
   *
   * @param maxNetworkRetries times to retry after a network failure
   * @param maxServerRetries  times to retry after an error response from the server
   * @param delayBase         base for exponential backoff, delay (seconds) == base ^ retryCount
   */
  public RetryNetworkFunc(int maxNetworkRetries, int maxServerRetries, int delayBase) {
    this.maxNetworkRetries = maxNetworkRetries;
    this.maxServerRetries = maxServerRetries;
    this.delayBase = delayBase;
  }

  /** Start the request. */
  public T call() throws Exception {

    Response response = run();
    return process(response);
  }

  /**
   * Orchestrates calls to {@link #work()}, {@link #retryNetwork()}, {@link #retryServer(int)}.
   */
  Response run() throws Exception {

    Response response;

    try {
      response = work();
    } catch (Exception e) {
      response = retryNetwork();
    }

    if (response.code() != 200) {
      response = retryServer(response.code());
    }

    return response;
  }

  /** Contains the actual network call. */
  abstract Response work() throws Exception;

  /** Handles retry logic for network failures. */
  Response retryNetwork() throws Exception {

    if (networkRetries >= maxNetworkRetries) {
      throw new IOException();
    }

    if (delayBase > 0) {
      try {
        Thread.sleep((long) Math.pow(delayBase, networkRetries) * 1000);
      } catch (InterruptedException e) {
        networkRetries++;
      }
    }

    networkRetries++;
    return run();
  }

  /** Handles retry logic for server errors. */
  Response retryServer(int code) throws Exception {

    // Don't retry for any of these statuses
    if (code == 206) {
      throw new InternalException();
    } else if (code == 400) {
      throw new InvalidParameterException();
    } else if (code == 403) {
      throw new PolicySignatureException();
    }

    // Do retry otherwise, up to the max
    if (serverRetries >= maxServerRetries) {
      throw new InternalException();
    }

    if (delayBase > 0) {
      try {
        Thread.sleep((long) Math.pow(delayBase, serverRetries) * 1000);
      } catch (InterruptedException e) {
        serverRetries++;
      }
    }

    serverRetries++;
    return run();
  }

  @SuppressWarnings("unchecked")
  /** Process the response to get a return value. */
  T process(Response response) throws Exception {
    return (T) response.body();
  }

  public int getNetworkRetries() {
    return networkRetries;
  }

  public int getServerRetries() {
    return serverRetries;
  }
}
