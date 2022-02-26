package com.example.shopy.ui.stock;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.R;
import com.example.shopy.adapter.ProductAdapter;
import com.example.shopy.databinding.FragmentStockBinding;
import com.example.shopy.interfaces.FragmentCallback;
import com.example.shopy.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.shopy.R.id.navigation_detail;

public class StockFragment extends Fragment {

    private StockViewModel stockViewModel;
    private FragmentStockBinding binding;

    //a list to store all the products
    private List<Product> productList;

    //the recyclerview
    private RecyclerView recyclerView;
    private Product product;
    private ProductAdapter adapter;
    private FragmentCallback callback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //getting the recyclerview from xml
        recyclerView = binding.recyclerView;
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productList = new ArrayList<>();
        getUserData();

        return root;
    }

    private void getUserData()
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("ProductDB").child("user-products")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance()
                        .getCurrentUser())
                        .getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        product = ds.getValue(Product.class);
                        assert product != null;
                        productList.add(product);
                    }

                    callback = new FragmentCallback() {
                        @Override
                        public void doSomething() {
                        }

                        @Override
                        public void onItemClicked(int position, Object object) {

                            // Handle Object of list item here
                            Product product = (Product) object;
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("product", product);
                            NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                                    .findFragmentById(R.id.nav_host_fragment_activity_main);
                            assert navHostFragment != null;
                            NavController navController = navHostFragment.getNavController();
                            navController.navigate(navigation_detail, bundle);
                        }

                        @SuppressLint("NonConstantResourceId")
                        @Override
                        public void onItemClicked(int position, Object object, int id) {

                            Product product = (Product) object;
                            if (id == R.id.update)
                            {

                                Toast.makeText(getContext(), "Clicked " + product.getTitle(), Toast.LENGTH_SHORT).show();
                            } else if (id == R.id.delete) {
                                deleteProduct(product.getProdID());
                            }
                        }
                    };
                    Collections.reverse(productList);
                    adapter = new ProductAdapter(getActivity(), StockFragment.this, productList, callback);

                    //setting adapter to recyclerview
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteProduct(final String prodId){

        try
        {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("ProductDB").child("products");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds :dataSnapshot.getChildren()){
                        Product product = ds.getValue(Product.class);
                        assert product != null;
                        if (product.getProdID().equals(prodId))
                        {
                            ds.getRef().removeValue();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            dbRef = FirebaseDatabase.getInstance().getReference()
                    .child("ProductDB")
                    .child("user-products")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds :dataSnapshot.getChildren()){
                        Product product = ds.getValue(Product.class);
                        assert product != null;
                        if (product.getProdID().equals(prodId))
                        {
                            ds.getRef().removeValue();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}