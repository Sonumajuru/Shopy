package com.example.shopy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.Controller;
import com.example.shopy.FragmentCallback;
import com.example.shopy.R;
import com.example.shopy.db.FavDB;
import com.example.shopy.model.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> cartItemList;
    private final Context mCtx;
    private FavDB favDB;
    private Controller controller;
    private int count;
    public FragmentCallback callback;

    public CartAdapter(List<CartItem> cartItemList, Context mCtx, FragmentCallback callback) {
        this.cartItemList = cartItemList;
        this.mCtx = mCtx;
        controller = Controller.getInstance(mCtx);
        this.callback = callback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favDB = new FavDB(mCtx);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items,parent,false);
        return new CartViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {

        final CartItem cartItem = cartItemList.get(position);

        holder.prname.setText(cartItem.getTitle());
        holder.prprice.setText(cartItem.getPrice() + " " + cartItem.getCurrency());
        Uri uri = Uri.parse(cartItem.getImageUrl());
        Picasso.with(mCtx).load(uri).into(holder.image);
        count = controller.getBadgeCount();
        holder.prqtty.setText(String.valueOf(count));

        holder.minusbtn.setOnClickListener(v -> {
            count = count - 1;
            if (count < 0) count = 0;
            controller.setBadgeCount(count);
            controller.addBadge(count);
            holder.prqtty.setText(String.valueOf(count));
        });
        holder.plusbtn.setOnClickListener(v -> {
            count = count + 1;
            if (count < 0) count = 0;
            controller.setBadgeCount(count);
            controller.addBadge(count);
            holder.prqtty.setText(String.valueOf(count));
        });
        holder.deletbtn.setOnClickListener(v -> {

            cartItemList.get(position).setCartStatus("0");
            favDB.remove_from_cart(cartItemList.remove(position).getKey_id());
//                cartItemList.remove(position);
            callback.onItemClicked(position, cartItem);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder{
        private final ImageView image;
        private final ImageView deletbtn;
        private final ImageView minusbtn;
        private final ImageView plusbtn;
        private final TextView prname;
        private final TextView prprice;
        private final TextView prqtty;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            prname = itemView.findViewById(R.id.idProductName);
            image = itemView.findViewById(R.id.idProductImage);
            prprice = itemView.findViewById(R.id.idProductPrice);
            prqtty = itemView.findViewById(R.id.idProductQty);
            minusbtn = itemView.findViewById(R.id.idMinusICon);
            plusbtn = itemView.findViewById(R.id.idPlusIcon);
            deletbtn = itemView.findViewById(R.id.idDeleteICon);
        }
    }
}