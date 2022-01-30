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
import com.example.shopy.helper.LanguageHelper;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    private CheckBox userBuyer;
    private CheckBox userSeller;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRetypePassword;
    private NavHostFragment navHostFragment;

    private User user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button btnRegister;

    private List<String> langCode;

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

        user = new User();
        langCode = new ArrayList<>();

        name = binding.txtName;
        surname = binding.txtSurname;
        male = binding.male;
        female = binding.female;
        address = binding.txtAddress;
        language = binding.language;
        country = binding.country;
        userBuyer = binding.buyer;
        userSeller = binding.seller;
        inputEmail = binding.txtEmail;
        inputPassword = binding.txtPassword;
        inputRetypePassword = binding.txtPhone;
        btnRegister = binding.btnRegister;

        setCountryAdapter();
        getLanguages();
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
            boolean buyer = userBuyer.isChecked();
            boolean seller = userSeller.isChecked();
            String retypePassword = inputRetypePassword.getText().toString().trim();

            formCheck(name, surname, address, email, password);
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
                                user = new User(name, surname, male, female, address, language, country,
                                        buyer, seller, email, password, retypePassword, deviceToken);
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
                                user = new User(name, surname, male, female, address, language, country,
                                        buyer, seller, email, password, retypePassword, deviceToken);
                                goToAccount(userId);
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
        userBuyer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                userSeller.setChecked(false);
            }
        });
        userSeller.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                userBuyer.setChecked(false);
            }
        });
    }

    private void getUserData()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userid = Objects.requireNonNull(user).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("User");
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
                RegisterFragment.this.user.setBuyer(dataSnapshot.getValue(User.class).isBuyer());
                RegisterFragment.this.user.setSeller(dataSnapshot.getValue(User.class).isSeller());
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

                userBuyer.setChecked(RegisterFragment.this.user.isBuyer());
                userSeller.setChecked(RegisterFragment.this.user.isSeller());
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
        userBuyer.setEnabled(false);
        userSeller.setEnabled(false);
        inputEmail.setEnabled(false);
    }

    private List<String> getCountryList()
    {
        List<String> countriesList = new ArrayList<>();
        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales)
        {
            if (countryCode.equals("CM") || countryCode.equals("NG")|| countryCode.equals("GH"))
            {
                Locale obj = new Locale("", countryCode);
                countriesList.add(obj.getDisplayCountry(Locale.ENGLISH));
                Collections.sort(countriesList);
            }
        }

        return countriesList;
    }

    private void setCountryAdapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, getCountryList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(adapter);
    }

    private void getLanguages()
    {
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                langCode.clear();
                if (country.getSelectedItem().equals("Cameroon"))
                {
                    langCode.add("FR " + LanguageHelper.countryCodeToEmoji("FR"));
                    langCode.add("ENG " + LanguageHelper.countryCodeToEmoji("UK"));
                }
                else
                {
                    langCode.add("ENG " + LanguageHelper.countryCodeToEmoji("UK"));
                }
                setLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void setLanguage()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, langCode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}