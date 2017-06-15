package util;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Serves as singleton holder for the OkHttp client and Retrofit services.
 * We only want to instantiate these classes once per app.
 */
public class Networking {
    private static boolean mockMode = false;

    private static OkHttpClient httpClient;
    private static CdnService cdnService;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (mockMode) {
                builder.addInterceptor(new MockInterceptor());
            }
            httpClient = builder.build();
        }
        return httpClient;
    }

    public static CdnService getCdnService() {
        if (cdnService == null) {
            OkHttpClient client = getHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(CdnService.URL)
                    .build();
            cdnService = retrofit.create(CdnService.class);
        }
        return cdnService;
    }

    /**
     * Sets the httpClient to intercept requests and return mock responses for testing.
     */
    public static void setMockMode(boolean mockMode) {
        if (Networking.mockMode != mockMode) {
            Networking.mockMode = mockMode;
            invalidate();
        }
    }

    /**
     * Sets the client and services to null so they'll be recreated with updated settings.
     */
    private static void invalidate() {
        httpClient = null;
        cdnService = null;
    }
}
