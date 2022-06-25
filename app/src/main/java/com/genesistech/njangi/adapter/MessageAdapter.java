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

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;
    private static final int VIEW_TYPE_MESSAGE_SENT = 2;
    private final Context mContext;
    private final FirebaseApp firebaseApp;
    private final List<Message> mMessageList;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;
    private String prevDate = "";
    private String receiverDate = "";
    private String senderDate = "";
    @SuppressLint("SimpleDateFormat")
    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        firebaseApp = new FirebaseApp();
        mMessageList = messageList;
        timeFormat = new SimpleDateFormat("HH:mm"); //hours and minutes, 24hr clock
        dateFormat = new SimpleDateFormat("dd - MMMM - yyyy");
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if (message.getSenderUuid().equals(Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid())) {
            // If the current user is the sender of the message
            senderDate = dateFormat.format(message.getCreatedAt());
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            receiverDate = dateFormat.format(message.getCreatedAt());
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
    // Passes the message object to a ViewHolder so that the contents can be bound to UI.    @Override
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
    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dataText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_gchat_message_me);
            timeText = itemView.findViewById(R.id.text_gchat_timestamp_me);
            dataText = itemView.findViewById(R.id.text_gchat_date_me);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(timeFormat.format(message.getCreatedAt()));
            if (senderDate.equals(receiverDate) || senderDate.equals(prevDate)) {
                dataText.setText("");
            }
            else {
                dataText.setText(dateFormat.format(message.getCreatedAt()));
            }
            prevDate = dateFormat.format(message.getCreatedAt());
        }
    }
    @SuppressLint("SimpleDateFormat")
    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dataText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_gchat_user_other);
            messageText = itemView.findViewById(R.id.text_gchat_message_other);
            timeText = itemView.findViewById(R.id.text_gchat_timestamp_other);
            dataText = itemView.findViewById(R.id.text_gchat_date_other);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            nameText.setText(message.getSenderName());
            timeText.setText(timeFormat.format(message.getCreatedAt()));
            if (receiverDate.equals(senderDate) || receiverDate.equals(prevDate)) {
                dataText.setText("");
            }
            else {
                dataText.setText(dateFormat.format(message.getCreatedAt()));
            }
            prevDate = dateFormat.format(message.getCreatedAt());
        }
    }
}