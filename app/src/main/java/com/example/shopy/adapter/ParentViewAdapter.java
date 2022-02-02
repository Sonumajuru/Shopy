package com.example.shopy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.interfaces.FragmentCallback;
import com.example.shopy.R;
import com.example.shopy.model.ParentModel;
import com.example.shopy.model.Product;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ParentViewAdapter extends RecyclerView.Adapter<ParentViewAdapter.MyViewHolder> {
    private final ArrayList<ParentModel> parentModelArrayList;
    private final List<Product> productList;
    public Context cxt;
    private final FragmentCallback callback;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public RecyclerView childRecyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.product_category);
            childRecyclerView = itemView.findViewById(R.id.Child_RV);
        }
    }

    public ParentViewAdapter(ArrayList<ParentModel> exampleList, List<Product> productList, Context context, FragmentCallback callback) {
        this.parentModelArrayList = exampleList;
        this.productList = productList;
        this.cxt = context;
        this.callback = callback;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return parentModelArrayList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ParentModel currentItem = parentModelArrayList.get(position);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(cxt, LinearLayoutManager.HORIZONTAL, false);
        holder.childRecyclerView.setLayoutManager(layoutManager);
        holder.childRecyclerView.setHasFixedSize(true);

        holder.category.setText(currentItem.getCategory());
        ArrayList<Product> arrayList = new ArrayList<>();

        // added the first child row
        if (parentModelArrayList.get(position).getCategory().equals(cxt.getString(R.string.electronics))) {
            for (Product product : productList) {
                if (product.getCategory().equals(cxt.getString(R.string.electronics)))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getTitle(), product.getCategory(),
                            product.getPrice(), product.getCurrency(),
                            product.getShortDesc(), product.getImageUrl(),
                            product.getRating(), product.getFavStatus()));
                }
            }
        }

        if (parentModelArrayList.get(position).getCategory().equals(cxt.getString(R.string.clothing))) {
            for (Product product : productList) {
                if (product.getCategory().equals(cxt.getString(R.string.clothing)))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getTitle(), product.getCategory(),
                            product.getPrice(), product.getCurrency(),
                            product.getShortDesc(), product.getImageUrl(),
                            product.getRating(), product.getFavStatus()));
                }
            }
        }

        // added in second child row
        if (parentModelArrayList.get(position).getCategory().equals(cxt.getString(R.string.phones))) {
            for (Product product : productList) {
                if (product.getCategory().equals(cxt.getString(R.string.phones)))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getTitle(), product.getCategory(),
                            product.getPrice(), product.getCurrency(),
                            product.getShortDesc(), product.getImageUrl(),
                            product.getRating(), product.getFavStatus()));
                }
            }
        }

        // added in third child row
        if (parentModelArrayList.get(position).getCategory().equals(cxt.getString(R.string.home_appliance))) {
            for (Product product : productList) {
                if (product.getCategory().equals(cxt.getString(R.string.home_appliance)))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getTitle(), product.getCategory(),
                            product.getPrice(), product.getCurrency(),
                            product.getShortDesc(), product.getImageUrl(),
                            product.getRating(), product.getFavStatus()));
                }
            }
        }

        // added in fourth child row
        if (parentModelArrayList.get(position).getCategory().equals(cxt.getString(R.string.games))) {
            for (Product product : productList) {
                if (product.getCategory().equals(cxt.getString(R.string.games)))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getTitle(), product.getCategory(),
                            product.getPrice(), product.getCurrency(),
                            product.getShortDesc(), product.getImageUrl(),
                            product.getRating(), product.getFavStatus()));
                }
            }
        }

        // added in fifth child row
        if (parentModelArrayList.get(position).getCategory().equals(cxt.getString(R.string.books))) {
            for (Product product : productList) {
                if (product.getCategory().equals(cxt.getString(R.string.books)))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getTitle(), product.getCategory(),
                            product.getPrice(), product.getCurrency(),
                            product.getShortDesc(), product.getImageUrl(),
                            product.getRating(), product.getFavStatus()));
                }
            }
        }

        // added in sixth child row
        if (parentModelArrayList.get(position).getCategory().equals(cxt.getString(R.string.computer))) {
            for (Product product : productList) {
                if (product.getCategory().equals(cxt.getString(R.string.computer)))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getTitle(), product.getCategory(),
                            product.getPrice(), product.getCurrency(),
                            product.getShortDesc(), product.getImageUrl(),
                            product.getRating(), product.getFavStatus()));
                }
            }
        }

        HomeAdapter childRecyclerViewAdapter = new HomeAdapter(holder.childRecyclerView.getContext(), arrayList, callback);
        holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);
    }

}