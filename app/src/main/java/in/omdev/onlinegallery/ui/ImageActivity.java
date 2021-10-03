package in.omdev.onlinegallery.ui;

import androidx.appcompat.app.AppCompatActivity;
import in.omdev.onlinegallery.databinding.ActivityImageBinding;

import android.os.Bundle;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityImageBinding binding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String url = getIntent().getStringExtra("url");
        if (url == null) {
            finish();
            return;
        }
        Glide.with(this).load(url).into(binding.photoView);
    }
}