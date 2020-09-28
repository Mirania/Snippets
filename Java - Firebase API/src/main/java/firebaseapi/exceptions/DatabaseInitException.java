package firebaseapi.exceptions;

public class DatabaseInitException extends Exception {

    public DatabaseInitException(String message) {
        super(message);
    }

    public DatabaseInitException(String message, Exception cause) {
        super(String.format("%s\nCause is %s: %s", message, cause.getClass().getName(), cause.getMessage()));
    }

}