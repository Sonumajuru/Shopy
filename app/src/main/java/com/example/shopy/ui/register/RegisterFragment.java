package com.example.shopy.ui.register;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.shopy.databinding.FragmentRegisterBinding;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private RegisterViewModel registerViewModel;
    private FragmentRegisterBinding binding;

    private EditText name;
    private EditText surname;
    private CheckBox checkBoxMale;
    private CheckBox checkBoxFemale;
    private EditText address;
    private Spinner language;
    private Spinner country;
    private Spinner userType;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRetypePassword;
    private Button btnRegister;
    private NavHostFragment navHostFragment;

    private User user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("User");

        name = binding.txtName;
        surname = binding.txtSurname;
        checkBoxMale = binding.checkBoxMale;
        checkBoxFemale = binding.checkBoxFemale;
        address = binding.txtAddress;
        language = binding.language;
        country = binding.country;
        userType = binding.user;
        inputEmail = binding.txtEmail;
        inputPassword = binding.txtPassword;
        inputRetypePassword = binding.txtRetypePassword;
        btnRegister = binding.btnRegister;

        btnRegister.setOnClickListener(this);

        getLanguages();
        getCountry();
        getUserType();
        return root;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.language:
                break;
            case R.id.country:
                break;
            case R.id.user:
                break;
            case R.id.btn_register:
                performRegister();
                break;
        }
    }

    private void performRegister()
    {
        btnRegister.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String username = name.getText().toString().trim();
            String lastname = surname.getText().toString().trim();
            boolean isMale = checkBoxMale.isChecked();
            boolean isFemale = checkBoxFemale.isChecked();
            String addr  = address.getText().toString().trim();
            String lang = language.getSelectedItem().toString();
            String ctry = country.getSelectedItem().toString();
            String usrType = userType.getSelectedItem().toString();
            String retypePassword = inputRetypePassword.getText().toString().trim();

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

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
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
                        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        @SuppressLint("HardwareIds")
                        String deviceToken = Settings.Secure.getString(requireActivity().getApplicationContext()
                                .getContentResolver(), Settings.Secure.ANDROID_ID);
                        user = new User(username, lastname, isMale, isFemale, addr, lang, ctry,
                                usrType, email, password, retypePassword, deviceToken);
                        goToAccount(userId);
                    }
                });
        });
    }

    private void goToAccount(String userId)
    {
        if (navHostFragment != null)
        {
            mAuth.addAuthStateListener(firebaseAuth -> {
                mDatabase.child(userId).setValue(user);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_account);
            });
        }
    }

//    Updating Password at a specified location in the database
//    mDatabase.child("users").child(userId).child("username").setValue(name);

    private void getLanguages()
    {
        ArrayAdapter<String> adapter;
        List<String> languages = new ArrayList<>();
        languages.add(Locale.US.getDisplayLanguage(Locale.ENGLISH));
        languages.add(Locale.FRANCE.getDisplayLanguage(Locale.ENGLISH));

        adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);
    }

    private void getCountry()
    {
        ArrayAdapter<String> adapter;
        List<String> ctry = new ArrayList<>();
        ctry.add("Cameroon");
        ctry.add("Nigeria");

        adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, ctry);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(adapter);
    }

    private void getUserType()
    {
        ArrayAdapter<String> adapter;
        List<String> user = new ArrayList<>();
        user.add("Buyer");
        user.add("Seller");

        adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, user);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}