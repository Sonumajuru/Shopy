package com.genesistech.njangi.adapter;

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
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private static final String TAG = "CartAdapter";
    private final List<CartItem> cartItemList;
    private final Context mCtx;
    private final Controller controller;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items,parent,false);
        return new CartViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {

        final CartItem cartItem = cartItemList.get(position);

        holder.prname.setText(cartItem.getTitle());
        holder.prprice.setText(cartItem.getPrice() + " " + cartItem.getCurrency());
        Uri uri = Uri.parse(cartItem.getImages().get(0));
        Picasso.with(mCtx).load(uri).into(holder.image);
        count = cartItem.getQuantity();
        holder.prqtty.setText(String.valueOf(count));

        holder.minusbtn.setOnClickListener(v -> {
            count = count - 1;
            if (count < 0) count = 0;
            cartItem.setQuantity(count);

            for (CartItem item : cartItemList) {
                if (item != cartItem)
                {
                    controller.setBadgeCount(count + item.getQuantity());
                    controller.addBadge(count + item.getQuantity());
                }
            }

            holder.prqtty.setText(String.valueOf(count));
            resetProduct(position, cartItem);
        });

        holder.plusbtn.setOnClickListener(v -> {
            count = count + 1;
            if (count < 0) count = 0;
            cartItem.setQuantity(count);

            for (CartItem item : cartItemList) {
                if (item != cartItem)
                {
                    controller.setBadgeCount(count + item.getQuantity());
                    controller.addBadge(count + item.getQuantity());
                }
            }

            holder.prqtty.setText(String.valueOf(count));
            resetProduct(position, cartItem);
        });

        holder.deletbtn.setOnClickListener(v -> deleteProduct(position, cartItem));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void resetProduct(int position, CartItem cartItem) {
        if (cartItem.getQuantity() == 0) {
            cartItemList.get(position).setCartStatus("0");
//            favDB.removeCartItem(cartItemList.remove(position).getKey_id());
//                cartItemList.remove(position);
            controller.addBadge(controller.getBadgeCount() + count);
            callback.onItemClicked(position, cartItem);
            notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteProduct(int position, CartItem cartItem) {
        cartItemList.get(position).setCartStatus("0");
//        favDB.removeCartItem(cartItemList.remove(position).getKey_id());
//                cartItemList.remove(position);
        controller.addBadge(controller.getBadgeCount() + count);
        callback.onItemClicked(position, cartItem);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
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

            controller.setTextLength(prname);
        }
    }
}