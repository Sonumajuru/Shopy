package com.example.shopy.ui.detail;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.Uri;
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
import com.example.shopy.Controller;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentDetailBinding;
import com.example.shopy.db.FavDB;
import com.example.shopy.helper.FirebaseApp;
import com.example.shopy.helper.PrefManager;
import com.example.shopy.model.FavItem;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.shopy.R.id.navigation_login;
import static com.example.shopy.R.id.navigation_profile;

public class DetailFragment extends Fragment {

    private DetailViewModel detailViewModel;
    private FragmentDetailBinding binding;

    private FavDB favDB;
    private FirebaseApp firebaseApp;
    private Controller controller;
    private PrefManager prefManager;

    private Product product;
    private FavItem favItem;

    private TextView price;
    private TextView title;
    private TextView description;
    private TextView productOwner;
    private RatingBar ratingBar;
    private ImageView favBtn;
    private ImageView productPhoto;
    private Button btnAddToCart;

    private int counter;
    private String uid;
    private String item_fav_status = null;

    private List<FavItem> favItemList;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        controller = Controller.getInstance(requireContext());
        prefManager = new PrefManager(requireContext());
        firebaseApp = new FirebaseApp();
        product = new Product();
        favItemList = new ArrayList<>();
        favDB = new FavDB(getActivity());
        counter = 0;
        controller.setNavView(requireActivity().findViewById(R.id.nav_view));

        productPhoto = binding.photo;
        btnAddToCart = binding.addToCartBtn;
        productOwner = binding.productOwner;
        favBtn = binding.favBtn;
        price = binding.priceOfProduct;
        title = binding.title;
        ratingBar = binding.ratingBar;
        description = binding.description;

        controller.setTextLength(title);

        assert getArguments() != null;
        product = getArguments().getParcelable("product");
        if (product != null)
        {
            Uri uri = Uri.parse(product.getImageUrl());
            Picasso.with(getActivity()).load(uri).into(productPhoto);
            price.setText(String.format("%.2f", product.getPrice()) + " " + product.getCurrency());
            title.setText(product.getTitle());
            ratingBar.setRating((float) product.getRating());
            description.setText(product.getShortDesc());
            getID(product.getUuid());
            uid = product.getUuid();
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
            uid = favItem.getUuid();
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
                if (product != null)
                {
                    if (product.getFavStatus().equals("0"))
                    {
                        product.setFavStatus("1");
                        favDB.insertIntoTheDatabase(product.getTitle(), product.getShortDesc(),
                                product.getImageUrl(), product.getId(),
                                product.getFavStatus(), product.getPrice(), product.getRating(),
                                product.getCurrency(), product.getUuid(), product.getCategory(), "false");
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
                                favItem.getCurrency(), favItem.getUuid(), favItem.getCategory(), "false");
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
                counter = counter + 1;
                controller.addBadge(counter);
                if (product != null)
                {
//                    if (Objects.equals(prefManager.getProductPrefs(), product.getId()))
//                    {
//
//                    }
                    favDB.insertIntoTheDatabase(product.getTitle(),
                            product.getShortDesc(),
                            product.getImageUrl(),
                            product.getId(),
                            "0",
                            product.getPrice(),
                            product.getRating(),
                            product.getCurrency(),
                            product.getUuid(),
                            product.getCategory(), "1");
                    saveProdDetails(product.getId(), product.getUuid(), product.getTitle());
                }
                else
                {
                    favDB.insertIntoTheDatabase(favItem.getTitle(),
                            favItem.getShortDesc(),
                            favItem.getImageUrl(),
                            favItem.getKey_id(),
                            "0",
                            favItem.getPrice(),
                            favItem.getRating(),
                            favItem.getCurrency(),
                            favItem.getUuid(),
                            favItem.getCategory(), "1");
                    saveProdDetails(favItem.getKey_id(), favItem.getUuid(), favItem.getTitle());
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

    @SuppressLint("SetTextI18n")
    private void getID(String uid)
    {
        DatabaseReference eventsRef = firebaseApp.getFirebaseDB().getReference().child("User").child(uid).getRef();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Resources res = getResources();
                @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
                String text = String.format(res.getString(R.string.product_owner), user.getFirstName());
                productOwner.setPaintFlags(productOwner.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                productOwner.setText(text + ": " + user.getFirstName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void saveProdDetails(String id, String uuid, String name) {
        prefManager.saveProductDetails(id, uuid, name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}