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
import com.example.shopy.adapter.SearchViewAdapter;
import com.example.shopy.databinding.FragmentSearchBinding;
import com.example.shopy.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;

    private ListView list;
    private SearchViewAdapter adapter;
    private SearchView searchView;
    private ArrayList<Product> arraylist = new ArrayList<>();
    private ArrayList<Product> productList = new ArrayList<>();
    private Product product;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        product = new Product();
        searchView = binding.search;
        list = binding.listview;

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
                // Binds all strings into an array
                arraylist.addAll(productList);

                // Pass results to ListViewAdapter Class
                adapter = new SearchViewAdapter(requireActivity(), arraylist);
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