package com.genesistech.njangi.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.genesistech.njangi.R;
import com.genesistech.njangi.databinding.FragmentLoginBinding;
import com.genesistech.njangi.helper.FirebaseApp;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private EditText inputEmail;
    private EditText inputPassword;
    private NavHostFragment navHostFragment;
    private NavOptions navOption;
    private FirebaseApp firebaseApp;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        //here the R.id refer to the fragment one wants to pop back once pressed back from the newly  navigated fragment
        navOption = new NavOptions.Builder().setPopUpTo(R.id.navigation_login, true).build();

        firebaseApp = new FirebaseApp();
        TextView signUp = binding.txtSignUp;
        inputEmail = binding.txtEmail;
        inputPassword = binding.txtPassword;
        Button btnLogin = binding.btnLogin;
        TextView forget = binding.txtForget;

        btnLogin.setOnClickListener(v -> {

            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            formCheck(email, password);

            if (email.isEmpty() || password.isEmpty()) return;
            //authenticate user
            firebaseApp.getAuth().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        // If sign in fails, display a message to the user.
                        // If sign in succeeds the auth state listener will be notified and logic to handle the
                        // signed-in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(),"Wrong Email or Password!",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loginViewModel.goToAccount(navHostFragment, navOption);
                        }
                    });
        });
        signUp.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.navigation_register));
        forget.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.navigation_reset));

        return root;
    }

    private void formCheck(String email, String password)
    {
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("");
            inputEmail.requestFocus();
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("");
            inputPassword.requestFocus();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseApp.getAuth().addAuthStateListener(firebaseAuth -> {
            if (firebaseApp.getAuth().getCurrentUser() != null) {
                loginViewModel.goToAccount(navHostFragment, navOption);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}