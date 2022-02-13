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
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.Controller;
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
import java.util.UUID;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    private FragmentRegisterBinding binding;
    private Controller controller;

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
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

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

        mDatabase = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("User");

        List<String> langCode = new ArrayList<>();
        controller = Controller.getInstance(requireContext());
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
        progressBar = binding.progressBar;

        registerViewModel.setCountryAdapter(country);
        registerViewModel.getLanguages(country, language, langCode);
        onCheckBoxSelection();
        checkIfSignedIn();

        btnRegister.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String firstName = this.name.getText().toString().trim();
            String lastName = this.surname.getText().toString().trim();
            boolean male = this.male.isChecked();
            boolean female = this.female.isChecked();
            String address  = this.address.getText().toString().trim();
            String language = this.language.getSelectedItem().toString();
            String country = this.country.getSelectedItem().toString();
            String retypePassword = inputRetypePassword.getText().toString().trim();
            String date = controller.getDate();
            String uniqueID = UUID.randomUUID().toString();

            progressBar.setVisibility(View.VISIBLE);
            formCheck(firstName, lastName, address, email, password);
            String text = btnRegister.getText().toString();
            if (!text.equals(getString(R.string.update)))
            {
                if (email.isEmpty() || password.isEmpty()) return;
                firebaseApp.getAuth().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task ->
                        {
                            if (!task.isSuccessful())
                            {
                                Toast.makeText(requireContext(), "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else
                            {
                                String userId = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();
                                user = new User(firstName, lastName, male, female, address, language, country,
                                        email, password, retypePassword, uniqueID, date);
                                mDatabase.child(userId).setValue(user);
                                registerViewModel.goToAccount(navHostFragment);
                                progressBar.setVisibility(View.GONE);
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
                                user = new User(firstName, lastName, male, female, address, language, country,
                                         email, password, retypePassword, uniqueID, date);
                                mDatabase.child(userId).setValue(user);
                                progressBar.setVisibility(View.GONE);
                                registerViewModel.goToAccount(navHostFragment);
                            }
                            else
                            {
                                Toast.makeText(requireContext(), "Failed to update password!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
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
//                Toast.makeText(requireContext(),"Selected Country : " + item, Toast.LENGTH_LONG).show();
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
        if (firebaseApp.getAuth().getCurrentUser() == null) return;
        String userid = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("User");
        ref.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);

                if (user != null)
                {
                    disAbleControls();
                    name.setText(user.getFirstName());
                    surname.setText(user.getLastName());
                    male.setChecked(user.isMale());
                    female.setChecked(user.isFemale());
                    address.setText(user.getAddress());

                    ArrayAdapter<String> countryAdapter = (ArrayAdapter<String>) country.getAdapter();
                    int countryPosition = countryAdapter.getPosition(user.getCountry());
                    country.setSelection(countryPosition);

                    ArrayAdapter<String> languageAdapter = (ArrayAdapter<String>) language.getAdapter();
                    int langPosition = languageAdapter.getPosition(user.getCountry());
                    language.setSelection(langPosition);

                    inputEmail.setText(user.getEmail());
                    inputPassword.setText(user.getPassword());
                    inputRetypePassword.setText(user.getTelNumber());
                    btnRegister.setText(R.string.update);
                }
                else
                {
                    getUserData();
                }
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

    public void checkIfSignedIn() {
        firebaseApp.getAuth().addAuthStateListener(firebaseAuth -> {
            if (firebaseApp.getAuth().getCurrentUser() == null) {
                btnRegister.setText(R.string.save);
            }
            else
            {
                btnRegister.setText(R.string.update);
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