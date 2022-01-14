package com.example.shopy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.shopy.R;
import com.example.shopy.model.Product;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final String TAG = "HomeAdapter";
    private final List<Product> productList;
    private Context mCtx;

    public HomeAdapter(Context context, List<Product> productList) {
        mCtx = context;
        this.productList = productList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_view_items, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Product product = productList.get(position);

        Uri uri = Uri.parse(product.getImageUrl());
        Picasso.with(mCtx).load(uri).into(holder.image);

        holder.seller.setText(product.getUuid());
        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice() + " " + product.getCurrency());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on an image: " + productList.get(position).getImageUrl());
                Toast.makeText(mCtx, productList.get(position).getImageUrl(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView seller, title, price;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
            seller = itemView.findViewById(R.id.seller);
            price = itemView.findViewById(R.id.price);
        }
    }
}