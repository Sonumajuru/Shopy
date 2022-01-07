package com.example.shopy.ui.product_overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.R;
import com.example.shopy.adapter.ProductAdapter;
import com.example.shopy.databinding.FragmentOverviewBinding;
import com.example.shopy.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ProductOverviewFragment extends Fragment {

    private ProductOverviewViewModel productOverviewViewModel;
    private FragmentOverviewBinding binding;

    //a list to store all the products
    private List<Product> productList;

    //the recyclerview
    private RecyclerView recyclerView;

    private Product product;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        productOverviewViewModel = new ViewModelProvider(this).get(ProductOverviewViewModel.class);
        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //getting the recyclerview from xml
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        //initializing the productlist
        productList = new ArrayList<>();

        //adding some items to our list
//        productList.add(
//                new Product(
//                        1,
//                        "Apple iPhone 11",
//                        "6.1 inches, Color black 828 x 1792 pixels",
//                        4.8,
//                        90000,
//                        R.drawable.apple));
//
//        productList.add(
//                new Product(
//                        1,
//                        "Galaxy A12",
//                        "2.3GHz, 1.8GHz CPU, multi color, 205 kg",
//                        4.1,
//                        55000,
//                        R.drawable.samsung));
//
//        productList.add(
//                new Product(
//                        1,
//                        "Oppo F17",
//                        "6.44 inch, Color Peach, 6GB RAM , 163  kg",
//                        4.3,
//                        60000,
//                        R.drawable.oppo));
        getUserData();

        return root;
    }

    private void getUserData()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("Product");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    product = ds.getValue(Product.class);
                    productList.add(product);
                }
                //creating recyclerview adapter
                ProductAdapter adapter = new ProductAdapter(getActivity(), productList);

                //setting adapter to recyclerview
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }
}