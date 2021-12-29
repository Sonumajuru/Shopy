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
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentRegisterBinding;
import com.example.shopy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RegisterFragment extends Fragment {

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
    private NavHostFragment navHostFragment;

    private User njangiUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button btnRegister;

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

        njangiUser = new User();
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

        getLanguages();
        getCountry();
        getUserType();

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

            formCheck(username, lastname, addr, email, password);
            String text = btnRegister.getText().toString();
            if (!text.equals(getString(R.string.update)))
            {
                if (email.isEmpty() || password.isEmpty()) return;
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), task -> {
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
                                njangiUser = new User(username, lastname, isMale, isFemale, addr, lang, ctry,
                                        usrType, email, password, retypePassword, deviceToken);
                                goToAccount(userId);
                            }
                        });
            }
            else
            {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                assert currentUser != null;
                currentUser.updatePassword(password)
                        .addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful())
                            {
                                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                @SuppressLint("HardwareIds")
                                String deviceToken = Settings.Secure.getString(requireActivity().getApplicationContext()
                                        .getContentResolver(), Settings.Secure.ANDROID_ID);
                                njangiUser = new User(username, lastname, isMale, isFemale, addr, lang, ctry,
                                        usrType, email, password, retypePassword, deviceToken);
                                goToAccount(userId);
                            }
                            else
                            {
                                Toast.makeText(requireActivity(), "Failed to update password!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        return root;
    }

    private void formCheck(String username, String lastname, String addr, String email, String password)
    {
        if (TextUtils.isEmpty(username)) {
            name.setError("");
        }
        if (TextUtils.isEmpty(lastname)) {
            surname.setError("");
        }
        if (TextUtils.isEmpty(addr)) {
            address.setError("");
        }
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("");
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("");
        }
        if (password.length() < 6) {
            inputPassword.setError("");
        }
    }

    private void goToAccount(String userId)
    {
        if (navHostFragment != null)
        {
            mAuth.addAuthStateListener(firebaseAuth -> {
                mDatabase.child(userId).setValue(njangiUser);
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
        // Get Current Language app and change in respective
        Locale current = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0);
        if (current.getDisplayLanguage().equals("en"))
        {
            languages.add(Locale.US.getDisplayLanguage(Locale.ENGLISH));
            languages.add(Locale.FRANCE.getDisplayLanguage(Locale.ENGLISH));
        }
        else
        {
            languages.add(Locale.US.getDisplayLanguage(Locale.FRENCH));
            languages.add(Locale.FRANCE.getDisplayLanguage(Locale.FRENCH));
        }

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
        user.add(getString(R.string.Buyer));
        user.add(getString(R.string.Seller));

        adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, user);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
    }

    private void getUserData()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = Objects.requireNonNull(user).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("User");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                String email = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getEmail();
                njangiUser.setName(dataSnapshot.getValue(User.class).getName());
                njangiUser.setSurname(dataSnapshot.getValue(User.class).getSurname());
                njangiUser.setCheckBokMale(dataSnapshot.getValue(User.class).isCheckBokMale());
                njangiUser.setCheckBokFemale(dataSnapshot.getValue(User.class).isCheckBokFemale());
                njangiUser.setAddress(dataSnapshot.getValue(User.class).getAddress());
                njangiUser.setLanguage(dataSnapshot.getValue(User.class).getLanguage());
                njangiUser.setCountry(dataSnapshot.getValue(User.class).getCountry());
                njangiUser.setUserType(dataSnapshot.getValue(User.class).getUserType());
                njangiUser.setEmail(dataSnapshot.getValue(User.class).getEmail());
                njangiUser.setPassword(dataSnapshot.getValue(User.class).getPassword());
                njangiUser.setRetypePassword(dataSnapshot.getValue(User.class).getRetypePassword());

                disAbleControls();
                name.setText(njangiUser.getName());
                surname.setText(njangiUser.getSurname());
                checkBoxMale.setChecked(njangiUser.isCheckBokMale());
                checkBoxFemale.setChecked(njangiUser.isCheckBokFemale());
                address.setText(njangiUser.getAddress());
//                language.setSelection(njangiUser.getLanguage());
//                country.setText(njangiUser.getEmail());
//                userType.setText(njangiUser.getEmail());
                inputEmail.setText(njangiUser.getEmail());
                inputPassword.setText(njangiUser.getPassword());
                inputRetypePassword.setText(njangiUser.getRetypePassword());
                btnRegister.setText(R.string.update);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
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
                getUserData();
            }
        });
    }

    private void disAbleControls()
    {
        name.setEnabled(false);
        surname.setEnabled(false);
        checkBoxMale.setEnabled(false);
        checkBoxFemale.setEnabled(false);
        inputEmail.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}