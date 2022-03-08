package com.genesistech.njangi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.R;
import com.genesistech.njangi.model.ParentModel;
import com.genesistech.njangi.model.Product;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ParentViewAdapter extends RecyclerView.Adapter<ParentViewAdapter.MyViewHolder> {

    public Context cxt;
    private final Controller controller;
    private final FragmentCallback callback;
    private final List<Product> productList;
    private final ArrayList<ParentModel> parentModelArrayList;

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
        controller = Controller.getInstance(context);
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

        holder.category.setText(controller.getTranslation(currentItem.getCategory()));
        ArrayList<Product> arrayList = new ArrayList<>();

        // added the first child row
        if (parentModelArrayList.get(position).getCategory().equals("Electronics")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Electronics"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(),product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        if (parentModelArrayList.get(position).getCategory().equals("Clothing")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Clothing"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in second child row
        if (parentModelArrayList.get(position).getCategory().equals("Phones")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Phones"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in third child row
        if (parentModelArrayList.get(position).getCategory().equals("Home Appliances")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Home Appliances"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in fourth child row
        if (parentModelArrayList.get(position).getCategory().equals("Games")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Games"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in fifth child row
        if (parentModelArrayList.get(position).getCategory().equals("Books")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Books"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in sixth child row
        if (parentModelArrayList.get(position).getCategory().equals("Computers")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Computers"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in seventh child row
        if (parentModelArrayList.get(position).getCategory().equals("Cars")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Cars"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in eight child row
        if (parentModelArrayList.get(position).getCategory().equals("Motorbike")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Motorbike"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in ninth child row
        if (parentModelArrayList.get(position).getCategory().equals("Shoes")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Shoes"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in tenth child row
        if (parentModelArrayList.get(position).getCategory().equals("Hairs")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Hairs"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in eleventh child row
        if (parentModelArrayList.get(position).getCategory().equals("Cosmetics")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Cosmetics"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in twelfth child row
        if (parentModelArrayList.get(position).getCategory().equals("Baby and Toys")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Baby and Toys"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        // added in thirteenth child row
        if (parentModelArrayList.get(position).getCategory().equals("Accessories")) {
            for (Product product : productList) {
                if (product.getCategory().equals("Accessories"))
                {
                    arrayList.add(new Product(product.getId(), product.getUuid(),
                            product.getSeller(), product.getTitle(),
                            product.getCategory(), product.getPrice(),
                            product.getCurrency(), product.getDescription(),
                            product.getImages(), product.getRating(),
                            product.getFavStatus(), product.getProdID(),
                            product.getStore(), product.getTrending()));
                }
            }
        }

        HomeAdapter childRecyclerViewAdapter = new HomeAdapter(holder.childRecyclerView.getContext(), arrayList, callback);
        holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);
    }
}