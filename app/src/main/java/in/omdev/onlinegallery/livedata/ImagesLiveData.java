package in.omdev.onlinegallery.livedata;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Room;
import in.omdev.onlinegallery.Const;
import in.omdev.onlinegallery.ImagesDatabase;
import in.omdev.onlinegallery.api.ImagesApi;
import in.omdev.onlinegallery.dao.ImagesDao;
import in.omdev.onlinegallery.model.Image;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImagesLiveData extends NonNullLiveData<ArrayList<Image>> {

    private final Handler handler = new Handler();
    private final ImagesDao imagesDao;
    final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    final ImagesApi imagesApi = retrofit.create(ImagesApi.class);
    private int page = 0;

    public ImagesLiveData(Context context) {
        super(new ArrayList<>(Collections.singleton(new Image())));
        imagesDao = Room.databaseBuilder(context,
                ImagesDatabase.class, "images").build().imagesDao();
    }

    public void loadNextPage() {
        page++;
        AsyncTask.execute(() -> {
            if (imagesDao.getFirst(page) != null) {
                List<Image> images = imagesDao.getAll(page);
                handler.post(() -> {
                    int startPosition = getValue().size() - 1;
                    getValue().addAll(startPosition, images);
                    setValue(getValue());
                });
                return;
            }
            handler.post(() -> imagesApi.getImages(page, Const.IMAGES_PER_PAGE)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<Image>> call,
                                               @NonNull Response<ArrayList<Image>> response) {
                            if (response.body() != null) {
                                getValue().addAll(getValue().size() - 1, response.body());
                                setValue(getValue());
                            }
                            for (Image image : getValue()) {
                                image.setPage(page);
                            }
                            AsyncTask.execute(() -> imagesDao.insertAll(response.body()));
                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<Image>> call,
                                              @NonNull Throwable t) {

                        }
                    }));
        });
    }

    public void reset() {
        page = 0;
        setValue(new ArrayList<>(Collections.singletonList(new Image())));
    }
}
