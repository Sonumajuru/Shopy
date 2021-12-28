package com.example.shopy.ui.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.Controller;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentAccountBinding;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import static com.example.shopy.R.id.*;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;

    private Button btnOrder;
    private Button btnInvoice;
    private Button btnManageItem;
    private Button btnSettings;
    private Button btnSignOut;
    private TextView username;
    private TextView userEmail;
    private LinearLayout linearLayout;

    private Controller controller;
    private User njangiUser;
    private NavHost navHostFragment;
    private FirebaseAuth mAuth;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        controller = new Controller();
        njangiUser = new User();
        btnOrder = binding.orderBtn;
        btnInvoice = binding.invoiceBtn;
        btnManageItem = binding.addRemoveBtn;
        btnSettings = binding.settingsBtn;
        btnSignOut = binding.signOutBtn;
        linearLayout = binding.supportLayout;

        btnOrder.setOnClickListener(this);
        btnInvoice.setOnClickListener(this);
        btnManageItem.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        username = binding.userName;
        userEmail = binding.userEmail;
        TextView appVersion = binding.appVersion;
        String versionName = BuildConfig.VERSION_NAME;
        appVersion.setText(getString(R.string.version_number) +" "+ versionName);
        getUserData();

        return root;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case order_Btn:
                break;
            case invoice_Btn:
                break;
            case add_remove_Btn:
                break;
            case settings_Btn:
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_register);
                break;
            case sign_out_Btn:
                mAuth.signOut();
                signOut();
                break;
        }
    }

    private void signOut()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(firebaseAuth -> {
            NavController navController = navHostFragment.getNavController();
            if (currentUser == null) {
                navController.navigate(navigation_login);
            }
            else
            {
                navController.navigate(R.id.navigation_account);
            }
        });
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
                Log.d("Datasnapshot",email);
                username.setText(njangiUser.getName());
                userEmail.setText(njangiUser.getEmail());
//                setLocale(requireActivity(), dataSnapshot.getValue(User.class).getLanguage());
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public static void setLocale(Activity activity, String languageCode)
    {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

//    requireActivity().finish();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}