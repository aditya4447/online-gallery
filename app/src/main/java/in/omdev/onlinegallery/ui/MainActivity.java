package in.omdev.onlinegallery.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import in.omdev.onlinegallery.ImagesDatabase;
import in.omdev.onlinegallery.R;
import in.omdev.onlinegallery.dao.ImagesDao;
import in.omdev.onlinegallery.databinding.ActivityMainBinding;
import in.omdev.onlinegallery.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private Menu menu;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.observeAuthLiveData(this, firebaseUser -> {
            if (firebaseUser == null) {
                // user logged out
                // show sign-in fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(binding.containerMain.getId(), SignInFragment.newInstance())
                        .commit();

                user = null;

                // perform other sign out actions
                onSignOut();
            } else {
                // user logged in
                // show main Fragment

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(binding.containerMain.getId(), ImagesFragment.newInstance())
                        .commit();

                user = firebaseUser;

                // perform other sign in actions
                onSignIn();
            }
        });

    }

    private void onSignIn() {
        setupProfileOption();
    }

    // set up profile option menu and display profile image
    private void setupProfileOption() {
        if (menu != null) {
            MenuItem item_profile = menu.findItem(R.id.item_profile);
            item_profile.setIcon(R.drawable.ic_baseline_person_24);
            item_profile.setVisible(true);
            Glide
                    .with(this)
                    .load(user.getPhotoUrl())
                    .error(R.drawable.ic_baseline_person_24)
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable>
                                                            transition) {
                            item_profile.setIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            item_profile.setIcon(R.drawable.ic_baseline_person_24);
                        }
                    });
        }
    }

    private void onSignOut() {
        removeProfileOption();
        AsyncTask.execute(() -> {
            ImagesDao imagesDao = Room.databaseBuilder(this,
                    ImagesDatabase.class, "images").build().imagesDao();
            imagesDao.deleteAll();
        });
    }

    private void removeProfileOption() {
        if (menu != null) {
            MenuItem item_profile = menu.findItem(R.id.item_profile);
            item_profile.setIcon(R.drawable.ic_baseline_person_24);
            item_profile.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        if (user != null) {
            setupProfileOption();
        } else {
            removeProfileOption();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_profile) {
            if (user != null) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("profile");
                if (fragment == null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(binding.containerProfile.getId(),
                                    ProfileFragment.newInstance(), "profile")
                            .addToBackStack(null)
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .popBackStack();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}