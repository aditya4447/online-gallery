package in.omdev.onlinegallery.viewmodel;

import android.app.Application;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import in.omdev.onlinegallery.livedata.AuthLiveData;
import in.omdev.onlinegallery.livedata.ImagesLiveData;
import in.omdev.onlinegallery.model.Image;

public class ImageViewModel extends AndroidViewModel {

    private final AuthLiveData authLiveData = new AuthLiveData();
    private final ImagesLiveData imagesLiveData = new ImagesLiveData(getApplication());
    private final ArrayList<Image> images = new ArrayList<>();
    public Parcelable state;

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

    public ArrayList<Image> getImages() {
        return images;
    }
}
