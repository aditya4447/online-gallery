package in.omdev.onlinegallery.livedata;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class NonNullLiveData<T> extends MutableLiveData<T> {

    public NonNullLiveData(@NonNull T data) {
        super(data);
    }

    @Override
    public void setValue(@NonNull T value) {
        super.setValue(value);
    }

    @Override
    public void postValue(@NonNull T value) {
        super.postValue(value);
    }

    @NonNull
    @Override
    public T getValue() {
        return Objects.requireNonNull(super.getValue());
    }
}
