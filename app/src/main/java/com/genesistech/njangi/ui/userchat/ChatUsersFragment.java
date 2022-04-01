package com.genesistech.njangi.ui.userchat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.ChatUserAdapter;
import com.genesistech.njangi.adapter.MessageAdapter;
import com.genesistech.njangi.databinding.FragmentChatUsersBinding;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ChatUsersFragment extends Fragment {

    private ChatUsersViewModel chatUsersViewModel;
    private FragmentChatUsersBinding binding;

    private List<Message> userList;
    private ChatUserAdapter adapter;
    private MessageAdapter messageAdapter;
    private FragmentCallback callback;
    private FirebaseApp firebaseApp;
    private RecyclerView recyclerView;

    public static ChatUsersFragment newInstance() {
        return new ChatUsersFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        chatUsersViewModel = new ViewModelProvider(this).get(ChatUsersViewModel.class);
        binding = FragmentChatUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userList = new ArrayList<>();
        firebaseApp = new FirebaseApp();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        getUserData();

        return root;
    }

    private void getUserData() {
        if (firebaseApp.getAuth().getCurrentUser() == null) return;
        String userid = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();
        firebaseApp.getFirebaseDB().getReference()
                .child("chats")
                .child(userid)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Message message = ds.getValue(Message.class);
                            assert message != null;
                            if (!Objects.equals(message.getSenderUuid(), userid)) {
                                userList.add(message);
                            }
                        }

                        TreeSet<Message> set = userList.stream()
                                .collect(Collectors.toCollection(() ->
                                        new TreeSet<>(Comparator.comparing(Message::getSenderName))));

                        ArrayList<Message> tempList = new ArrayList<>(set);
                        callback = new FragmentCallback() {
                            @Override
                            public void doSomething() {
                            }

                            @Override
                            public void onItemClicked(int position, Object object) {

                                Message message = (Message) object;
                                Bundle bundle = new Bundle();
                                bundle.putString("uuid", message.getSenderUuid());
                                Navigation.findNavController(requireView()).navigate(R.id.navigation_message, bundle);
                            }

                            @Override
                            public void onItemClicked(int position, Object object, int id) {

                            }
                        };

                        adapter = new ChatUserAdapter(getActivity(), tempList, callback);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void getMessages() {
        messageAdapter = new MessageAdapter(requireContext(), userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(messageAdapter);
        String receiverUuid = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();

        firebaseApp.getFirebaseDB().getReference()
                .child("chats")
                .child(receiverUuid)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        userList.clear();
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            Message message = ds.getValue(Message.class);
                            assert message != null;
                            userList.add(message);
                        }

                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}