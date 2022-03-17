package com.genesistech.njangi.adapter;

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
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.helper.PrefManager;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private final Context context;
    private final Controller controller;
    private final FragmentCallback callback;
    private static List<Product> favItemList;
    private final PrefManager prefManager;

    public FavAdapter(List<Product> favItemList, Context context, FragmentCallback callback) {
        this.context = context;
        prefManager = new PrefManager(context);
        controller = Controller.getInstance(context);
        FavAdapter.favItemList = favItemList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favourites_items,viewGroup,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product favItem = favItemList.get(position);
        //binding the data with the viewHolder views
        holder.textViewTitle.setText(favItemList.get(position).getTitle());
        holder.textViewRating.setRating((float) favItemList.get(position).getRating());
        holder.textViewPrice.setText(favItemList.get(position).getPrice() + " " + favItemList.get(position).getCurrency());

        Picasso.with(context).load(favItemList.get(position).getImages().get(0)).into(holder.imageView);
        holder.imageView.setOnClickListener(v -> callback.onItemClicked(position, favItem));
    }

    @Override
    public int getItemCount() {
        return favItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RatingBar textViewRating;
        TextView textViewTitle, textViewPrice;
        ImageView imageView, favBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewRating = itemView.findViewById(R.id.ratingBar);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
            favBtn = itemView.findViewById(R.id.fav_btn);

            controller.setTextLength(textViewTitle);
            favBtn.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                final Product favItem = favItemList.get(position);
                removeItem(position);
                prefManager.updateFavList(favItem.getProdID());
            });
        }
    }

    private void removeItem(int position) {
        favItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,favItemList.size());
    }
}