package com.genesistech.njangi.ui.overview;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.ProductAdapter;
import com.genesistech.njangi.databinding.FragmentOverviewBinding;
import com.genesistech.njangi.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.genesistech.njangi.R.id.navigation_detail;

public class OverviewFragment extends Fragment {

    FragmentOverviewBinding binding;
    //a list to store all the products
    private List<Product> productList;

    //the recyclerview
    private RecyclerView recyclerView;
    private Product product;
    private String category;
    private ArrayList<Parcelable> mList;
    private ProductAdapter adapter;
    private FragmentCallback callback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        OverviewViewModel overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //getting the recyclerview from xml
        recyclerView = binding.recyclerView;
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productList = new ArrayList<>();

        assert getArguments() != null;
        category = getArguments().getString(requireContext().getResources().getString(R.string.category));
        if (category != null)
        {
            getUserData();
        }
        else
        {
            mList = new ArrayList<>();
            mList = getArguments().getParcelableArrayList(requireContext().getResources().getString(R.string.feeling_lucky));
            getData();
        }

        return root;
    }

    private void getUserData()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("ProductDB").child("products");
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

                Collections.reverse(productList);
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

                    @Override
                    public void onItemClicked(int position, Object object, int id) {

                    }
                };
                adapter = new ProductAdapter(getActivity(), OverviewFragment.this, productList, callback);

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

    private void getData()
    {
        for (Parcelable parcelable : mList) {
            productList.add((Product) parcelable);
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
                Navigation.findNavController(requireView()).navigate(navigation_detail, bundle);
            }

            @Override
            public void onItemClicked(int position, Object object, int id) {

            }
        };
        adapter = new ProductAdapter(getActivity(), OverviewFragment.this, productList, callback);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}