package util;

import model.Client;
import model.FileLink;
import model.Policy;
import model.Security;
import okhttp3.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class TestUpload {

    @BeforeClass
    public static void setup() {
        Interceptor startInterceptor = new GenericInterceptor.Builder()
                .urlRegex(".+multipart/start$")
                .addEmptyParam("filename", "size", "mimetype", "policy", "signature")
                .addEmptyResponse("uri", "region", "location_url", "upload_id")
                .build();

        Interceptor uploadInterceptor = new GenericInterceptor.Builder()
                .urlRegex(".+multipart/upload$")
                .addEmptyParam("uri", "region", "upload_id", "part", "size", "md5")
                .addEmptyResponse("url", "location_url")
                .addEmptyResponseObject("headers", "Authorization", "x-amz-acl", "Content-MD5",
                        "x-amz-content-sha256", "x-amz-date")
                .addResponse("url", "https://s3.amazonaws.com/test")
                .build();

        Interceptor completeInterceptor = new GenericInterceptor.Builder()
                .urlRegex(".+multipart/complete$")
                .addEmptyParam("uri", "region", "upload_id", "filename", "size", "mimetype")
                .addParam("parts", "1:test;2:test")
                .addEmptyResponse("url", "handle", "filename", "mimetype")
                .addResponse("size", 0)
                .build();

        OkHttpClient httpClient = Networking.getHttpClient().newBuilder()
                .addInterceptor(startInterceptor)
                .addInterceptor(uploadInterceptor)
                .addInterceptor(completeInterceptor)
                .addInterceptor(new S3Interceptor())
                .build();

        Networking.setCustomClient(httpClient);
    }

    @Test
    public void testUpload() throws IOException {
        Policy policy = new Policy.Builder().giveFullAccess().build();
        Security security = Security.createNew(policy, "appSecret");
        Client client = new Client("apiKey", security);

        Path path = Paths.get("/tmp/" + UUID.randomUUID().toString() + ".txt");
        RandomAccessFile file = new RandomAccessFile(path.toString(), "rw");
        file.writeChars("test content\n");
        file.setLength(10 * 1024 * 1024);
        file.close();

        FileLink fileLink = client.upload(path.toString());
        Assert.assertTrue(fileLink.getHandle().equals("test"));

        Files.delete(path);
    }

    @AfterClass
    public static void teardown() {
        Networking.removeCustomClient();
    }

    private static class S3Interceptor implements Interceptor {
        private static final MediaType XML_MEDIA_TYPE = MediaType.parse("application/xml");
        private static final String[] HEADERS = new String[] {"Authorization", "x-amz-acl", "Content-MD5",
                "x-amz-content-sha256", "x-amz-date"};

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String url = request.url().toString();

            if (!url.matches(".+amazonaws.+"))
                return chain.proceed(request);

            for (String header : HEADERS)
                Assert.assertNotNull(request.header(header));

            ResponseBody body =  ResponseBody.create(XML_MEDIA_TYPE, "test");

            return new Response.Builder()
                    .protocol(Protocol.HTTP_1_1)
                    .addHeader("ETag", "test")
                    .request(request)
                    .code(200)
                    .body(body)
                    .message("Okay")
                    .build();
        }
    }
}
