package com.genesistech.njangi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.R;
import com.genesistech.njangi.model.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.UserChatViewHolder> {

    private final Context mCtx;
    private List<User> UserList;

    //getting the context and product list with constructor
    public ChatUserAdapter(Context mCtx, Fragment fragment, List<User> UserList) {
        this.mCtx = mCtx;
        this.UserList = UserList;
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
    @SuppressLint({"RecyclerView", "SetTextI18n", "DefaultLocale", "ResourceType", "NonConstantResourceId"})
    public void onBindViewHolder(@NotNull UserChatViewHolder holder, int position) {
        User user = UserList.get(position);
        holder.txt_username.setText(user.getFirstName() + " " + user.getLastName());
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
    }
}