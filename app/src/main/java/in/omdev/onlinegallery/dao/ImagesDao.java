package in.omdev.onlinegallery.dao;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import in.omdev.onlinegallery.model.Image;

@Dao
public interface ImagesDao {
    @Query("SELECT * FROM image WHERE page = :page LIMIT 1")
    Image getFirst(int page);

    @Query("SELECT * FROM image WHERE page = :page ORDER BY id")
    List<Image> getAll(int page);

    @Insert
    void insertAll(ArrayList<Image> images);

    @Query("DELETE FROM image")
    void deleteAll();
}
