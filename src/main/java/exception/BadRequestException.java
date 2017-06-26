package exception;

public class BadRequestException extends FilestackIOException {

    public BadRequestException() {
        super("Something was wrong with your request");
    }

    public BadRequestException(String message) {
        super(message);
    }
}
