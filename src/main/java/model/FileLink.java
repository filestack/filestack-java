package model;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;
import util.CdnService;
import util.Networking;

import java.io.*;

/**
 * References a file in Filestack.
 */
public class FileLink {
    private CdnService cdnService;
    private String apiKey;
    private String handle;

    /**
     * @param apiKey Get from the Developer Portal.
     * @param handle A handle is returned after a file upload.
     */
    public FileLink(String apiKey, String handle) {
        this.apiKey = apiKey;
        this.handle = handle;

        this.cdnService = Networking.getCdnService();
    }

    public ResponseBody getContent() throws IOException {
        return cdnService.get(this.handle).execute().body();
    }

    public File download(String directory) throws IOException {
        return download(directory, null);
    }

    public File download(String directory, String filename) throws IOException {
        Response<ResponseBody> response = cdnService.get(this.handle).execute();

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
