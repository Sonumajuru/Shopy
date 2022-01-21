package com.example.shopy.ui.search;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.shopy.adapter.SearchAdapter;
import com.example.shopy.adapter.SuggestionAdapter;
import com.example.shopy.databinding.FragmentSearchBinding;
import com.example.shopy.db.SuggestionsDatabase;
import com.example.shopy.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;

    private ListView list;
    private SearchAdapter adapter;
    private final ArrayList<Product> arraylist = new ArrayList<>();
    private final ArrayList<Product> productList = new ArrayList<>();
    private SuggestionsDatabase database;
    private SearchView searchView;
    private Product product;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        product = new Product();
        database = new SuggestionsDatabase(requireActivity());
        searchView = binding.search;
        list = binding.listview;
        list.setVisibility(View.INVISIBLE);

        getUserData();

        searchView.setOnClickListener(v -> searchView.setIconified(false));
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        searchView.setQueryRefinementEnabled(true);
        searchView.requestFocus(1);

        list.setOnItemClickListener((parent, view, position, id) -> {
            Product product = arraylist.get(position);
            // -------------------------------------------------------
            //           Launch display of product in detail fragment
            //           If product is more than one display categories fragment of it
            // -------------------------------------------------------
            String favorite_message = product.getId();
        });

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

                // Binds all strings into an array
                arraylist.addAll(productList);

                // Pass results to ListViewAdapter Class
                adapter = new SearchAdapter(requireActivity(), arraylist);
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

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        SQLiteCursor cursor = (SQLiteCursor) searchView.getSuggestionsAdapter().getItem(position);
        int indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);
        searchView.setQuery(cursor.getString(indexColumnSuggestion), false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // -------------------------------------------------------
        //           Lines responsible to save the query
        //           do something on text submit
        // -------------------------------------------------------
        database.insertSuggestion(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(!newText.isEmpty())
        {
            list.setVisibility(View.VISIBLE);
            adapter.filter(newText);
            Cursor cursor = database.getSuggestions(newText);
            if(cursor.getCount() != 0)
            {
                String[] columns = new String[] {SuggestionsDatabase.FIELD_SUGGESTION };
                int[] columnTextId = new int[] { android.R.id.text1};

                SuggestionAdapter simple = new SuggestionAdapter(requireActivity().getBaseContext(),
                        android.R.layout.simple_list_item_1, cursor,
                        columns , columnTextId
                        , 0);

                searchView.setSuggestionsAdapter(simple);
                return true;
            }
            else
            {
                return false;
            }

        } else {
            list.setVisibility(View.INVISIBLE);
            return false;
        }
    }
}