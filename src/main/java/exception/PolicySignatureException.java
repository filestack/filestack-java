package exception;

public class PolicySignatureException extends FilestackIOException {

    public PolicySignatureException() {
        super("Your policy or signature was rejected for the action");
    }
}
