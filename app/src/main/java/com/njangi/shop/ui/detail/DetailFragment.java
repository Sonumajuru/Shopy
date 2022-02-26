package com.njangi.shop.ui.detail;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import com.njangi.shop.Controller;
import com.njangi.shop.R;
import com.njangi.shop.adapter.ImagePagerAdapter;
import com.njangi.shop.databinding.FragmentDetailBinding;
import com.njangi.shop.db.FavDB;
import com.njangi.shop.helper.FirebaseApp;
import com.njangi.shop.helper.PrefManager;
import com.njangi.shop.model.FavItem;
import com.njangi.shop.model.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.njangi.shop.R.id.navigation_login;
import static com.njangi.shop.R.id.navigation_profile;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;

    private FavDB favDB;
    private Controller controller;
    private PrefManager prefManager;

    private JSONObject json;
    private Product product;
    private FavItem favItem;

    private ImageView favBtn;

    private int counter;
    private String uid;
    private String item_fav_status = null;
    private String imageList;
    private List<String> images;
    private List<FavItem> favItemList;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DetailViewModel detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        controller = Controller.getInstance(requireContext());
        prefManager = new PrefManager(requireContext());
        FirebaseApp firebaseApp = new FirebaseApp();
        product = new Product();
        favItemList = new ArrayList<>();
        favDB = new FavDB(getActivity());
        json = new JSONObject();
        images = new ArrayList<>();
        counter = 0;
        controller.setNavView(requireActivity().findViewById(R.id.nav_view));

        ImagePagerAdapter imagePagerAdapter;
        ViewPager viewPager = binding.imagePager;
        Button btnAddToCart = binding.addToCartBtn;
        TextView productOwner = binding.productOwner;
        TextView price = binding.priceOfProduct;
        TextView title = binding.title;
        RatingBar ratingBar = binding.ratingBar;
        TextView description = binding.description;
        TabLayout tabLayout = binding.tabDots;
        favBtn = binding.favBtn;

        controller.setTextLength(title);
        assert getArguments() != null;
        product = getArguments().getParcelable("product");

        if (product != null)
        {
            imagePagerAdapter = new ImagePagerAdapter(requireContext(), DetailFragment.this, product.getImages());

            price.setText(String.format("%.2f", product.getPrice()) + " " + product.getCurrency());
            title.setText(product.getTitle());
            ratingBar.setRating((float) product.getRating());
            description.setText(product.getDescription());
            uid = product.getUuid();
        }
        else
        {
            favItem = getArguments().getParcelable("favItem");
            imagePagerAdapter = new ImagePagerAdapter(requireContext(), DetailFragment.this, favItem.getImages());

            price.setText(String.format("%.2f", favItem.getPrice()) + " " + favItem.getCurrency());
            title.setText(favItem.getTitle());
            ratingBar.setRating((float) favItem.getRating());
            description.setText(favItem.getDescription());
            uid = favItem.getUuid();
        }

        viewPager.setAdapter(imagePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);

        Resources res = getResources();
        String text = res.getString(R.string.product_owner);
        productOwner.setPaintFlags(productOwner.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        productOwner.setText(text);

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
            getFav();
        }
        else
        {
            favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
        }

        favBtn.setOnClickListener(v -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null)
            {
                if (product != null)
                {
                    if (product.getFavStatus().equals("0"))
                    {
                        try {
                            json.put("images", new JSONArray(product.getImages()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imageList = json.toString();

                        product.setFavStatus("1");
                        favDB.insertIntoTheDatabase(product.getTitle(),
                                product.getDescription(),
                                product.getSeller(),
                                imageList,
                                product.getId(),
                                product.getFavStatus(),
                                product.getPrice(),
                                product.getRating(),
                                product.getCurrency(),
                                product.getUuid(),
                                product.getCategory(), "false");
                        favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                    }
                    else
                    {
                        product.setFavStatus("0");
                        favDB.remove_fav(product.getId());
                        favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                    }
                }
                else
                {
                    if (item_fav_status != null && item_fav_status.equals("0"))
                    {
                        try {
                            json.put("images", new JSONArray(favItem.getImages()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imageList = json.toString();
                        favItem.setFavStatus("1");
                        favDB.insertIntoTheDatabase(favItem.getTitle(),
                                favItem.getDescription(),
                                favItem.getCategory(),
                                imageList,
                                favItem.getKey_id(),
                                favItem.getFavStatus(),
                                favItem.getPrice(),
                                favItem.getRating(),
                                favItem.getCurrency(),
                                favItem.getUuid(),
                                favItem.getCategory(), "false");
                        favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                    }
                    {
                        favItem.setFavStatus("0");
                        favDB.remove_fav(favItem.getKey_id());
                        favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                    }
                }
            }
            else
            {
                Navigation.findNavController(v).navigate(navigation_login);
            }
        });
        btnAddToCart.setOnClickListener(view -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null)
            {
                if (prefManager.getQuantity() != 0)
                {
                    counter = prefManager.getQuantity() + 1;
                    controller.addBadge(counter);
                }
                else
                {
                    counter = counter + 1;
                    controller.addBadge(counter);
                }

                if (product != null)
                {
                    try {
                        json.put("images", new JSONArray(product.getImages()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imageList = json.toString();
                    favDB.insertIntoTheDatabase(product.getTitle(),
                            product.getDescription(),
                            product.getSeller(),
                            imageList,
                            product.getId(),
                            "0",
                            product.getPrice(),
                            product.getRating(),
                            product.getCurrency(),
                            product.getUuid(),
                            product.getCategory(), "1");
                    saveProdDetails(counter);
                }
                else
                {
                    try {
                        json.put("images", new JSONArray(favItem.getImages()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imageList = json.toString();
                    favDB.insertIntoTheDatabase(favItem.getTitle(),
                            favItem.getDescription(),
                            favItem.getSeller(),
                            imageList,
                            favItem.getKey_id(),
                            "0",
                            favItem.getPrice(),
                            favItem.getRating(),
                            favItem.getCurrency(),
                            favItem.getUuid(),
                            favItem.getCategory(), "1");
                    saveProdDetails(counter);
                }
            }
            else
            {
                Navigation.findNavController(view).navigate(navigation_login);
            }
        });
        productOwner.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("uid", uid);
            Navigation.findNavController(view).navigate(navigation_profile, bundle);
        });

        return root;
    }

    @SuppressLint("Range")
    private void getFav()
    {
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                item_fav_status = cursor.getString(cursor.getColumnIndex(FavDB.FAVORITE_STATUS));
                String title = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavDB.KEY_ID));
                JSONObject json = new JSONObject(cursor.getString(cursor.getColumnIndex(String.valueOf(FavDB.ITEM_IMAGE))));
                JSONArray jArray = json.optJSONArray("images");
                for (int i = 0; i < Objects.requireNonNull(jArray).length(); i++) {
                    images.add(jArray.optString(i));  //<< jget value from jArray
                }
                double price = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_PRICE));
                double rating = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_RATING));
                String currency = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CURRENCY));
                String favStatus = cursor.getString(cursor.getColumnIndex(FavDB.FAVORITE_STATUS));
                String uuid = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_UUID));
                String desc = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CATEGORY));
                String seller = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_SELLER));
                FavItem favItem = new FavItem(title, seller, desc, id, images, price, rating, currency, uuid, category, favStatus);
                favItemList.add(favItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        if (product != null)
        {
            for (FavItem favItem : favItemList) {
                if (product.getId().equals(favItem.getKey_id()))
                {
                    favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                    break;
                }
                favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
            }
        }
        else
        {
            if (item_fav_status != null && item_fav_status.equals("1")) {
                favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
            }
            else if (item_fav_status != null && item_fav_status.equals("0")) {
                favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
            }
        }
    }

    private void saveProdDetails(int quantity) {
        prefManager.saveQuantity(quantity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}