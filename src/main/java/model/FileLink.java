package model;

import exception.FilestackIOException;
import model.transform.base.ImageTransform;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.apache.tika.Tika;
import retrofit2.Response;
import util.FilestackService;
import util.Networking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    public void overwrite(String pathname) throws IOException {
        if (security == null)
            throw new FilestackIOException("Overwrite requires security to be set");

        File file = new File(pathname);
        if (!file.isFile())
            throw new FileNotFoundException(pathname);

        Tika tika = new Tika();
        String mimeType = tika.detect(file);
        RequestBody body = RequestBody.create(MediaType.parse(mimeType), file);

        apiService.overwrite(handle, security.getPolicy(), security.getSignature(), body).execute();
    }

    public void delete() throws IOException {
        if (security == null)
            throw new FilestackIOException("Delete requires security to be set");

        apiService.delete(handle, apiKey, security.getPolicy(), security.getSignature()).execute();
    }

    public ImageTransform imageTransform() {
        return new ImageTransform(this);
    }

    public String getHandle() {
        return handle;
    }

    public Security getSecurity() {
        return security;
    }
}
