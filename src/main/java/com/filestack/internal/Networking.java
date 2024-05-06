package org.filestack.internal;

import com.google.gson.Gson;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Holds {@link OkHttpClient} singletons.
 * We only want to instantiate these classes once per app.
 */
public final class Networking {

  private static BaseService baseService;
  private static CdnService cdnService;
  private static UploadService uploadService;
  private static CloudService cloudService;

  private static final NetworkClient networkClient = new NetworkClient(buildOkHtttpClient(), new Gson());

  /** Get http fsClient singleton. */
  private static OkHttpClient buildOkHtttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .addInterceptor(new HeaderInterceptor())
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false);
    setTls12Support(builder);
    return builder.build();
  }

  private static void setTls12Support(OkHttpClient.Builder builder) {
    try {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null);
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
        throw new IllegalStateException("Unexpected default trust managers:"
            + Arrays.toString(trustManagers));
      }

      X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
      SSLContext sc = SSLContext.getInstance("TLSv1.2");
      sc.init(null, null, null);
      builder.sslSocketFactory(new TlsSocketFactory(sc.getSocketFactory()), trustManager);

      ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
          .tlsVersions(TlsVersion.TLS_1_2)
          .build();

      List<ConnectionSpec> specs = new ArrayList<>();
      specs.add(cs);
      specs.add(ConnectionSpec.COMPATIBLE_TLS);

      builder.connectionSpecs(specs);
    } catch (Exception exc) {
      //fail silently for now
    }
  }

  /**
   * Get {@link BaseService} singleton.
   */
  public static BaseService getBaseService() {
    if (baseService == null) {
      baseService = new BaseService(networkClient);
    }
    return baseService;
  }

  /**
   * Get {@link CdnService} singleton.
   */
  public static CdnService getCdnService() {
    if (cdnService == null) {
      cdnService = new CdnService(networkClient);
    }
    return cdnService;
  }


  /**
   * Get {@link UploadService} singleton.
   */
  public static UploadService getUploadService() {
    if (uploadService == null) {
      uploadService = new UploadService(networkClient);
    }
    return uploadService;
  }

  /**
   * Get {@link CloudService} singleton.
   */
  public static CloudService getCloudService() {
    if (cloudService == null) {
      cloudService = new CloudService(networkClient, new Gson());
    }
    return cloudService;
  }

  private Networking() {

  }
}
