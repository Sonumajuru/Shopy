package com.genesistech.njangi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.R;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.model.Message;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private final Context mContext;
    private FirebaseApp firebaseApp;
    private final List<Message> mMessageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        firebaseApp = new FirebaseApp();
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if (message.getSender().getUniqueID().equals(Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_gchat_message_me);
            timeText = itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());

            @SuppressLint("SimpleDateFormat")
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm"); //hours and minutes, 24hr clock
            timeText.setText(format.format(message.getCreatedAt()));

        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_gchat_user_other);
            messageText = itemView.findViewById(R.id.text_gchat_message_other);
            timeText = itemView.findViewById(R.id.text_gchat_timestamp_other);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            nameText.setText(message.getSender().getFirstName());

            // Format the stored timestamp into a readable String using method.
            @SuppressLint("SimpleDateFormat")
            final SimpleDateFormat format = new SimpleDateFormat("HH:mm"); //hours and minutes, 24hr clock
            timeText.setText(format.format(message.getCreatedAt()));
        }
    }
}