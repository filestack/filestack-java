package util;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.HashMap;
import java.util.Map;

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

    public interface Upload {
        String URL = "https://upload.filestackapi.com/";

        @Multipart
        @POST("/multipart/start")
        Call<StartResponse> start(@PartMap Map<String, RequestBody> parameters);

        @Multipart
        @POST("/multipart/upload")
        Call<UploadResponse> upload(@PartMap Map<String, RequestBody> parameters);

        @PUT
        Call<ResponseBody> uploadS3(@HeaderMap Map<String, String> headers, @Url String url, @Body RequestBody body);

        @Multipart
        @POST("/multipart/commit")
        Call<ResponseBody> commit(@PartMap Map<String, RequestBody> parameters);

        @Multipart
        @POST("/multipart/complete")
        Call<CompleteResponse> complete(@PartMap Map<String, RequestBody> parameters);

        class StartResponse {
            private String uri;
            private String region;
            @SerializedName("location_url")
            private String locationUrl;
            @SerializedName("upload_id")
            private String uploadId;
            @SerializedName("upload_type")
            private String uploadType;

            public Map<String, RequestBody> getUploadParams() {
                HashMap<String, RequestBody> parameters = new HashMap<>();
                parameters.put("uri", Util.createStringPart(uri));
                parameters.put("region", Util.createStringPart(region));
                parameters.put("upload_id", Util.createStringPart(uploadId));
                return parameters;
            }

            public boolean isIntelligent() {
                return uploadType != null && uploadType.equals("intelligent_ingestion");
            }
        }

        public class UploadResponse {
            private String url;
            @SerializedName("location_url")
            private String locationUrl;
            @SerializedName("headers")
            private S3Headers s3Headers;

            public String getUrl() {
                return url;
            }

            public String getLocationUrl() {
                return locationUrl;
            }

            public Map<String, String> getS3Headers() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", s3Headers.auth);
                if (s3Headers.acl != null)
                    headers.put("x-amz-acl", s3Headers.acl);
                headers.put("Content-MD5", s3Headers.md5);
                headers.put("x-amz-content-sha256", s3Headers.sha256);
                headers.put("x-amz-date", s3Headers.date);

                return headers;
            }

            private class S3Headers {
                @SerializedName("Authorization")
                private String auth;
                @SerializedName("x-amz-acl")
                private String acl;
                @SerializedName("Content-MD5")
                private String md5;
                @SerializedName("x-amz-content-sha256")
                private String sha256;
                @SerializedName("x-amz-date")
                private String date;
            }
        }

        public class CompleteResponse {
            private String url;
            private String handle;
            private String filename;
            private long size;
            private String mimetype;

            public String getUrl() {
                return url;
            }

            public String getHandle() {
                return handle;
            }

            public String getFilename() {
                return filename;
            }

            public long getSize() {
                return size;
            }

            public String getMimetype() {
                return mimetype;
            }
        }
    }
}
