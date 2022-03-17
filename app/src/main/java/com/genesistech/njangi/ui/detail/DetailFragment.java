package com.genesistech.njangi.ui.detail;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.ImagePagerAdapter;
import com.genesistech.njangi.databinding.FragmentDetailBinding;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.helper.PrefManager;
import com.genesistech.njangi.model.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.genesistech.njangi.R.id.navigation_login;
import static com.genesistech.njangi.R.id.navigation_profile;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;

    private Controller controller;
    private PrefManager prefManager;

    private JSONObject json;
    private Product product;

    private ImageView favBtn;

    private int counter;
    private String uid;
    private String imageList;
    private List<String> images;
    private List<Product> productList;

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
        productList = new ArrayList<>();
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
        checkIfItemIsFav();

        if (product != null) {

            imagePagerAdapter = new ImagePagerAdapter(requireContext(), DetailFragment.this, product.getImages(), null);
            price.setText(String.format("%.2f", product.getPrice()) + " " + product.getCurrency());
            title.setText(product.getTitle());
            ratingBar.setRating((float) product.getRating());
            description.setText(product.getDescription());
            uid = product.getUuid();

            viewPager.setAdapter(imagePagerAdapter);
            tabLayout.setupWithViewPager(viewPager, true);
        }

        Resources res = getResources();
        String text = res.getString(R.string.product_owner);
        productOwner.setPaintFlags(productOwner.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        productOwner.setText(text);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (product != null) {
                if (product.getFavStatus().equals("1")) {
                    favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                }
                else {
                    favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                }
            }
        }
        else {
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
                        productList.add(product);
                        prefManager.initializeMyQuoteList(productList, product.getProdID());
                        favBtn.setBackgroundResource(R.drawable.ic_red_favorite_24);
                        Toast.makeText(getActivity(), product.getTitle() + " Added to favorite!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        product.setFavStatus("0");
                        prefManager.updateQuoteList(product.getProdID());
                        favBtn.setBackgroundResource(R.drawable.ic_favorite_border_24);
                    }
                }
            }
            else {
                Navigation.findNavController(v).navigate(navigation_login);
            }
        });
        btnAddToCart.setOnClickListener(view -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (prefManager.getQuantity() != 0) {
                    counter = prefManager.getQuantity() + 1;
                    controller.addBadge(counter);
                }
                else {
                    counter = counter + 1;
                    controller.addBadge(counter);
                }

                if (product != null) {
                    try {
                        json.put("images", new JSONArray(product.getImages()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imageList = json.toString();
                    saveProdDetails(counter);
                }
            }
            else {
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

    private void checkIfItemIsFav() {
        for (int i = 0; i < prefManager.getProductList().size(); i++) {
            if (prefManager.getProductList().get(i).getProdID().equals(product.getProdID())) {
                product.setFavStatus(prefManager.getProductList().get(i).getFavStatus());
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