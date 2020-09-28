package firebaseapi.responses;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * A class representing an interaction with the Firebase database which resulted in either a success
 * ({@link #data() data}) or failure ({@link #error() error}).
 *
 * <p>Whether this instance represents an error or not can be checked through ({@link #isError() isError}).
 *
 * <p>In case of a success, the data type of the information retrieved is immediately known.
 */
public class TypedResult<T> extends VoidResult {

    private T data;

    /**
     * Creates a database response that represents a success.
     * @param data The data to be stored.
     */
    public TypedResult(T data) {
        this.data = data;
    }

    /**
     * Creates a database response that represents a success.
     * @param data The data snapshot retrieved from the database.
     */
    public TypedResult(DataSnapshot data) {
        this.data = (T) data.getValue();
    }

    /**
     * Creates a database response that represents a failure caused by the supplied error.
     * @param error The database error.
     */
    public TypedResult(DatabaseError error) {
        super(error);
    }

    /**
     * Converts a wildcard response into a typed response.
     * @param response The wildcard response.
     */
    public TypedResult(WildcardResult response) {
        if (response.isError()) error = response.error;
        else data = response.data();
    }

    /**
     * Returns the database result represented by this response, if any.
     * @return The database result, or null if this response represents an error.
     */
    public T data() {
        return data;
    }

    @Override
    public String toString() {
        return isError()
                ? String.format("[DatabaseResponse error = %s]", error.getMessage())
                : String.format("[DatabaseResponse class = %s, value = %s]", data.getClass().getName(), data.toString());
    }

}
