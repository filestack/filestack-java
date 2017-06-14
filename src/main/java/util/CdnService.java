package util;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Retrofit wrapper interface to Filestack endpoints served by CDN.
 */
public interface CdnService {
    String URL = "https://cdn.filestackcontent.com/";

    @GET("{handle}")
    @Streaming
    Call<ResponseBody> get(@Path("handle") String handle);
}
