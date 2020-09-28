package firebaseapi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import firebaseapi.responses.*;
import java.util.concurrent.CompletableFuture;
import static com.google.firebase.database.DatabaseReference.*;

public class DatabaseListeners {

    public static ValueEventListener newEventListener(CompletableFuture<WildcardResult> responseHolder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                responseHolder.complete(new WildcardResult(dataSnapshot));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                responseHolder.complete(new WildcardResult(error));
            }
        };
    }

    public static <T> ValueEventListener newTypedEventListener(CompletableFuture<TypedResult<T>> responseHolder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                responseHolder.complete(new TypedResult<>(dataSnapshot));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                responseHolder.complete(new TypedResult<>(error));
            }
        };
    }

    public static CompletionListener newCompletionListener(CompletableFuture<VoidResult> responseHolder) {
        return (databaseError, databaseReference) -> responseHolder.complete(
                databaseError == null
                        ? new VoidResult()
                        : new VoidResult(databaseError)
        );
    }

    public static CompletionListener newPushCompletionListener(
            CompletableFuture<TypedResult<String>> responseHolder,
            String key
    ) {
        return (databaseError, databaseReference) -> responseHolder.complete(
                databaseError == null
                        ? new TypedResult<>(key)
                        : new TypedResult<>(databaseError)
        );
    }

}