package com.example.shopy.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.shopy.R;
import com.example.shopy.adapter.ListViewAdapter;
import com.example.shopy.databinding.FragmentSearchBinding;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;

    // Declare Variables
    private ListView list;
    private ListViewAdapter adapter;
    private SearchView searchView;
    private ArrayList<Product> arraylist = new ArrayList<>();

    private ArrayList<Product> productList = new ArrayList<>();
    private Product product;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        product = new Product();
        mAuth = FirebaseAuth.getInstance();

        searchView = binding.search;
        list = binding.listview;

        // Generate sample data
//        productNameList = new String[]{"Lion", "Tiger", "Dog",
//                "1", "Tortoise", "Rat", "Elephant"};

//        for (int i = 0; i < productNameList.length; i++) {
//            Product productNames = new Product("Lion", "Tiger", "Dog",
//                    1, "Tortoise", "Rat", "Elephant");
//            // Binds all strings into an array
//            arraylist.add(productNames);
//        }

        getUserData();

        searchView.setOnQueryTextListener(this);

        return root;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
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
                for (Product productNames : productList) {
                    // Binds all strings into an array
                    arraylist.add(productNames);
                }

                // Pass results to ListViewAdapter Class
                adapter = new ListViewAdapter(requireActivity(), arraylist);
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