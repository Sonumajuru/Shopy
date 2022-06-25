package com.genesistech.njangi.ui.search;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.SearchAdapter;
import com.genesistech.njangi.adapter.SuggestionAdapter;
import com.genesistech.njangi.databinding.FragmentSearchBinding;
import com.genesistech.njangi.db.SuggestionsDatabase;
import com.genesistech.njangi.model.Product;
import com.google.firebase.database.*;

import java.util.*;
import java.util.stream.Collectors;
public class SearchFragment extends Fragment{
    private FragmentSearchBinding binding;
    private androidx.appcompat.widget.SearchView searchView;
    private Product product;
    private SearchAdapter adapter;
    private SuggestionsDatabase database;
    private ListView list;
    private ArrayList<Product> arraylist;
    private ArrayList<Product> productList;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        SearchViewModel searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        product = new Product();
        arraylist = new ArrayList<>();
        productList = new ArrayList<>();
        database = new SuggestionsDatabase(requireContext());
        searchView = binding.search;
        list = binding.listview;
        list.setVisibility(View.INVISIBLE);

        getUserData();

        searchView.setOnClickListener(v -> searchView.setIconified(false));
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // -------------------------------------------------------
                //           Lines responsible to save the query
                //           do something on text submit
                // -------------------------------------------------------
                database.insertSuggestion(query);
                getSearch(query);
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

                        SuggestionAdapter simple = new SuggestionAdapter(requireContext(),
                                android.R.layout.simple_list_item_1
                                , cursor
                                , columns
                                ,columnTextId
                                , 0);

//                        searchView.setSuggestionsAdapter(simple);
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
        });
        searchView.setOnSuggestionListener(new androidx.appcompat.widget.SearchView.OnSuggestionListener() {
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
        });
        searchView.setQueryRefinementEnabled(true);
        searchView.requestFocus(1);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) requireContext().getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        list.setOnItemClickListener((parent, view, position, id) -> {
            product = arraylist.get(position);
            String lastSearch = product.getTitle();

            // hashmap to store the frequency of element
            Map<String, Integer> map = new HashMap<>();

            for (Product value : productList) {
                String title = value.getTitle();
                Integer count = map.get(title);
                map.put(title, (count == null) ? 1 : count + 1);
            }

            // displaying the occurrence of elements in the arraylist
            for (Map.Entry<String, Integer> val : map.entrySet()) {
                System.out.println("Element " + val.getKey() + " " + "occurs" + ": " + val.getValue() + " times");
                if (val.getValue() == 1)
                {
                    if (lastSearch.equals(val.getKey()))
                    {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("product", product);
                        Navigation.findNavController(view).navigate(R.id.navigation_detail, bundle);
                        searchView.setQuery("", false);
                        searchView.setIconified(true);
                        searchView.clearFocus();
                    }
                }
                else
                {
                    ArrayList<Product> tempList = new ArrayList<>();
                    if (lastSearch.equals(val.getKey()))
                    {
                        for (Product product : productList) {
                            if (lastSearch.equals(product.getTitle())) {
                                tempList.add(product);
                            }
                        }
                        //Get all items > 2 in a list and submit into parceableList
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(requireContext()
                                .getResources()
                                .getString(R.string.feeling_lucky), tempList);
                        Navigation.findNavController(view).navigate(R.id.navigation_overview, bundle);
                        searchView.setQuery("", false);
                        searchView.setIconified(true);
                        searchView.clearFocus();
                    }
                }
            }
        });

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
                    productList.add(product);
                }

                Set<Product> set = productList.stream()
                        .collect(Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(Product::getTitle))));

                product.setProductList(productList);
                // Binds all strings into an array
                arraylist.addAll(set);

                // Pass results to ListViewAdapter Class
                adapter = new SearchAdapter(requireContext(), arraylist);
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
    private void getSearch(String mySearch)
    {
        // hashmap to store the frequency of element
        Map<String, Integer> map = new HashMap<>();

        for (Product value : productList) {
            String title = value.getTitle();
            Integer count = map.get(title);
            map.put(title, (count == null) ? 1 : count + 1);
        }

        // displaying the occurrence of elements in the arraylist
        for (Map.Entry<String, Integer> val : map.entrySet()) {
            System.out.println("Element " + val.getKey() + " " + "occurs" + ": " + val.getValue() + " times");
            if (mySearch.equals(val.getKey().toUpperCase())
                    || mySearch.equals(val.getKey().toLowerCase())
                    || mySearch.equals(val.getKey()))
            {
                if (val.getValue() == 1)
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("product", product);
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_detail, bundle);
                }
                else
                {
                    ArrayList<Product> tempList = new ArrayList<>();
                    for (Product product : productList) {
                        if (mySearch.equals(product.getTitle())) {
                            tempList.add(product);
                        }
                    }
                    //Get all items > 2 in a list and submit into parceableList
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(requireContext()
                            .getResources()
                            .getString(R.string.feeling_lucky), tempList);
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_overview, bundle);
                }
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchView.clearFocus();
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}