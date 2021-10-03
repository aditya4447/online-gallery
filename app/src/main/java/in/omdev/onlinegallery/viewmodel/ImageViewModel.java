package in.omdev.onlinegallery.viewmodel;

import android.app.Application;

import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import in.omdev.onlinegallery.livedata.AuthLiveData;
import in.omdev.onlinegallery.livedata.ImagesLiveData;

public class ImageViewModel extends AndroidViewModel {

    private final AuthLiveData authLiveData = new AuthLiveData();
    private final ImagesLiveData imagesLiveData = new ImagesLiveData(getApplication());

    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    public void observeAuthLiveData(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super FirebaseUser> observer) {
        authLiveData.observe(owner, observer);
    }

    public ImagesLiveData getImagesLiveData() {
        return imagesLiveData;
    }
}
