package firebaseapi.responses;

import com.google.firebase.database.DatabaseError;

/**
 * A class representing an interaction with the Firebase database which resulted in either a success (no data
 * returned) or failure ({@link #error() error}).
 *
 * <p>Whether this instance represents an error or not can be checked through ({@link #isError() isError}).
 */
public class VoidResult {

    protected DatabaseError error;

    /**
     * Creates a database response that represents a success.
     */
    public VoidResult() { }

    /**
     * Creates a database response that represents a failure caused by the supplied error.
     * @param error The database error.
     */
    public VoidResult(DatabaseError error) {
        this.error = error;
    }

    /**
     * Checks whether this response represents a database error.
     * @return Whether this response contains a DatabaseError object.
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * Returns the database error represented by this response, if any.
     * @return The database error, or null if this response represents a success.
     */
    public DatabaseError error() {
        return error;
    }

    @Override
    public String toString() {
        return isError()
                ? String.format("[DatabaseResponse error = %s]", error.getMessage())
                : "[DatabaseResponse]";
    }
}
