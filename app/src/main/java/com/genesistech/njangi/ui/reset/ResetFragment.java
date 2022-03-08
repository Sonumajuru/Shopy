package com.genesistech.njangi.ui.reset;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.genesistech.njangi.databinding.FragmentResetBinding;
import com.genesistech.njangi.helper.FirebaseApp;

public class ResetFragment extends Fragment {

    private ResetViewModel resetViewModel;
    private FragmentResetBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        resetViewModel = new ViewModelProvider(this).get(ResetViewModel.class);
        binding = FragmentResetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseApp firebaseApp = new FirebaseApp();
        TextView inputEmail = binding.txtEmail;
        Button btnReset = binding.btnReset;
        ProgressBar progressBar = binding.progressBar;

        btnReset.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                inputEmail.setError("");
                inputEmail.requestFocus();
            }

            progressBar.setVisibility(View.VISIBLE);
            firebaseApp.getAuth().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task ->
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}