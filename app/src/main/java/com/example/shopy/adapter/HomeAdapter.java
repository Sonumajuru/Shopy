package com.example.shopy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.shopy.R;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import static com.example.shopy.R.id.navigation_detail;

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

        getID(product.getUuid(), holder);
        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice() + " " + product.getCurrency());

        holder.image.setOnClickListener(view -> {
            Log.d(TAG, "onClick: clicked on an image: " + productList.get(position).getImageUrl());
//                Toast.makeText(mCtx, "Product ID: "+ productList.get(position).getId(), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", product);
            NavHost navHostFragment = (NavHostFragment) ((AppCompatActivity) mCtx).getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();
            navController.navigate(navigation_detail, bundle);
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

    private void getID(String uid, ViewHolder holder) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("User").child(uid).getRef();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                holder.seller.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }
}