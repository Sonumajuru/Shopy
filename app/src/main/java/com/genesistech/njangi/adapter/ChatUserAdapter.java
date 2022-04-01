package com.genesistech.njangi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.R;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.UserChatViewHolder> {

    private final Context mCtx;
    public FragmentCallback callback;
    private final List<Message> UserList;

    //getting the context and product list with constructor
    public ChatUserAdapter(Context mCtx, List<Message> UserList, FragmentCallback callback) {
        this.mCtx = mCtx;
        this.UserList = UserList;
        this.callback = callback;
    }

    @SuppressLint("InflateParams")
    @NotNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(R.layout.chat_users_layout, null);
        return new UserChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull UserChatViewHolder holder, int position) {
        Message message = UserList.get(position);
        holder.txt_username.setText(message.getSenderName());
        holder.bind(UserList.get(position), callback);

        holder.txt_username.setOnClickListener(v -> {
//            clickedPos = holder.getAbsoluteAdapterPosition();
            callback.onItemClicked(position, message);
        });
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }

    public static class UserChatViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username;

        public UserChatViewHolder(View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.user_name);
        }

        public void bind(final Message item, final FragmentCallback listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClicked(getAbsoluteAdapterPosition(), item);
                }
            });
        }
    }
}