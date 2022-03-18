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
import com.genesistech.njangi.helper.PrefManager;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private static final String TAG = "CartAdapter";
    private final List<Product> cartItemList;
    private final Context mCtx;
    private final Controller controller;
    private int count;
    public FragmentCallback callback;
    private final PrefManager prefManager;

    public CartAdapter(List<Product> cartItemList, Context mCtx, FragmentCallback callback) {
        this.cartItemList = cartItemList;
        prefManager = new PrefManager(mCtx);
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

        final Product cartItem = cartItemList.get(position);

        holder.prname.setText(cartItem.getTitle());
        holder.prprice.setText(cartItem.getPrice() + " " + cartItem.getCurrency());
        Uri uri = Uri.parse(cartItem.getImages().get(0));
        Picasso.with(mCtx).load(uri).into(holder.image);
        count = cartItem.getQuantity();
        holder.prqtty.setText(String.valueOf(count));

        holder.minusbtn.setOnClickListener(v -> {
            count = cartItem.getQuantity() - 1;
            if (count < 0) count = 0;
            cartItem.setQuantity(count);

            int foo = controller.getBadgeCount();
            foo = foo - 1;
            controller.setBadgeCount(foo);
            controller.addBadge(foo);

            holder.prqtty.setText(String.valueOf(count));
            resetProduct(position, cartItem);
            cartItem.setPrice(cartItem.getPrice() * cartItem.getQuantity());
            callback.onItemClicked(position, cartItem);
        });

        holder.plusbtn.setOnClickListener(v -> {
            count = cartItem.getQuantity() + 1;
            if (count < 0) count = 0;
            cartItem.setQuantity(count);

            int foo = controller.getBadgeCount();
            foo = foo + 1;
            controller.setBadgeCount(foo);
            controller.addBadge(foo);

            holder.prqtty.setText(String.valueOf(count));
            cartItem.setPrice(cartItem.getPrice() * cartItem.getQuantity());
            callback.onItemClicked(position, cartItem);
        });

        holder.deletbtn.setOnClickListener(v -> deleteProduct(position, cartItem));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void resetProduct(int position, Product cartItem) {
        if (cartItem.getQuantity() == 0) {

            int foo = controller.getBadgeCount();
            foo = foo - cartItem.getQuantity();
            controller.setBadgeCount(foo);
            controller.addBadge(foo);

            cartItemList.get(position).setCartStatus("0");
            cartItemList.remove(position);
            prefManager.updateCartList(cartItem.getProdID());
            callback.onItemClicked(position, cartItem);
            notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteProduct(int position, Product cartItem) {

        int prodQtt = cartItem.getQuantity();
        int foo = controller.getBadgeCount();
        foo = foo - prodQtt;
        cartItem.setQuantity(0);

        controller.setBadgeCount(foo);
        controller.addBadge(foo);

        cartItemList.get(position).setCartStatus("0");
        cartItemList.remove(position);
        prefManager.updateCartList(cartItem.getProdID());
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