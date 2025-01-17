package com.genesistech.njangi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.List;
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private static final String TAG = "HomeAdapter";
    private final Context mCtx;
    private final Controller controller;
    private final FragmentCallback callback;
    private final List<Product> productList;
    public HomeAdapter(Context context, List<Product> productList, FragmentCallback callback) {
        mCtx = context;
        controller = Controller.getInstance(context);
        this.productList = productList;
        this.callback = callback;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_view_items, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Product product = productList.get(position);

        Uri uri = Uri.parse(product.getImages().get(0));
        Picasso.with(mCtx).load(uri).into(holder.image);
        holder.title.setText(product.getTitle());
        holder.price.setText(String.format("%.2f", product.getPrice()) + " " + product.getCurrency());

        holder.image.setOnClickListener(view -> {
            Log.d(TAG, "onClick: clicked on an image: " + productList.get(position).getImages());
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", product);
            Navigation.findNavController(view).navigate(R.id.navigation_detail, bundle);
            callback.doSomething(); //<-- This is how you call callback method
        });
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title, price;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);

            controller.setTextLength(title);
        }
    }
}