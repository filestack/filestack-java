package util;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Serves as singleton holder for the OkHttp client and Retrofit services.
 * We only want to instantiate these classes once per app.
 */
public class Networking {
    private static boolean mockMode = false;

    private static OkHttpClient httpClient;
    private static FilestackService.Cdn cdnService;
    private static FilestackService.Api apiService;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new HeaderInterceptor());
            if (mockMode) {
                builder.addInterceptor(new MockInterceptor());
            }
            httpClient = builder.build();
        }
        return httpClient;
    }

    public static FilestackService.Cdn getCdnService() {
        if (cdnService == null) {
            OkHttpClient client = getHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(FilestackService.Cdn.URL)
                    .build();
            cdnService = retrofit.create(FilestackService.Cdn.class);
        }
        return cdnService;
    }

    public static FilestackService.Api getApiService() {
        if (apiService == null) {
            OkHttpClient client = getHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(FilestackService.Api.URL)
                    .build();
            apiService = retrofit.create(FilestackService.Api.class);
        }
        return apiService;
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
        apiService = null;
    }
}
