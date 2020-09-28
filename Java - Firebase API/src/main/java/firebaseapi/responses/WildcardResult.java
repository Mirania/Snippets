package firebaseapi.responses;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * A class representing an interaction with the Firebase database which resulted in either a success
 * ({@link #data() data}) or failure ({@link #error() error}).
 *
 * <p>Whether this instance represents an error or not can be checked through ({@link #isError() isError}).
 *
 * <p>In case of a success, the data type of the information retrieved is unknown - it is up to the consumer to cast
 * the value to the correct class.
 */
public class WildcardResult extends VoidResult {

    private Object data;

    /**
     * Creates a database response that represents a success.
     * @param data The data snapshot retrieved from the database.
     */
    public WildcardResult(DataSnapshot data) {
        this.data = data.getValue();
    }

    /**
     * Creates a database response that represents a failure caused by the supplied error.
     * @param error The database error.
     */
    public WildcardResult(DatabaseError error) {
        super(error);
    }

    /**
     * Returns the database result represented by this response, if any.
     * @param <T> The intended return type.
     * @return The database result, or null if this response represents an error.
     */
    public <T> T data() {
        return (T) data;
    }

    @Override
    public String toString() {
        return isError()
                ? String.format("[DatabaseResponse error = %s]", error.getMessage())
                : String.format("[DatabaseResponse class = %s, value = %s]", data.getClass().getName(), data.toString());
    }

}
