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
import com.example.shopy.R;
import com.example.shopy.model.Cart;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<Cart> cartList;
    private final Context mCtx;

    public CartAdapter(List<Cart> cartList, Context mCtx) {
        this.cartList = cartList;
        this.mCtx = mCtx;
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

        final Cart cart = cartList.get(position);

        holder.prname.setText(cart.getTitle());
        holder.prprice.setText(cart.getPrice() + " " + cart.getCurrency());
        Uri uri = Uri.parse(cart.getImageUrl());
        Picasso.with(mCtx).load(uri).into(holder.image);

        holder.minusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.deletbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartList.remove(position);
                notifyDataSetChanged();
//                MainActivity.myDatabase.cartDao().deleteItem(cart.getId());
//                int cartcount = MainActivity.myDatabase.cartDao().countCart();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder{
        private final ImageView image;
        private final ImageView deletbtn;
        private final ImageView minusbtn;
        private final ImageView plusbtn;
        private final TextView prname;
        private final TextView prprice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            prname = itemView.findViewById(R.id.idProductName);
            image = itemView.findViewById(R.id.idProductImage);
            prprice = itemView.findViewById(R.id.idProductPrice);
            minusbtn = itemView.findViewById(R.id.idMinusICon);
            plusbtn = itemView.findViewById(R.id.idPlusIcon);
            deletbtn = itemView.findViewById(R.id.idDeleteICon);
        }
    }
}