package exception;

public class HandleNotFoundException extends FilestackIOException {

    public HandleNotFoundException() {
        super("No file with that handle was found");
    }
}
