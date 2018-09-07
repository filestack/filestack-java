package com.filestack.internal;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Intercepts requests to add Filestack headers.
 */
public class HeaderInterceptor implements Interceptor {
  public static String HEADER_USER_AGENT = "User-Agent";
  public static String HEADER_FILESTACK_SOURCE = "Filestack-Source";

  public static String USER_AGENT = "filestack-java %s";
  public static String FILESTACK_SOURCE = "Java-%s";

  private String version;

  HeaderInterceptor() {
    this.version = Util.getVersion();
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request original = chain.request();

    Request modified = original.newBuilder()
        .addHeader(HEADER_USER_AGENT, String.format(USER_AGENT, version))
        .addHeader(HEADER_FILESTACK_SOURCE, String.format(FILESTACK_SOURCE, version))
        .build();

    return chain.proceed(modified);
  }
}
