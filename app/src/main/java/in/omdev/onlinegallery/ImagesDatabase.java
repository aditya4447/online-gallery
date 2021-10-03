package in.omdev.onlinegallery;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import in.omdev.onlinegallery.dao.ImagesDao;
import in.omdev.onlinegallery.model.Image;

@Database(entities = {Image.class}, version = 1)
public abstract class ImagesDatabase extends RoomDatabase {
    public abstract ImagesDao imagesDao();
}
