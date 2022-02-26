package com.njangi.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.njangi.shop.Controller;
import com.njangi.shop.interfaces.FragmentCallback;
import com.njangi.shop.R;
import com.njangi.shop.db.FavDB;
import com.njangi.shop.model.FavItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    Context context;
    private static FavDB favDB;
    private final Controller controller;
    private final FragmentCallback callback;
    private static List<FavItem> favItemList;

    public FavAdapter(List<FavItem> favItemList, Context context, FragmentCallback callback) {
        this.context = context;
        controller = Controller.getInstance(context);
        FavAdapter.favItemList = favItemList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favourites_items,viewGroup,false);
        favDB = new FavDB(context);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FavItem favItem = favItemList.get(position);
        //binding the data with the viewHolder views
        holder.textViewTitle.setText(favItemList.get(position).getTitle());
        holder.textViewRating.setRating((float) favItemList.get(position).getRating());
        holder.textViewPrice.setText(favItemList.get(position).getPrice() + " " + favItemList.get(position).getCurrency());

        Picasso.with(context).load(favItemList.get(position).getImages().get(0)).into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            callback.onItemClicked(position, favItem);
        });
    }

    @Override
    public int getItemCount() {
        return favItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RatingBar textViewRating;
        TextView textViewTitle, textViewPrice;
        ImageView imageView, favBtn;;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewRating = itemView.findViewById(R.id.ratingBar);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
            favBtn = itemView.findViewById(R.id.fav_btn);

            controller.setTextLength(textViewTitle);
            favBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();
                final FavItem favItem = favItemList.get(position);
                favDB.remove_fav(favItem.getKey_id());
                removeItem(position);
            });
        }
    }

    private void removeItem(int position) {
        favItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,favItemList.size());
    }
}