package model;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;
import util.FilestackService;
import util.Networking;

import java.io.*;

/**
 * References a file in Filestack.
 */
public class FileLink {
    private String apiKey;
    private String handle;
    private Security security;

    private FilestackService.Cdn cdnService;
    private FilestackService.Api apiService;

    public FileLink(String apiKey, String handle) {
        this.apiKey = apiKey;
        this.handle = handle;

        this.cdnService = Networking.getCdnService();
        this.apiService = Networking.getApiService();
    }

    public FileLink(String apiKey, String handle, Security security) {
        this.apiKey = apiKey;
        this.handle = handle;
        this.security = security;

        this.cdnService = Networking.getCdnService();
        this.apiService = Networking.getApiService();
    }

    public ResponseBody getContent() throws IOException {
        if (security == null)
            return cdnService.get(this.handle, null, null).execute().body();
        else
            return cdnService.get(this.handle, security.getPolicy(), security.getSignature()).execute().body();
    }

    public File download(String directory) throws IOException {
        return download(directory, null);
    }

    public File download(String directory, String filename) throws IOException {
        Response<ResponseBody> response;

        if (security == null)
             response = cdnService.get(this.handle, null, null).execute();
        else
            response = cdnService.get(this.handle, security.getPolicy(), security.getSignature()).execute();

        if (filename == null)
            filename = response.headers().get("x-file-name");

        File file = new File(directory + "/" + filename);
        boolean created = file.createNewFile();

        BufferedSource source = response.body().source();
        BufferedSink sink = Okio.buffer(Okio.sink(file));

        sink.writeAll(source);
        sink.close();

        return file;
    }

    public String getHandle() {
        return handle;
    }
}
