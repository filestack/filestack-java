package util;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit interfaces wrapping Filestack API.
 */
public class FilestackService {

    public interface Api {
        String URL = "https://www.filestackapi.com/api/file/";

        @POST("{handle}")
        Call<ResponseBody> overwrite(@Path("handle") String handle, @Query("policy") String policy,
                                     @Query("signature") String signature, @Body RequestBody body);

        @DELETE("{handle}")
        Call<ResponseBody> delete(@Path("handle") String handle, @Query("key") String key,
                                  @Query("policy") String policy, @Query("signature") String signature);
    }

    public interface Cdn {
        String URL = "https://cdn.filestackcontent.com/";

        @GET("{handle}")
        @Streaming
        Call<ResponseBody> get(@Path("handle") String handle, @Query("policy") String policy,
                               @Query("signature") String signature);
    }
}
