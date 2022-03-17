package com.genesistech.njangi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.*;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.helper.PrefManager;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;
import com.genesistech.njangi.ui.boutique.BoutiqueFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context mCtx;
    public int clickedPos = -1;
    public FragmentCallback callback;
    private final Controller controller;
    private final List<Product> productList;
    private final PrefManager prefManager;
    private final JSONObject json;
    private String imageList;
    private final Fragment fragment;

    //getting the context and product list with constructor
    public ProductAdapter(Context mCtx, Fragment fragment, List<Product> productList, FragmentCallback callback) {
        this.mCtx = mCtx;
        controller = Controller.getInstance(mCtx);
        prefManager = new PrefManager(mCtx);
        this.productList = productList;
        this.callback = callback;
        this.fragment = fragment;
        json = new JSONObject();
    }

    @SuppressLint("InflateParams")
    @NotNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(R.layout.product_items, null);
        return new ProductViewHolder(view);
    }

    @Override
    @SuppressLint({"RecyclerView", "SetTextI18n", "DefaultLocale", "ResourceType", "NonConstantResourceId"})
    public void onBindViewHolder(@NotNull ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Product product = productList.get(position);
        checkIfItemIsFav(product, holder);

        if (fragment instanceof BoutiqueFragment)
        {
            holder.txtMore.setVisibility(View.VISIBLE);
            holder.favBtn.setVisibility(View.GONE);
            holder.txtMore.setOnClickListener(v -> {
                // Initializing the popup menu and giving the reference as current context
                Context wrapper = new ContextThemeWrapper(mCtx, R.xml.popup_menu);
                PopupMenu popupMenu = new PopupMenu(wrapper, holder.txtMore);
                popupMenu.setGravity(Gravity.END);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    popupMenu.getMenu().setGroupDividerEnabled(true);
                }

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.xml.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    // Toast message on menu item clicked
                    int id = menuItem.getItemId();
                    switch(id)
                    {
                        case R.id.update:
                        case R.id.delete:
                            callback.onItemClicked(position, product, id);
                            return true;
                        default:
                            return false;
                    }
                });
                // Showing the popup menu
                popupMenu.setForceShowIcon(true);
                popupMenu.show();
            });
        }

        //binding the data with the viewHolder views
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewShortDesc.setText(product.getDescription());
        holder.textViewRating.setRating((float) product.getRating());
        holder.textViewPrice.setText(String.format("%.2f", product.getPrice()) + " " + product.getCurrency());

        Uri uri = Uri.parse(product.getImages().get(0));
        Picasso.with(mCtx).load(uri).into(holder.imageView);
        holder.favBtn.setOnClickListener(v -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                //User is Logged in
                if (product.getFavStatus().equals("0"))
                {
                    try {
                        json.put("images", new JSONArray(product.getImages()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imageList = json.toString();
                    product.setFavStatus("1");
                    List<Product> productList = new ArrayList<>();
                    productList.add(product);
                    prefManager.saveFavList(productList, product.getProdID());
                    holder.favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                    Toast.makeText(mCtx, product.getTitle() + " Added to favorite!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    product.setFavStatus("0");
                    prefManager.updateFavList(product.getProdID());
                    holder.favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                }
            }
            else {
                //No User is Logged in
                Navigation.findNavController(v).navigate(R.id.navigation_login);
            }
        });
        holder.imageView.setOnClickListener(v -> {
            clickedPos = holder.getAbsoluteAdapterPosition();
            String title = product.getTitle();
            callback.onItemClicked(position, product);
        });
    }

    public void removeAt(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productList.size());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        RatingBar textViewRating;
        TextView textViewTitle, textViewShortDesc, textViewPrice, txtMore;
        ImageView imageView, favBtn;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.ratingBar);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
            txtMore = itemView.findViewById(R.id.txtMore);
            favBtn = itemView.findViewById(R.id.fav_btn);

            controller.setTextLength(textViewTitle);
            favBtn.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                final Product favItem = productList.get(position);
                removeItem(position);
                prefManager.updateFavList(favItem.getProdID());
            });
        }
    }

    private void checkIfItemIsFav(Product product, ProductViewHolder holder) {
        for (int i = 0; i < prefManager.getFavList().size(); i++) {
            if (prefManager.getFavList().get(i).getProdID().equals(product.getProdID())) {
                product.setFavStatus(prefManager.getFavList().get(i).getFavStatus());
            }
        }
        if (product != null) {
            if (product.getFavStatus().equals("1")) {
                holder.favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
            }
            else {
                holder.favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
            }
        }
    }

    private void removeItem(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,productList.size());
    }
}