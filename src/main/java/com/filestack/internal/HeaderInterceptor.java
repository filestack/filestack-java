package org.filestack.internal;

import org.filestack.FilestackBuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Locale;

/**
 * Intercepts requests to add Filestack headers.
 */
public class HeaderInterceptor implements Interceptor {
  static String HEADER_USER_AGENT = "User-Agent";
  static String HEADER_FILESTACK_SOURCE = "Filestack-Source";

  static String VERSION = FilestackBuildConfig.VERSION;
  static String USER_AGENT = String.format(Locale.ROOT, "filestack-java %s", VERSION);
  static String FILESTACK_SOURCE = String.format(Locale.ROOT, "Java-%s", VERSION);

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request original = chain.request();

    Request modified = original.newBuilder()
        .addHeader(HEADER_USER_AGENT, USER_AGENT)
        .addHeader(HEADER_FILESTACK_SOURCE, FILESTACK_SOURCE)
        .build();

    return chain.proceed(modified);
  }
}
