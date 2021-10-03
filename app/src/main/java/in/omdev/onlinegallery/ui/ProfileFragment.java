package in.omdev.onlinegallery.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import in.omdev.onlinegallery.R;
import in.omdev.onlinegallery.databinding.FragmentProfileBinding;
import in.omdev.onlinegallery.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment implements Observer<FirebaseUser> {

    private FragmentProfileBinding binding;
    private FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public boolean checkUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            getParentFragmentManager().beginTransaction().remove(this)
                    .commit();
            getParentFragmentManager().popBackStack();
            return false;
        }
        return true;
    }

    @Override
    public void onChanged(FirebaseUser user) {
        checkUser();
    }

    /**
     * Get new Instance of the profile fragment
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding
                .inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.observeAuthLiveData(getViewLifecycleOwner(), this);
        if (!checkUser()) {
            return;
        }
        Glide.with(this)
                .load(user.getPhotoUrl())
                .error(R.drawable.ic_baseline_person_24)
                .circleCrop()
                .into(binding.imageProfile);
        binding.textProfileName.setText(user.getDisplayName());
        binding.textProfileEmail.setText(user.getEmail());
        binding.btnSignOut.setOnClickListener(v -> {
            if (getContext() != null) {
                AuthUI.getInstance().signOut(requireContext());
            }
        });
    }
}