package com.genesistech.njangi.ui.message;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.adapter.MessageAdapter;
import com.genesistech.njangi.databinding.FragmentMessageBinding;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.model.Message;
import com.genesistech.njangi.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageFragment extends Fragment {

    private MessageViewModel messageViewModel;
    private FragmentMessageBinding binding;
    private FirebaseApp firebaseApp;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private RecyclerView recyclerView;
    private EditText editTextChat;
    private Button sendButton;
    private String name;
    private String receiverUuid;

    private String uUid;
    private String receiverRoom;
    private String senderRoom;
    private String senderUuid;
    private Controller controller;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseApp = new FirebaseApp();
        messageList = new ArrayList<>();
        controller = Controller.getInstance(requireContext());
        recyclerView = binding.recyclerGchat;
        editTextChat = binding.editGchatMessage;
        sendButton = binding.buttonGchatSend;

        assert getArguments() != null;
        uUid = getArguments().getString("uuid");
        receiverUuid = uUid;

        senderUuid = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();
        senderRoom = receiverUuid + " <------> "+ senderUuid;
        receiverRoom = senderUuid + " <------> "+ receiverUuid;

        getMessages();

        // Adding message to the DB
        /** Include product title, price to the message*/
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long millis = new java.util.Date().getTime();
                String message = editTextChat.getText().toString().trim();
                Message messageObject = new Message(message, controller.getUserName(), senderUuid, receiverUuid, millis);

                firebaseApp.getFirebaseDB().getReference()
                        .child("chats")
                        .child(senderUuid)
                        .child("messages").push()
                        .setValue(messageObject)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Write was successful!
                                firebaseApp.getFirebaseDB().getReference()
                                        .child("chats")
                                        .child(receiverUuid)
                                        .child("messages").push()
                                        .setValue(messageObject);
                                editTextChat.setText("");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                // ...
                                Toast.makeText(requireContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return root;
    }

    private void getMessages() {
        messageAdapter = new MessageAdapter(requireContext(), messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(messageAdapter);

        firebaseApp.getFirebaseDB().getReference()
                .child("chats")
                .child(senderUuid)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        messageList.clear();
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            Message message = ds.getValue(Message.class);
                            assert message != null;
                            if (Objects.equals(message.getReceiverUuid(), receiverUuid)
                                    || Objects.equals(message.getSenderUuid(), receiverUuid)) {
                                messageList.add(message);
                            }
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