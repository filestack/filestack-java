package util;

import com.google.gson.Gson;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

import java.util.Map;

public class MockUploadService implements FilestackService.Upload {
    private final BehaviorDelegate<FilestackService.Upload> delegate;
    private final Gson gson;

    MockUploadService(BehaviorDelegate<FilestackService.Upload> delegate) {
        this.delegate = delegate;
        this.gson = new Gson();
    }

    @Override
    public Call<StartResponse> start(Map<String, RequestBody> parameters) {
        String jsonString = "{"
                + "'uri' : '/bucket/apikey/filename',"
                + "'region' : 'region',"
                + "'upload_id' : 'id',"
                + "'location_url' : 'url',"
                + "'upload_type' : 'intelligent_ingestion'"
                + "}";

        StartResponse response = gson.fromJson(jsonString, StartResponse.class);

        return delegate.returningResponse(response).start(parameters);
    }

    @Override
    public Call<UploadResponse> upload(Map<String, RequestBody> parameters) {
        String jsonString = "{"
                + "'url' : 'https://s3.amazonaws.com/path',"
                + "'headers' : {"
                + "'Authorization' : 'auth_value',"
                + "'Content-MD5' : 'md5_value',"
                + "'x-amz-content-sha256' : 'sha256_value',"
                + "'x-amz-date' : 'date_value',"
                + "'x-amz-acl' : 'acl_value'"
                + "},"
                + "'location_url' : 'url'"
                + "}";

        UploadResponse response = gson.fromJson(jsonString, UploadResponse.class);

        return delegate.returningResponse(response).upload(parameters);
    }

    @Override
    public Call<ResponseBody> uploadS3(Map<String, String> headers, String url, RequestBody body) {
        MediaType mediaType = MediaType.parse("text/xml");
        ResponseBody responseBody = ResponseBody.create(mediaType, "");
        Response<ResponseBody> response = Response.success(responseBody, Headers.of("ETag", "test-etag"));
        Call<ResponseBody> call = Calls.response(response);

        return delegate.returningResponse(call).uploadS3(headers, url, body);
    }

    @Override
    public Call<ResponseBody> commit(Map<String, RequestBody> parameters) {
        MediaType mediaType = MediaType.parse("text/plain");
        ResponseBody responseBody = ResponseBody.create(mediaType, "");

        return delegate.returningResponse(responseBody).commit(parameters);
    }

    @Override
    public Call<CompleteResponse> complete(Map<String, RequestBody> parameters) {
        String jsonString = "{"
                + "'handle' : 'handle',"
                + "'url' : 'url',"
                + "'filename' : 'filename',"
                + "'size' : '0',"
                + "'mimetype' : 'mimetype'"
                + "}";

        CompleteResponse response = gson.fromJson(jsonString, CompleteResponse.class);

        return delegate.returningResponse(response).complete(parameters);
    }
}
