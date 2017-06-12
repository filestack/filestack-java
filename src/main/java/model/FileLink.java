package model;

/**
 * References a file in Filestack.
 */
public class FileLink {
    private String handle;

    /**
     * @param apiKey Get from the Developer Portal.
     * @param handle A handle is returned after a file upload.
     */
    public FileLink(String apiKey, String handle) {
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }
}
