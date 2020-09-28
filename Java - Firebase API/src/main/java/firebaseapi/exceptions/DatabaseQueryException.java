package firebaseapi.exceptions;

public class DatabaseQueryException extends RuntimeException {

    public DatabaseQueryException(String message) {
        super(message);
    }

    public DatabaseQueryException(String message, Exception cause) {
        super(String.format("%s\nCause is %s: %s", message, cause.getClass().getName(), cause.getMessage()));
    }

}