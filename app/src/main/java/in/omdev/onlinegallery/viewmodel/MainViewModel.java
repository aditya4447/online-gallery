package in.omdev.onlinegallery.viewmodel;

import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import in.omdev.livedata.AuthLiveData;

public class MainViewModel extends ViewModel {

    private final AuthLiveData authLiveData = new AuthLiveData();

    public void observeAuthLiveData(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super FirebaseUser> observer) {
        authLiveData.observe(owner, observer);
    }
}
