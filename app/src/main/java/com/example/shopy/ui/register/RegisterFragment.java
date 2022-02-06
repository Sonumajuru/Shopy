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
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentRegisterBinding;
import com.example.shopy.helper.FirebaseApp;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    private FragmentRegisterBinding binding;

    private EditText name;
    private EditText surname;
    private CheckBox male;
    private CheckBox female;
    private EditText address;
    private Spinner country;
    private Spinner language;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRetypePassword;
    private NavHostFragment navHostFragment;
    private FirebaseApp firebaseApp;

    private User user;
    private Button btnRegister;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        DatabaseReference mDatabase = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("User");

        user = new User();
        List<String> langCode = new ArrayList<>();
        firebaseApp = new FirebaseApp();

        name = binding.txtName;
        surname = binding.txtSurname;
        male = binding.male;
        female = binding.female;
        address = binding.txtAddress;
        language = binding.language;
        country = binding.country;
        inputEmail = binding.txtEmail;
        inputPassword = binding.txtPassword;
        inputRetypePassword = binding.txtPhone;
        btnRegister = binding.btnRegister;

        registerViewModel.setCountryAdapter(country);
        registerViewModel.getLanguages(country, language, langCode);
        onCheckBoxSelection();
        btnRegister.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String name = this.name.getText().toString().trim();
            String surname = this.surname.getText().toString().trim();
            boolean male = this.male.isChecked();
            boolean female = this.female.isChecked();
            String address  = this.address.getText().toString().trim();
            String language = this.language.getSelectedItem().toString();
            String country = this.country.getSelectedItem().toString();
            String retypePassword = inputRetypePassword.getText().toString().trim();

            formCheck(name, surname, address, email, password);
            String text = btnRegister.getText().toString();
            if (!text.equals(getString(R.string.update)))
            {
                if (email.isEmpty() || password.isEmpty()) return;
                firebaseApp.getAuth().createUserWithEmailAndPassword(email, password)
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
                                user = new User(name, surname, male, female, address, language, country,
                                        email, password, retypePassword, deviceToken);

                                registerViewModel.goToAccount(userId, user, navHostFragment, mDatabase);
                            }
                        });
            }
            else
            {
                FirebaseUser currentUser = firebaseApp.getAuth().getCurrentUser();
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
                                user = new User(name, surname, male, female, address, language, country,
                                         email, password, retypePassword, deviceToken);
                                registerViewModel.goToAccount(userId, user, navHostFragment, mDatabase);
                            }
                            else
                            {
                                Toast.makeText(requireActivity(), "Failed to update password!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id)
            {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                // Showing selected spinner item
//                Toast.makeText(requireActivity(),"Selected Country : " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

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

    private void onCheckBoxSelection()
    {
        male.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                female.setChecked(false);
            }
        });
        female.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                male.setChecked(false);
            }
        });
    }

    private void getUserData()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userid = Objects.requireNonNull(user).getUid();
        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("User");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                RegisterFragment.this.user.setName(dataSnapshot.getValue(User.class).getName());
                RegisterFragment.this.user.setSurname(dataSnapshot.getValue(User.class).getSurname());
                RegisterFragment.this.user.setMale(dataSnapshot.getValue(User.class).isMale());
                RegisterFragment.this.user.setFemale(dataSnapshot.getValue(User.class).isFemale());
                RegisterFragment.this.user.setAddress(dataSnapshot.getValue(User.class).getAddress());
                RegisterFragment.this.user.setLanguage(dataSnapshot.getValue(User.class).getLanguage());
                RegisterFragment.this.user.setCountry(dataSnapshot.getValue(User.class).getCountry());
                RegisterFragment.this.user.setEmail(dataSnapshot.getValue(User.class).getEmail());
                RegisterFragment.this.user.setPassword(dataSnapshot.getValue(User.class).getPassword());
                RegisterFragment.this.user.setTelNumber(dataSnapshot.getValue(User.class).getTelNumber());

                disAbleControls();
                name.setText(RegisterFragment.this.user.getName());
                surname.setText(RegisterFragment.this.user.getSurname());
                male.setChecked(RegisterFragment.this.user.isMale());
                female.setChecked(RegisterFragment.this.user.isFemale());
                address.setText(RegisterFragment.this.user.getAddress());

                ArrayAdapter<String> countryAdapter = (ArrayAdapter<String>) country.getAdapter();
                int countryPosition = countryAdapter.getPosition(dataSnapshot.getValue(User.class).getCountry());
                country.setSelection(countryPosition);

                ArrayAdapter<String> languageAdapter = (ArrayAdapter<String>) language.getAdapter();
                int langPosition = languageAdapter.getPosition(dataSnapshot.getValue(User.class).getCountry());
                language.setSelection(langPosition);

                inputEmail.setText(RegisterFragment.this.user.getEmail());
                inputPassword.setText(RegisterFragment.this.user.getPassword());
                inputRetypePassword.setText(RegisterFragment.this.user.getTelNumber());
                btnRegister.setText(R.string.update);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void disAbleControls()
    {
        name.setEnabled(false);
        surname.setEnabled(false);
        male.setEnabled(false);
        country.setEnabled(false);
        female.setEnabled(false);
        inputEmail.setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseApp.getAuth().addAuthStateListener(firebaseAuth -> {
            if (firebaseApp.getAuth().getCurrentUser() == null) {
            }
            else
            {
                getUserData();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}