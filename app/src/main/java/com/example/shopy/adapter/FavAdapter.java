package com.example.shopy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.R;
import com.example.shopy.db.FavDB;
import com.example.shopy.model.FavItem;
import com.example.shopy.model.Product;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {
    Context context;
    private static List<FavItem> favItemList;
    private static FavDB favDB;
    private static DatabaseReference refLike;
    private final FavAdapter.OnItemClickListener onItemClickListener; // Global scope

    public FavAdapter(List<FavItem> favItemList, Context context, FavAdapter.OnItemClickListener onItemClickListener) {
        this.context = context;
        FavAdapter.favItemList = favItemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favourites_view,viewGroup,false);
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

        Picasso.with(context).load(favItemList.get(position).getImageUrl()).into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            onItemClickListener.onItemClicked(position, favItem);
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

//            refLike = FirebaseDatabase.getInstance().getReference().child("Product");
            //remove from fav after click
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final FavItem favItem = favItemList.get(position);
//                    final DatabaseReference upvotesRefLike = refLike.child(favItemList.get(position).getKey_id());
                    favDB.remove_fav(favItem.getKey_id());
                    removeItem(position);
//                    upvotesRefLike.runTransaction(new Transaction.Handler() {
//                        @NonNull
//                        @Override
//                        public Transaction.Result doTransaction(@NonNull final MutableData mutableData) {
//                            try {
//                                Integer currentValue = mutableData.getValue(Integer.class);
//                                if (currentValue == null) {
//                                    mutableData.setValue(1);
//                                } else {
//                                    mutableData.setValue(currentValue - 1);
//                                }
//                            } catch (Exception e) {
//                                throw e;
//                            }
//                            return Transaction.success(mutableData);
//                        }
//
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//                            System.out.println("Transaction completed");
//                        }
//                    });
                }
            });
        }
    }

    private void removeItem(int position) {
        favItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,favItemList.size());
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, Object object);
    }
}