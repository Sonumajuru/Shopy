package com.example.shopy.ui.register;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.R;
import com.example.shopy.activity.MainActivity;
import com.example.shopy.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.shopy.R.id.*;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private RegisterViewModel registerViewModel;
    private FragmentRegisterBinding binding;

    private EditText name;
    private EditText surname;
    private CheckBox checkBokMale;
    private CheckBox checkBokFemale;
    private EditText address;
    private Spinner spinnerLanguage;
    private Spinner spinnerCountry;
    private Spinner spinnerUser;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRetypePassword;
    private Button btnRegister;
    private NavHostFragment navHostFragment;
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        name = binding.txtName;
        surname = binding.txtSurname;
        checkBokMale = binding.checkBoxMale;
        checkBokFemale = binding.checkBoxFemale;
        address = binding.txtAddress;
        spinnerLanguage = binding.language;
        spinnerCountry = binding.country;
        spinnerUser = binding.user;
        inputEmail = binding.txtEmail;
        inputPassword = binding.txtPassword;
        inputRetypePassword = binding.txtRetypePassword;
        btnRegister = binding.btnRegister;
        btnRegister.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case language:
                break;
            case country:
                break;
            case user:
                break;
            case btn_register:
                performRegister();
                break;
        }
    }

    private void performRegister()
    {
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

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

                if (password.length() < 6) {
                    Toast.makeText(requireActivity().getApplicationContext(),
                            "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(requireActivity(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
    //                                progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            // If sign in succeeds the auth state listener will be notified and logic to handle the
                            // signed-in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(requireActivity(), "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if (navHostFragment != null)
                                {
                                    NavController navController = navHostFragment.getNavController();
                                    navController.navigate(R.id.navigation_account);
                                }
                            }
                        }
                    });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}