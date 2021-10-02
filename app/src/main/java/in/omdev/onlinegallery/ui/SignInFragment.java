package in.omdev.onlinegallery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collections;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import in.omdev.onlinegallery.R;
import in.omdev.onlinegallery.databinding.FragmentSignInBinding;

public class SignInFragment extends Fragment implements ActivityResultCallback<IdpResponse> {


    private FragmentSignInBinding binding;
    private final ActivityResultLauncher<Void> signInLauncher = registerForActivityResult(
            new SignInContract(), this);

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Get new Instance of the Sign-in fragment
     *
     * @return A new instance of fragment SignInFragment.
     */
    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding
                .inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Launch sign in Intent on button click
        binding.btnSignIn.setOnClickListener(v -> signInLauncher.launch(null));
    }

    @Override
    public void onActivityResult(IdpResponse result) {
        // Display error if any
        if (result == null) {
            showError(R.string.unknown_error);
            return;
        }
        if (result.getError() != null
        ) {
            if (result.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showError(R.string.no_internet_connection);
                return;
            }
            showError(R.string.unknown_error);
            return;
        }
        showError("");
    }

    public void showError(@StringRes int text) {
        binding.textErrorSigningIn.setText(text);
    }

    public void showError(String text) {
        binding.textErrorSigningIn.setText(text);
    }

    /**
     * Sign in Intent builder
     */
    public static class SignInContract extends ActivityResultContract<Void, IdpResponse> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            return AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Collections.singletonList(
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                    ))
                    .build();
        }

        @Override
        public IdpResponse parseResult(int resultCode, @Nullable Intent intent) {
            if (intent == null) {
                return null;
            }
            return IdpResponse.fromResultIntent(intent);
        }
    }
}