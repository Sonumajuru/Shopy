package com.example.shopy.ui.account;

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
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentAccountBinding;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

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
    TextView username;
    private LinearLayout linearLayout;

    private User NjangiUser;
    private NavHost navHostFragment;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        NjangiUser = new User();
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

        username = binding.textAccount;
        getUserData();

        return root;
    }

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
                NjangiUser.setName(dataSnapshot.getValue(User.class).getName());
                NjangiUser.setSurname(dataSnapshot.getValue(User.class).getSurname());
                NjangiUser.setCheckBokMale(dataSnapshot.getValue(User.class).isCheckBokMale());
                NjangiUser.setCheckBokFemale(dataSnapshot.getValue(User.class).isCheckBokFemale());
                NjangiUser.setAddress(dataSnapshot.getValue(User.class).getAddress());
                NjangiUser.setLanguage(dataSnapshot.getValue(User.class).getLanguage());
                NjangiUser.setCountry(dataSnapshot.getValue(User.class).getCountry());
                NjangiUser.setUserType(dataSnapshot.getValue(User.class).getUserType());
                NjangiUser.setEmail(dataSnapshot.getValue(User.class).getEmail());
                NjangiUser.setPassword(dataSnapshot.getValue(User.class).getPassword());
                NjangiUser.setRetypePassword(dataSnapshot.getValue(User.class).getRetypePassword());
                Log.d("Datasnapshot",email);
                username.setText(NjangiUser.getName());
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}