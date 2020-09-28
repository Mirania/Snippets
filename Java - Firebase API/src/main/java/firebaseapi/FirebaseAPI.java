package firebaseapi;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import firebaseapi.exceptions.*;
import firebaseapi.responses.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FirebaseAPI {

    private FirebaseDatabase database;
    private ExecutorService executor;

    /**
     * Connects the database instance to the online Firebase service, becoming operational.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * connect(new FileInputStream(credentials), "https://<database>:firebaseio.com");
     * }</pre>
     * @param config A file containing authentication information. This should include data
     * such as {@code type}, {@code project_id}, {@code private_key_id}, {@code private_key}
     * and more.
     * @param url The database's firebaseio URL.
     * @throws DatabaseInitException If the database instance fails to connect to the
     *         online service.
     */
    public FirebaseAPI(FileInputStream config, String url) throws DatabaseInitException {
        connect(config, url);
        executor = Executors.newCachedThreadPool();
    }

    /**
     * Connects the database instance to the online Firebase service, becoming operational.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * connect("path/to/credentials", "https://<database>:firebaseio.com");
     * }</pre>
     * @param configPath Path to a file containing authentication information. This should include data
     * such as {@code type}, {@code project_id}, {@code private_key_id}, {@code private_key}
     * and more.
     * @param url The database's firebaseio URL.
     * @throws DatabaseInitException If the database instance fails to connect to the
     *         online service.
     */
    public FirebaseAPI(String configPath, String url) throws DatabaseInitException {
        try {
            connect(new FileInputStream(configPath), url);
            executor = Executors.newCachedThreadPool();
        } catch (FileNotFoundException e) {
            throw new DatabaseInitException("Failed to initialize database.", e);
        }
    }

    /**
     * Connects the database instance to the online Firebase service, becoming operational.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * connect(new FileInputStream(credentials), "https://<database>:firebaseio.com");
     * }</pre>
     * @param config A file containing authentication information. This should include data
     * such as {@code type}, {@code project_id}, {@code private_key_id}, {@code private_key}
     * and more.
     * @param url The database's firebaseio URL.
     * @param executor The executor that will handle communication with the online service.
     * @throws DatabaseInitException If the database instance fails to connect to the
     *         online service.
     */
    public FirebaseAPI(FileInputStream config, String url, ExecutorService executor) throws DatabaseInitException {
        connect(config, url);
        this.executor = executor;
    }

    /**
     * Connects the database instance to the online Firebase service, becoming operational.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * connect("path/to/credentials", "https://<database>:firebaseio.com");
     * }</pre>
     * @param configPath Path to a file containing authentication information. This should include data
     * such as {@code type}, {@code project_id}, {@code private_key_id}, {@code private_key}
     * and more.
     * @param url The database's firebaseio URL.
     * @param executor The executor that will handle communication with the online service.
     * @throws DatabaseInitException If the database instance fails to connect to the
     *         online service.
     */
    public FirebaseAPI(String configPath, String url, ExecutorService executor) throws DatabaseInitException {
        try {
            connect(new FileInputStream(configPath), url);
            this.executor = executor;
        } catch (FileNotFoundException e) {
            throw new DatabaseInitException("Failed to initialize database.", e);
        }
    }

    private void connect(FileInputStream config, String url) throws DatabaseInitException {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(config))
                    .setDatabaseUrl(url)
                    .build();
            FirebaseApp.initializeApp(options);
            database = FirebaseDatabase.getInstance();
        } catch (IOException e) {
            throw new DatabaseInitException("Failed to initialize database.", e);
        }

    }

    /**
     * Returns the database instance currently being used.
     */
    public FirebaseDatabase database() {
        return database;
    }

    /**
     * Gets the data located at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * String name = database.get("users/123/name").data();
     * int age = database.get("users/123/age").data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the requested data, whose type
     * is up to the caller to declare.
     * @see #asyncGet(String)
     */
    public WildcardResult get(String path) {
        try {
            return asyncGet(path).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseQueryException("Failed to read from database.", e);
        }
    }

    private <T> TypedResult<T> typedGet(String path) {
        try {
            return this.<T>typedAsyncGet(path).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseQueryException("Failed to read from database.", e);
        }
    }

    /**
     * Gets the String located at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var x = database.getString("users/123/field").data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the requested data.
     * @see #get(String)
     * @see #asyncGetString(String) 
     */
    public TypedResult<String> getString(String path) {
        return typedGet(path);
    }

    /**
     * Gets the Long located at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var x = database.getLong("users/123/field").data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the requested data.
     * @see #get(String)
     * @see #asyncGetLong(String) 
     */
    public TypedResult<Long> getLong(String path) {
        return typedGet(path);
    }

    /**
     * Gets the Double located at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var x = database.getDouble("users/123/field").data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the requested data.
     * @see #get(String)
     * @see #asyncGetDouble(String) 
     */
    public TypedResult<Double> getDouble(String path) {
        return typedGet(path);
    }

    /**
     * Gets the Boolean located at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var x = database.getBoolean("users/123/field").data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the requested data.
     * @see #get(String)
     * @see #asyncGetBoolean(String)
     */
    public TypedResult<Boolean> getBoolean(String path) {
        return typedGet(path);
    }

    /**
     * Gets the Map located at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var x = database.<Integer>getMap("users/123/field").data();
     * }</pre>
     * @param <V> The class of every value in the map.
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the requested data.
     * @see #get(String)
     * @see #asyncGetMap(String)
     */
    public <V> TypedResult<Map<String, V>> getMap(String path) {
        return typedGet(path);
    }

    /**
     * Gets the List located at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var x = database.<Integer>getList("users/123/field").data();
     * }</pre>
     * @param <V> The class of every value in the list.
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the requested data.
     * @see #get(String)
     * @see #asyncGetList(String)
     */
    public <V> TypedResult<List<V>> getList(String path) {
        return typedGet(path);
    }

    /**
     * Posts the data to {@code path}. Will <b>overwrite</b> all data at that path if any is present.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * database.post("users/123/name", name);
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @param value The data to post, which can be a string, number, object, null and so on.
     * @returns An object containing either an error or nothing.
     * @see #asyncPost(String, Object) 
     */
    public VoidResult post(String path, Object value) {
        try {
            return asyncPost(path, value).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseQueryException("Failed to write to database.", e);
        }
    }

    /**
     * Updates the data at {@code path}. Only the provided keys will be overwritten.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * database.update("users/123", fields);
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @param value An object containing the fields that shall be replaced.
     * @returns An object containing either an error or nothing.
     * @see #asyncUpdate(String, Map) 
     */
    public VoidResult update(String path, Map<String, Object> value) {
        try {
            return asyncUpdate(path, value).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseQueryException("Failed to write to database.", e);
        }
    }

    /**
     * Removes the data at {@code path}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * database.remove("users/123");
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or nothing.
     * @see #asyncRemove(String) 
     */
    public VoidResult remove(String path) {
        try {
            return asyncRemove(path).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseQueryException("Failed to remove value from database.", e);
        }
    }

    /**
     * Generates a new key at {@code path} and posts the data to {@code path/<new key>}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var key = database.push("users", user).data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns An object containing either an error or the generated key.
     * @see #asyncPush(String, Object)
     */
    public TypedResult<String> push(String path, Object value) {
        try {
            return asyncPush(path, value).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DatabaseQueryException("Failed to write to database.", e);
        }
    }

    /**
     * Asynchronous alternative to {@link #get(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncGet("users/123/name");
     *
     * ...
     *
     * String name = future.get().data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the requested data, 
     * whose type is up to the caller to declare.
     */
    public Future<WildcardResult> asyncGet(String path) {
        var future = new CompletableFuture<WildcardResult>();

        executor.submit(() -> {
            var ref = database.getReference(path);
            ref.addListenerForSingleValueEvent(DatabaseListeners.newEventListener(future));
        });

        return future;
    }

    private <T> Future<TypedResult<T>> typedAsyncGet(String path) {
        var future = new CompletableFuture<TypedResult<T>>();

        executor.submit(() -> {
            var ref = database.getReference(path);
            ref.addListenerForSingleValueEvent(DatabaseListeners.newTypedEventListener(future));
        });

        return future;
    }

    /**
     * Asynchronous alternative to {@link #getString(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncGetString("users/123/field");
     *
     * ...
     *
     * var x = future.get().data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the requested data.
     * @see #asyncGet(String)
     */
    public Future<TypedResult<String>> asyncGetString(String path) {
        return typedAsyncGet(path);
    }

    /**
     * Asynchronous alternative to {@link #getLong(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncGetLong("users/123/field");
     *
     * ...
     *
     * var x = future.get().data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the requested data.
     * @see #asyncGet(String)
     */
    public Future<TypedResult<Long>> asyncGetLong(String path) {
        return typedAsyncGet(path);
    }

    /**
     * Asynchronous alternative to {@link #getDouble(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncGetDouble("users/123/field");
     *
     * ...
     *
     * var x = future.get().data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the requested data.
     * @see #asyncGet(String)
     */
    public Future<TypedResult<Double>> asyncGetDouble(String path) {
        return typedAsyncGet(path);
    }

    /**
     * Asynchronous alternative to {@link #getBoolean(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncGetBoolean("users/123/field");
     *
     * ...
     *
     * var x = future.get().data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the requested data.
     * @see #asyncGet(String)
     */
    public Future<TypedResult<Boolean>> asyncGetBoolean(String path) {
        return typedAsyncGet(path);
    }

    /**
     * Asynchronous alternative to {@link #getMap(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.<Integer>asyncGetMap("users/123/field");
     *
     * ...
     *
     * var x = future.get().data();
     * }</pre>
     * @param <V> The class of every value in the map.
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the requested data.
     * @see #asyncGet(String)
     */
    public <V> Future<TypedResult<Map<String, V>>> asyncGetMap(String path) {
        return typedAsyncGet(path);
    }

    /**
     * Asynchronous alternative to {@link #getList(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.<Integer>asyncGetList("users/123/field");
     *
     * ...
     *
     * var x = future.get().data();
     * }</pre>
     * @param <V> The class of every value in the list.
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the requested data.
     * @see #asyncGet(String)
     */
    public <V> Future<TypedResult<List<V>>> asyncGetList(String path) {
        return typedAsyncGet(path);
    }

    /**
     * Asynchronous alternative to {@link #post(String, Object)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncPost("users/123/name", name);
     *
     * ...
     *
     * future.get();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @param value The data to post, which can be a string, number, object, null and so on.
     * @returns A future. When resolved, it returns an object containing either an error or nothing.
     */
    public Future<VoidResult> asyncPost(String path, Object value) {
        var future = new CompletableFuture<VoidResult>();

        executor.submit(() -> {
            var ref = database.getReference(path);
            ref.setValue(value, DatabaseListeners.newCompletionListener(future));
        });

        return future;
    }

    /**
     * Asynchronous alternative to {@link #update(String, Map)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncUpdate("users/123", fields);
     *
     * ...
     *
     * future.get();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @param value The data to post, which can be a string, number, object, null and so on.
     * @returns A future. When resolved, it returns an object containing either an error or nothing.
     */
    public Future<VoidResult> asyncUpdate(String path, Map<String, Object> value) {
        var future = new CompletableFuture<VoidResult>();

        executor.submit(() -> {
            var ref = database.getReference(path);
            ref.updateChildren(value, DatabaseListeners.newCompletionListener(future));
        });

        return future;
    }

    /**
     * Asynchronous alternative to {@link #remove(String)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncRemove("users/123");
     *
     * ...
     *
     * future.get();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or nothing.
     */
    public Future<VoidResult> asyncRemove(String path) {
        var future = new CompletableFuture<VoidResult>();

        executor.submit(() -> {
            var ref = database.getReference(path);
            ref.removeValue(DatabaseListeners.newCompletionListener(future));
        });

        return future;
    }

    /**
     * Asynchronous alternative to {@link #push(String, Object)}.
     *
     * <p><h3>Example usage:</h3>
     * <pre>{@code
     * var future = database.asyncPush("users/123/name");
     *
     * ...
     *
     * var key = future.get().data();
     * }</pre>
     * @param path Path within the database, e.g. {@code users/123}.
     * @returns A future. When resolved, it returns an object containing either an error or the generated key.
     */
    public Future<TypedResult<String>> asyncPush(String path, Object value) {
        var future = new CompletableFuture<TypedResult<String>>();

        executor.submit(() -> {
            var ref = database.getReference(path).push();
            ref.setValue(value, DatabaseListeners.newPushCompletionListener(future, ref.getKey()));
        });

        return future;
    }

}
