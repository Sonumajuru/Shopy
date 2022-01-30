package com.example.shopy.ui.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.helper.LanguageHelper;
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
//import static com.example.shopy.BuildConfig.*;

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

    private User user;
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

        user = new User();
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
        TextView emailSender = binding.emailText;
        TextView whatsAppNum = binding.chatTel;
        emailSender.setText(Html.fromHtml("<a href=\"malito:njangi@support.com\">Email: njangi@support.com</a>"));
        emailSender.setMovementMethod(LinkMovementMethod.getInstance());
        whatsAppNum.setText(Html.fromHtml("Chat: "+ "<a href=\"\">WhatsApp</a>"));
        whatsAppNum.setOnClickListener(view -> support());

        try {
            PackageInfo pInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            appVersion.setText(getString(R.string.version_number) +" "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getUserData();
        return root;
    }

    private void support()
    {
        String contact = "+237 666305349"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = requireActivity().getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        NavController navController = navHostFragment.getNavController();
        switch (v.getId())
        {
            case order_Btn:
                break;
            case invoice_Btn:
                break;
            case add_remove_Btn:
                navController.navigate(navigation_product);
                break;
            case settings_Btn:
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
        if (user == null) return;
        String userid = Objects.requireNonNull(user).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("User");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                String email = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getEmail();
                AccountFragment.this.user.setName(dataSnapshot.getValue(User.class).getName());
                AccountFragment.this.user.setSurname(dataSnapshot.getValue(User.class).getSurname());
                AccountFragment.this.user.setMale(dataSnapshot.getValue(User.class).isMale());
                AccountFragment.this.user.setFemale(dataSnapshot.getValue(User.class).isFemale());
                AccountFragment.this.user.setAddress(dataSnapshot.getValue(User.class).getAddress());
                AccountFragment.this.user.setLanguage(dataSnapshot.getValue(User.class).getLanguage());
                AccountFragment.this.user.setCountry(dataSnapshot.getValue(User.class).getCountry());
                AccountFragment.this.user.setBuyer(dataSnapshot.getValue(User.class).isBuyer());
                AccountFragment.this.user.setSeller(dataSnapshot.getValue(User.class).isSeller());
                AccountFragment.this.user.setEmail(dataSnapshot.getValue(User.class).getEmail());
                AccountFragment.this.user.setPassword(dataSnapshot.getValue(User.class).getPassword());
                AccountFragment.this.user.setTelNumber(dataSnapshot.getValue(User.class).getTelNumber());
                username.setText(AccountFragment.this.user.getName());
                userEmail.setText(AccountFragment.this.user.getEmail());
                btnManageItem.setEnabled(!Objects.requireNonNull(dataSnapshot.getValue(User.class)).isBuyer());
                setLocale(requireActivity(), dataSnapshot.getValue(User.class).getLanguage());
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public void setLocale(Activity activity, String languageCode)
    {

        Locale locale = new Locale(languageCode.substring(0,2));
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        LanguageHelper.storeUserLanguage(requireActivity(), String.valueOf(locale));
        LanguageHelper.updateLanguage(requireActivity(), String.valueOf(locale));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}