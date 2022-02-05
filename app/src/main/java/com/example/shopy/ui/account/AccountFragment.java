package com.example.shopy.ui.account;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
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
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentAccountBinding;
import com.example.shopy.helper.FirebaseApp;
import com.example.shopy.model.User;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.example.shopy.R.id.*;
//import static com.example.shopy.BuildConfig.*;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;

    private User user;
    private NavHost navHostFragment;
    private LinearLayout linearLayout;
    private FirebaseApp firebaseApp;

    private Button btnOrder;
    private Button btnInvoice;
    private Button btnManageItem;
    private Button btnSettings;
    private Button btnSignOut;
    private TextView username;
    private TextView userEmail;

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        user = new User();
        firebaseApp = new FirebaseApp();
        btnOrder = binding.orderBtn;
        username = binding.userName;
        userEmail = binding.userEmail;
        btnInvoice = binding.invoiceBtn;
        btnManageItem = binding.manageBtn;
        btnSettings = binding.settingsBtn;
        btnSignOut = binding.signOutBtn;
        linearLayout = binding.supportLayout;

        btnOrder.setOnClickListener(this);
        btnInvoice.setOnClickListener(this);
        btnManageItem.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        final TextView emailSender = binding.emailText;
        accountViewModel.getEmail().observe(getViewLifecycleOwner(), s -> {
            emailSender.setText(setupHyperlink(s));
            emailSender.setLinkTextColor(Color.BLACK);
            emailSender.setMovementMethod(LinkMovementMethod.getInstance());
        });

        final TextView whatsAppNum = binding.chatTel;
        accountViewModel.getChat().observe(getViewLifecycleOwner(), s -> {
            whatsAppNum.setText(setupHyperlink(s));
            whatsAppNum.setOnClickListener(view -> accountViewModel.support(requireActivity()));
        });

        final TextView appVersion = binding.appVersion;
        accountViewModel.getAppVersion().observe(getViewLifecycleOwner(), appVersion::setText);

        getUserData();
        return root;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        NavController navController = navHostFragment.getNavController();
        switch (v.getId())
        {
            case orderBtn:
                break;
            case invoice_Btn:
                break;
            case manage_Btn:
                navController.navigate(navigation_product);
                break;
            case settings_Btn:
                navController.navigate(R.id.navigation_register);
                break;
            case sign_out_Btn:
                accountViewModel.signOut(navHostFragment);
                break;
        }
    }

    private void getUserData()
    {
        if (firebaseApp.getAuth().getCurrentUser() == null) return;
        String userid = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();
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
                accountViewModel.setLocale(requireActivity(), dataSnapshot.getValue(User.class).getLanguage());
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private SpannableString setupHyperlink(String linkTextView) {
        SpannableString content = new SpannableString(linkTextView);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}