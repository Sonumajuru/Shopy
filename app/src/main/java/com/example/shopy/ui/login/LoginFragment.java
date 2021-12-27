package com.example.shopy.ui.login;

import android.annotation.SuppressLint;
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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.shopy.R.id.btn_login;
import static com.example.shopy.R.id.txt_sign_up;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnLogin;
    private NavHostFragment navHostFragment;

    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("User");

        TextView textView = binding.txtSignUp;
        inputEmail = binding.txtEmail;
        inputPassword = binding.txtPassword;
        btnLogin = binding.btnLogin;
        btnLogin.setOnClickListener(this);
        textView.setOnClickListener(this);

        return root;
    }

    private void performLogin()
    {
        mAuth = FirebaseAuth.getInstance();
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Login details are empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setOnClickListener(v -> {

        //authenticate user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity(), task -> {
                // If sign in fails, display a message to the user.
                // If sign in succeeds the auth state listener will be notified and logic to handle the
                // signed-in user can be handled in the listener.
                if (!task.isSuccessful())
                {
                    Toast.makeText(getActivity(),"Wrong Email or Password!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    goToAccount();
                }
            });
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case btn_login:
                performLogin();
                break;
            case txt_sign_up:
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_register);
                break;
        }
    }

    private void goToAccount()
    {
        if (navHostFragment != null)
        {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.navigation_account);
        }
    }

    public void swapFragment(Fragment fragment)
    {
        Fragment currentFragment = this;
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(currentFragment.getId(), fragment)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(firebaseAuth -> {
            if (currentUser == null) {
            }
            else
            {
                goToAccount();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}