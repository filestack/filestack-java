package model;

/**
 * Wrapper for communicating with the Filestack REST API.
 * Instantiate with an API Key from the Developer Portal.
 */
public class Client {
    private String apiKey;

    /**
     * @param apiKey Get from the Developer Portal.
     */
    public Client(String apiKey) {
        this.apiKey = apiKey;
    }
}
