package org.filestack.internal;

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

    Response<T> response = run();
    return process(response);
  }

  /**
   * Orchestrates calling {@link #work()} and retrying on failure.
   */
  private Response<T> run() throws Exception {
    Response<T> response = null;
    Exception exception = null;

    while (networkRetries <= maxNetworkRetries && serverRetries <= maxServerRetries) {

      try {
        response = work();
      } catch (Exception e) {
        exception = e;
        onNetworkFail(networkRetries);
        continue;
      }

      if (!responseOkay(response)) {
        onServerFail(serverRetries);
      } else {
        break;
      }
    }

    if (networkRetries > maxNetworkRetries) {
      throw exception;
    } else if (serverRetries > maxServerRetries) {
      Util.throwHttpResponseException(response);
    }

    return response;
  }

  /** Contains the actual network call. */
  abstract Response<T> work() throws Exception;

  /** Process the response to get a return value. */
  T process(Response<T> response) {
    return response.getData();
  }

  /** Called for network failures. */
  public void onNetworkFail(int retries) throws Exception {
    networkRetries = sleep(retries);
  }

  /** Called for server failures. */
  public void onServerFail(int retries) throws Exception {
    serverRetries = sleep(retries);
  }

  /**
   * Causes thread to sleep for delayBase ^ count seconds.
   *
   * @param count power to raise delayBase to
   * @return new count value
   */
  private int sleep(int count) {
    if (!Util.isUnitTest()) {
      try {
        Thread.sleep((long) Math.pow(delayBase, count) * 1000);
      } catch (InterruptedException e) {
        count++;
      }
    }
    return ++count;
  }

  /**
   * Checks code of {@link #work()} response.
   *
   * @param response from {@link #work()}
   * @return true for success response, false otherwise
   * @throws Exception if failed response and not recoverable
   */
  private boolean responseOkay(Response response) throws Exception {
    int code = response.code();

    // Immediately stop and throw exception for these codes
    if (code == 206 || code == 400 || code == 403) {
      Util.throwHttpResponseException(response);
    }

    // Possibly return false but don't throw exception so we keep retrying
    return code == 200;
  }

  public int getNetworkRetries() {
    return networkRetries;
  }

  public int getServerRetries() {
    return serverRetries;
  }
}
