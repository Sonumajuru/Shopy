package com.genesistech.njangi.ui.userchat;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.genesistech.njangi.R;

public class ChatUsersFragment extends Fragment {

    private ChatUsersViewModel mViewModel;

    public static ChatUsersFragment newInstance() {
        return new ChatUsersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_users, container, false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }
}