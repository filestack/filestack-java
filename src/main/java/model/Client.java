package model;

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

    public String getApiKey() {
        return apiKey;
    }

    public Security getSecurity() {
        return security;
    }
}
