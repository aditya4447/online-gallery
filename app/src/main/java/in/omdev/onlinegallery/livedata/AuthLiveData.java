package in.omdev.onlinegallery.livedata;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class AuthLiveData extends LiveData<FirebaseUser>
        implements FirebaseAuth.AuthStateListener {
    @Override
    protected void onActive() {
        super.onActive();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        setValue(firebaseAuth.getCurrentUser());
    }
}