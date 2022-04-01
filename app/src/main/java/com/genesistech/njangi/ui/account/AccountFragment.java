package com.genesistech.njangi.ui.account;

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
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.databinding.FragmentAccountBinding;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.model.User;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.genesistech.njangi.R.id.*;
//import static com.example.shopy.BuildConfig.*;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;

    private User user;
    private Controller controller;
    private FirebaseApp firebaseApp;
    private NavHost navHostFragment;

    private TextView username;
    private TextView userEmail;
    private Button btnSignOut;

    private boolean isConnected = false;

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        user = new User();
        firebaseApp = new FirebaseApp();
        controller = Controller.getInstance(requireContext());
        Button btnOrder = binding.orderBtn;
        username = binding.userName;
        userEmail = binding.userEmail;
        Button btnManageItem = binding.manageBtn;
        Button btnSettings = binding.settingsBtn;
        btnSignOut = binding.signOutBtn;
        Button btnAddPay = binding.AddPaymentBtn;
        Button btnMessages = binding.messageBtn;

        btnOrder.setOnClickListener(this);
        btnManageItem.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnAddPay.setOnClickListener(this);
        btnMessages.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        checkIfSignedIn();

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

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Navigation.findNavController(requireView()).navigate(navigation_home);
            }
        });

        getUserData();
        return root;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case messageBtn:
                if (!checkIfSignedIn()) {
                    getLogin();
                } else {
                    Navigation.findNavController(v).navigate(navigation_chat_user);
                }
                break;
            case orderBtn:
                break;
            case AddPayment_Btn:
                if (!checkIfSignedIn()) {
                    getLogin();
                } else {
                    Navigation.findNavController(v).navigate(R.id.navigation_bank);
                }
                break;
            case manage_Btn:
                if (!checkIfSignedIn()) {
                    getLogin();
                } else {
                    Navigation.findNavController(v).navigate(R.id.navigation_product);
                }
                break;
            case settings_Btn:
                if (!checkIfSignedIn()) {
                    getLogin();
                } else {
                    Navigation.findNavController(v).navigate(R.id.navigation_register);
                }
                break;
            case sign_out_Btn:
                if(btnSignOut.getText().toString().equals(getString(R.string.login))) {
                    getLogin();
                } else {
                    accountViewModel.signOut(navHostFragment);
                }
                break;
        }
    }

    private void getUserData() {
        if (firebaseApp.getAuth().getCurrentUser() == null) return;
        String userid = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();
        firebaseApp.getFirebaseDB()
                .getReference()
                .child("User")
                .child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                if (user != null)
                {
                    username.setText(user.getFirstName());
                    controller.setUserName(user.getFirstName());
                    userEmail.setText(user.getEmail());
                    accountViewModel.setLocale(requireActivity(), user.getLanguage());
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

    private void getLogin() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(
                R.id.navigation_login,
                null,
                new NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_up)
                        .setExitAnim(R.anim.slide_down)
                        .build()
        );
    }

    private SpannableString setupHyperlink(String linkTextView) {
        SpannableString content = new SpannableString(linkTextView);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    public boolean checkIfSignedIn() {
        firebaseApp.getAuth().addAuthStateListener(firebaseAuth -> {
            if (firebaseApp.getAuth().getCurrentUser() == null) {
                int unicode = 0x1F60A;
                btnSignOut.setText(R.string.login);
                username.setText(R.string.welcome);
                userEmail.setText(R.string.not_signed_in);
                isConnected = false;
            }
            else
            {
                btnSignOut.setText(R.string.sign_out);
                isConnected = true;
            }
        });
        return isConnected;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}