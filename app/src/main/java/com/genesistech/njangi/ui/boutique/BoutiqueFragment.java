package com.genesistech.njangi.ui.boutique;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.ProductAdapter;
import com.genesistech.njangi.databinding.FragmentStockBinding;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.genesistech.njangi.R.id.*;

public class BoutiqueFragment extends Fragment {

    private BoutiqueViewModel stockViewModel;
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
        String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("ProductDB").child("products")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        product = ds.getValue(Product.class);
                        assert product != null;
                        if (uuid.equals(product.getUuid()))
                        {
                            productList.add(product);
                        }
                    }

                    callback = new FragmentCallback() {
                        @Override
                        public void doSomething() {
                        }

                        @Override
                        public void onItemClicked(int position, Object object) {

                            Product product = (Product) object;
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("product", product);
                            Navigation.findNavController(requireView()).navigate(navigation_detail, bundle);
                        }

                        @SuppressLint("NonConstantResourceId")
                        @Override
                        public void onItemClicked(int position, Object object, int id) {

                            Product product = (Product) object;
                            if (id == R.id.update) {
//                                Toast.makeText(getContext(), "Clicked " + product.getTitle(), Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("product", product);
                                Navigation.findNavController(requireView()).navigate(navigation_product, bundle);
                            } else if (id == R.id.delete) {
                                deleteProduct(product.getProdID());
                            }
                        }
                    };
                    Collections.reverse(productList);
                    adapter = new ProductAdapter(getActivity(), BoutiqueFragment.this, productList, callback);

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
                            ds.child(prodId).getRef().removeValue();
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