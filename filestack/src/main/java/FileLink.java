/**
 * References a file in Filestack.
 *
 * @author Shawn Aten (shawn@filestack.com)
 */
public class FileLink {
    private String handle;

    /**
     *
     * @param handle A handle is returned after a file upload.
     */
    public FileLink(String handle) {
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }
}
