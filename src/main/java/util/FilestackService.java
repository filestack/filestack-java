package util;

import com.google.gson.JsonObject;
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

    public interface Process {
        String URL = "https://process.filestackapi.com/";

        // Requests that use a handle
        @Streaming
        @GET("{tasks}/{handle}")
        Call<ResponseBody> get(@Path("tasks") String tasks, @Path("handle") String handle);

        @GET("debug/{tasks}/{handle}")
        Call<JsonObject> debug(@Path("tasks") String tasks, @Path("handle") String handle);

        @POST("{tasks}/{handle}")
        Call<StoreResponse> store(@Path("tasks") String tasks, @Path("handle") String handle);

        // Requests that use an API key and external URL
        @Streaming
        @GET("{key}/{tasks}/{url}")
        Call<ResponseBody> getExternal(@Path("key") String key, @Path("tasks") String tasks,
                                       @Path("url") String url);

        @GET("{key}/debug/{tasks}/{url}")
        Call<JsonObject> debugExternal(@Path("key") String key, @Path("tasks") String tasks,
                                       @Path("url") String url);

        @POST("{key}/{tasks}/{url}")
        Call<StoreResponse> storeExternal(@Path("key") String key, @Path("tasks") String tasks,
                                          @Path("url") String url);

        public static class StoreResponse {
            String url;
            String filename;
            String type;

            String container;
            String key;

            int width;
            int height;
            int size;

            public String getUrl() {
                return url;
            }

            public String getFilename() {
                return filename;
            }

            public String getType() {
                return type;
            }

            public String getContainer() {
                return container;
            }

            public String getKey() {
                return key;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public int getSize() {
                return size;
            }
        }
    }
}
