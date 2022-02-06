package com.example.shopy.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.shopy.R;
import com.example.shopy.adapter.CategoryAdapter;
import com.example.shopy.databinding.FragmentCategoryBinding;
import com.example.shopy.helper.FirebaseApp;
import com.example.shopy.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CategoryFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    private FragmentCategoryBinding binding;
    private FirebaseApp firebaseApp;

    private CategoryAdapter adapter;
    private Product product;

    private ListView list;
    private ArrayList<Product> arraylist;
    private ArrayList<Product> productList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseApp = new FirebaseApp();
        product = new Product();
        arraylist = new ArrayList<>();
        productList = new ArrayList<>();
        list = binding.listview;

        list.setOnItemClickListener((parent, view, position, id) -> {
            Product product = (Product)parent.getAdapter().getItem(position);
            String category = product.getCategory();

            Bundle bundle = new Bundle();
            bundle.putString(requireActivity().getResources().getString(R.string.category), product.getCategory());
            Navigation.findNavController(view).navigate(R.id.navigation_overview, bundle);
        });

        getUserData();

        return root;
    }

    private void getUserData()
    {
        DatabaseReference eventsRef = firebaseApp.getFirebaseDB().getReference().child("Product");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    product = ds.getValue(Product.class);
                    productList.add(product);
                }

                ;
                Set<Product> set = productList.stream()
                        .collect(Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(Product::getCategory))));

                product.setProductList(productList);
                // Binds all strings into an array
                arraylist.addAll(set);

                // Pass results to ListViewAdapter Class
                adapter = new CategoryAdapter(requireActivity(), arraylist);
                // Binds the Adapter to the ListView
                list.setAdapter(adapter);
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