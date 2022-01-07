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
    private String category;

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
        productList = new ArrayList<>();

        assert getArguments() != null;
        category = getArguments().getString("category");

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
                    assert product != null;
                    if (product.getCategory().equals(category))
                    {
                        productList.add(product);
                    }
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