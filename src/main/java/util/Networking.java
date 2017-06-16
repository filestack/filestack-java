package util;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Serves as singleton holder for the OkHttp client and Retrofit services.
 * We only want to instantiate these classes once per app.
 */
public class Networking {
    private static OkHttpClient httpClient;
    private static CdnService cdnService;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder().build();
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
     * The retrofit services will be recreated to use this client after calling.
     *
     * @param httpClient Customized OkHttpClient
     */
    public static void setHttpClient(OkHttpClient httpClient) {
        Networking.httpClient = httpClient;
        Networking.cdnService = null;
    }

    /**
     * The client your customized service uses should be the same one stored in this class.
     * You should call setHttpClient before calling this method if you're also using a custom httpClient or
     * you should use the client returned from {@link #getHttpClient() getHttpClient()} when building your service.
     *
     * @param CdnService Customized cdnService
     */
    public static void setCdnService(CdnService CdnService) {
        Networking.cdnService = CdnService;
    }
}
