package model;

import model.transform.base.ImageTransform;
import util.Upload;
import util.UploadOptions;

import java.io.IOException;

/**
 * Wrapper for communicating with the Filestack REST API.
 * Instantiate with an API Key from the Developer Portal.
 */
public class Client {
    private String apiKey;
    private Security security;

    public Client(String apiKey) {
        this.apiKey = apiKey;
    }

    public Client(String apiKey, Security security) {
        this.apiKey = apiKey;
        this.security = security;
    }

    public ImageTransform imageTransform(String url) {
        return new ImageTransform(this, url);
    }

    public FileLink upload(String filepath) throws IOException {
        UploadOptions defaultOptions = new UploadOptions.Builder().build();
        return upload(filepath, defaultOptions);
    }

    public FileLink upload(String filepath, UploadOptions options) throws IOException {
        Upload upload = new Upload(filepath, this, options);
        return upload.run();
    }

    public String getApiKey() {
        return apiKey;
    }

    public Security getSecurity() {
        return security;
    }
}
