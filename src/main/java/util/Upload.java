package util;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import exception.UploadException;
import model.Client;
import model.FileLink;
import model.Security;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import static util.FilestackService.Upload.*;

public class Upload {
    public static final int CHUNK_SIZE = 5 * 1024 * 1024;

    private final Client client;

    private final HashMap<String, RequestBody> baseParams;

    private final String filepath;
    private final String filename;
    private final String mimeType;
    private final MediaType mediaType;
    private final long size;

    StartResponse startResponse;
    private String parts;
    CompleteResponse completeResponse;

    public Upload(Client client, String filepath, UploadOptions options) throws IOException {
        this.client = client;
        this.filepath = filepath;

        baseParams = new HashMap<>();
        baseParams.put("apikey", Util.createStringPart(client.getApiKey()));
        baseParams.putAll(options.getMap());

        File file = new File(filepath);
        if (!file.exists()) throw new FileNotFoundException();

        filename = file.getName();
        mimeType = new Tika().detect(file);
        mediaType = MediaType.parse(mimeType);
        size = file.length();
    }

    public FileLink run() throws IOException {
        multipartStart();
        multipartUpload();
        multipartComplete();

        return new FileLink(client.getApiKey(), completeResponse.getHandle(), client.getSecurity());
    }

    private void multipartStart() throws IOException {
        HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);

        params.put("filename", Util.createStringPart(filename));
        params.put("size", Util.createStringPart(Long.toString(size)));
        params.put("mimetype", Util.createStringPart(mimeType));
        Security security = client.getSecurity();
        if (security != null) {
            params.put("policy", Util.createStringPart(security.getPolicy()));
            params.put("signature", Util.createStringPart(security.getSignature()));
        }

        StartResponse response = Networking.getUploadService().start(params).execute().body();
        if (response == null) throw new UploadException("Multipart start failed");
        startResponse = response;
    }

    private void multipartUpload() throws IOException {
        RandomAccessFile file = new RandomAccessFile(filepath, "r");
        HashMap<Integer, String> partsMap = new HashMap<>();

        byte[] bytes = new byte[CHUNK_SIZE];
        long bytesLeft = size;
        int chunkSize;
        long offset = 0;
        int part = 1;

        while (bytesLeft != 0) {
            chunkSize = (int) Math.min(bytesLeft, CHUNK_SIZE);

            file.seek(offset);
            file.read(bytes, 0, chunkSize);

            UploadResponse params = getUploadParams(part, bytes, chunkSize);
            String etag = uploadToS3(params, offset, chunkSize);
            partsMap.put(part, etag);

            part++;
            bytesLeft -= chunkSize;
            offset += chunkSize;
        }

        StringBuilder builder = new StringBuilder();
        for (Integer key : partsMap.keySet())
            builder.append(key).append(':').append(partsMap.get(key)).append(';');
        builder.deleteCharAt(builder.length()-1);
        parts = builder.toString();
    }

    private UploadResponse getUploadParams(int part, byte[] bytes, int chunkSize) throws IOException {
        @SuppressWarnings("deprecation")
        HashCode hc = Hashing.md5().newHasher().putBytes(bytes, 0, chunkSize).hash();
        String md5 = BaseEncoding.base64().encode(hc.asBytes());

        HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);
        params.putAll(startResponse.getUploadParams());
        params.put("part", Util.createStringPart(Integer.toString(part)));
        params.put("size", Util.createStringPart(Integer.toString(chunkSize)));
        params.put("md5", Util.createStringPart(md5));

        UploadResponse response = Networking.getUploadService().upload(params).execute().body();
        if (response == null) throw new UploadException("Multipart upload Filestack call failed");
        return response;
    }

    private String uploadToS3(UploadResponse params, long offset, int chunkSize) throws IOException {
        RequestBody requestBody = new ChunkRequestBody(filepath, mediaType, offset, chunkSize);

        Request request = new Request.Builder()
                .url(params.getUrl())
                .headers(params.getS3Headers())
                .put(requestBody)
                .build();

        Response response = Networking.getHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) throw new UploadException("Multipart upload S3 call failed");
        String etag = response.header("ETag");
        response.close();
        return etag;
    }

    private void multipartComplete() throws IOException {
        HashMap<String, RequestBody> params = new HashMap<>();
        params.putAll(baseParams);
        params.putAll(startResponse.getUploadParams());
        params.put("filename", Util.createStringPart(filename));
        params.put("size", Util.createStringPart(Long.toString(size)));
        params.put("mimetype", Util.createStringPart(mimeType));
        params.put("parts", Util.createStringPart(parts));

        CompleteResponse response = Networking.getUploadService().complete(params).execute().body();
        if (response == null) throw new UploadException("Multipart complete failed");
        completeResponse = response;
    }
}
