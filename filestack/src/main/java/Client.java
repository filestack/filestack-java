/**
 * Wrapper for communicating with the Filestack REST API. Instantiate with an API Key from the Developer Portal.
 *
 * @author Shawn Aten (shawn@filestack.com)
 */
public class Client {
    private String APIKey;

    /**
     *
     * @param APIKey Get from the Developer Portal.
     */
    public Client(String APIKey) {
        this.APIKey = APIKey;
    }
}
