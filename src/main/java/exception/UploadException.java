package exception;

public class UploadException extends FilestackIOException {

    public UploadException() {
        super("File upload failed");
    }

    public UploadException(String message) {
        super(message);
    }
}
