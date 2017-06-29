package util;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Serves as singleton holder for the OkHttp client and Retrofit services.
 * We only want to instantiate these classes once per app.
 */
public class Networking {
    private static OkHttpClient httpClient;
    private static FilestackService.Cdn cdnService;
    private static FilestackService.Api apiService;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor())
                    .build();
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

    public static void setCustomClient(OkHttpClient client) {
        httpClient = client;
        invalidate();
    }

    /**
     * Sets the services to null so they'll be recreated.
     */
    private static void invalidate() {
        cdnService = null;
        apiService = null;
    }
}
