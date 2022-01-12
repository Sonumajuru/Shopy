package com.example.shopy.ui.detail;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentDetailBinding;
import com.example.shopy.db.FavDB;
import com.example.shopy.model.FavItem;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.shopy.R.id.navigation_login;

public class DetailFragment extends Fragment {

    private DetailViewModel detailViewModel;
    private FragmentDetailBinding binding;

    private FirebaseAuth mAuth;
    private Product product;
    private FavItem favItem;

    private ImageView productPhoto;
    private TextView productOwner;
    private ImageView favBtn;
    private TextView price;
    private TextView title;
    private RatingBar ratingBar;
    private TextView description;

    private FavDB favDB;
    private List<FavItem> favItemList;
    private List<Product> productList;
    String item_fav_status = null;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        product = new Product();
        favItemList = new ArrayList<>();
        favDB = new FavDB(getActivity());

        productPhoto = binding.photo;
        productOwner = binding.productOwner;
        favBtn = binding.favBtn;
        price = binding.priceOfProduct;
        title = binding.title;
        ratingBar = binding.ratingBar;
        description = binding.description;

        assert getArguments() != null;
        product = getArguments().getParcelable("product");
        if (product != null)
        {
            Uri uri = Uri.parse(product.getImageUrl());
            Picasso.with(getActivity()).load(uri).into(productPhoto);
            price.setText(product.getPrice() + " " + product.getCurrency());
            title.setText(product.getTitle());
            ratingBar.setRating((float) product.getRating());
            description.setText(product.getShortDesc());
            getID(product.getUuid());
        }
        else
        {
            favItem = getArguments().getParcelable("favItem");
            Uri uri = Uri.parse(favItem.getImageUrl());
            Picasso.with(getActivity()).load(uri).into(productPhoto);
            price.setText(favItem.getPrice() + " " + favItem.getCurrency());
            title.setText(favItem.getTitle());
            ratingBar.setRating((float) favItem.getRating());
            description.setText(favItem.getShortDesc());
            getID(favItem.getUuid());
        }

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
                //User is Logged in
                if (product != null)
                {
                    if (product.getFavStatus().equals("0"))
                    {
                        product.setFavStatus("1");
                        favDB.insertIntoTheDatabase(product.getTitle(), product.getShortDesc(),
                                product.getImageUrl(), product.getId(),
                                product.getFavStatus(), product.getPrice(), product.getRating(),
                                product.getCurrency(), product.getUuid(), product.getCategory());
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
                        favItem.setFavStatus("1");
                        favDB.insertIntoTheDatabase(favItem.getTitle(), favItem.getShortDesc(),
                                favItem.getImageUrl(), favItem.getKey_id(),
                                favItem.getFavStatus(), favItem.getPrice(), favItem.getRating(),
                                favItem.getCurrency(), favItem.getUuid(), product.getCategory());
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
                //No User is Logged in
                NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_activity_main);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.navigate(navigation_login);
            }
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
                String image = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_IMAGE));
                double price = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_PRICE));
                double rating = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_RATING));
                String currency = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CURRENCY));
                String uuid = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_UUID));
                String desc = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CATEGORY));
                FavItem favItem = new FavItem(title, desc, id, image, price, rating, currency, uuid, category);
                favItemList.add(favItem);
            }
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

    private void getID(String uid)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("User").child(uid).getRef();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Resources res = getResources();
                @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
                String text = String.format(res.getString(R.string.product_owner), user.getName());
                productOwner.setText(text + ": " + user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}