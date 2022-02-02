package com.example.shopy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.interfaces.FragmentCallback;
import com.example.shopy.R;
import com.example.shopy.db.FavDB;
import com.example.shopy.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context mCtx;
    //we are storing all the products in a list
    private final List<Product> productList;
    public int clickedPos = -1;
    public FragmentCallback callback;
    private FavDB favDB;

    //getting the context and product list with constructor
    public ProductAdapter(Context mCtx, List<Product> productList, FragmentCallback callback) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.callback = callback;
    }

    @SuppressLint("InflateParams")
    @NotNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        favDB = new FavDB(mCtx);
        //create table on first
        SharedPreferences prefs = mCtx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if (firstStart) {
            createTableOnFirstStart();
        }

        View view = inflater.inflate(R.layout.product_items, null);
        return new ProductViewHolder(view);
    }

    @Override
    @SuppressLint({"RecyclerView", "SetTextI18n"})
    public void onBindViewHolder(@NotNull ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Product product = productList.get(position);
        readCursorData(product, holder);

        //binding the data with the viewHolder views
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewShortDesc.setText(product.getShortDesc());
        holder.textViewRating.setRating((float) product.getRating());
        holder.textViewPrice.setText(product.getPrice() + " " + product.getCurrency());
        Uri uri = Uri.parse(product.getImageUrl());
        Picasso.with(mCtx).load(uri).into(holder.imageView);

        holder.favBtn.setOnClickListener(v -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null)
            {
                //User is Logged in
                if (product.getFavStatus().equals("0"))
                {
                    product.setFavStatus("1");
                    favDB.insertIntoTheDatabase(product.getTitle(), product.getShortDesc(),
                            product.getImageUrl(), product.getId(), product.getFavStatus(),
                            product.getPrice(), product.getRating(), product.getCurrency(),
                            product.getUuid(), product.getCategory(), "0");
                    holder.favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                }
                else
                {
                    product.setFavStatus("0");
                    favDB.remove_fav(product.getId());
                    holder.favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                }
            }
            else
            {
                //No User is Logged in
                Navigation.findNavController(v).navigate(R.id.navigation_login);
            }
        });
        holder.imageView.setOnClickListener(v -> {
            clickedPos = holder.getAdapterPosition();
            String title = product.getTitle();
            callback.onItemClicked(position, product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        RatingBar textViewRating;
        TextView textViewTitle, textViewShortDesc, textViewPrice;
        ImageView imageView, favBtn;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.ratingBar);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
            favBtn = itemView.findViewById(R.id.fav_btn);
        }
    }

    private void createTableOnFirstStart() {
        favDB.insertEmpty();

        SharedPreferences prefs = mCtx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void readCursorData(Product product, ProductViewHolder holder) {
        Cursor cursor = favDB.read_all_data(product.getId());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                @SuppressLint("Range")
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavDB.FAVORITE_STATUS));
                product.setFavStatus(item_fav_status);

                //check fav status
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    if (item_fav_status != null && item_fav_status.equals("1")) {
                        holder.favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                    }
                    else if (item_fav_status != null && item_fav_status.equals("0")) {
                        holder.favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                    }
                }
                else
                {
                    holder.favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }
    }
}