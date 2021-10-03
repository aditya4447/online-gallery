package in.omdev.onlinegallery.api;


import java.util.ArrayList;

import in.omdev.onlinegallery.Const;
import in.omdev.onlinegallery.model.Image;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImagesApi {
    @GET(Const.URL)
    Call<ArrayList<Image>> getImages(@Query("page") int page, @Query("limit") int entries);
}
