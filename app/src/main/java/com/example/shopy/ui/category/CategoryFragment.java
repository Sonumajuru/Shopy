package com.example.shopy.ui.category;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.shopy.R;
import com.example.shopy.adapter.ExpandableListViewAdapter;
import com.example.shopy.databinding.FragmentCategoryBinding;
import com.example.shopy.model.Product;
import com.google.firebase.database.*;

import java.util.*;

public class CategoryFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    private FragmentCategoryBinding binding;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;

    private Product product;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private List<String> electronics = new ArrayList<>();
    private List<String> computer = new ArrayList<>();
    private List<String> home_appliance = new ArrayList<>();
    private List<String> phones = new ArrayList<>();
    private List<String> books = new ArrayList<>();
    private List<String> games = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        expandableListView = root.findViewById(R.id.expandableListView);
        expandableListDetail = new HashMap<>();
        electronics = new ArrayList<>();
        computer = new ArrayList<>();
        home_appliance = new ArrayList<>();
        phones = new ArrayList<>();
        books = new ArrayList<>();
        games = new ArrayList<>();

        getUserData();

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Toast.makeText(
                    requireActivity().getApplicationContext(), expandableListTitle.get(groupPosition)
                            + " -> "
                            + Objects.requireNonNull(expandableListDetail.get(
                            expandableListTitle.get(groupPosition))).get(childPosition), Toast.LENGTH_SHORT
            ).show();
            return false;
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

                    if (Objects.requireNonNull(ds.getValue(Product.class)).getCategory().equals(getResStringLanguage(R.string.electronics, "en")))
                    {
                        electronics.add(Objects.requireNonNull(ds.getValue(Product.class)).getName());
                    }
                    if (Objects.requireNonNull(ds.getValue(Product.class)).getCategory().equals(getResStringLanguage(R.string.computer, "en")))
                    {
                        computer.add(Objects.requireNonNull(ds.getValue(Product.class)).getName());
                    }
                    if (Objects.requireNonNull(ds.getValue(Product.class)).getCategory().equals(getResStringLanguage(R.string.home_appliance, "en")))
                    {
                        home_appliance.add(Objects.requireNonNull(ds.getValue(Product.class)).getName());
                    }
                    if (Objects.requireNonNull(ds.getValue(Product.class)).getCategory().equals(getResStringLanguage(R.string.phones, "en")))
                    {
                        phones.add(Objects.requireNonNull(ds.getValue(Product.class)).getName());
                    }
                    if (Objects.requireNonNull(ds.getValue(Product.class)).getCategory().equals(getResStringLanguage(R.string.books, "en")))
                    {
                        books.add(Objects.requireNonNull(ds.getValue(Product.class)).getName());
                    }
                    if (Objects.requireNonNull(ds.getValue(Product.class)).getCategory().equals(getResStringLanguage(R.string.games, "en")))
                    {
                        games.add(Objects.requireNonNull(ds.getValue(Product.class)).getName());
                    }
                }
                expandableListDetail.put(getString(R.string.electronics), electronics);
                expandableListDetail.put(getString(R.string.computer), computer);
                expandableListDetail.put(getString(R.string.home_appliance), home_appliance);
                expandableListDetail.put(getString(R.string.phones), phones);
                expandableListDetail.put(getString(R.string.books), books);
                expandableListDetail.put(getString(R.string.games), games);

                expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                expandableListAdapter = new ExpandableListViewAdapter(requireActivity(), expandableListTitle, expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public String getResStringLanguage(int id, String lang){
        //Get default locale to back it
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.locale;
        //Retrieve resources from desired locale
        Configuration confAr = getResources().getConfiguration();
        confAr.locale = new Locale(lang);
        DisplayMetrics metrics = new DisplayMetrics();
        Resources resources = new Resources(requireActivity().getAssets(), metrics, confAr);
        //Get string which you want
        String string = resources.getString(id);
        //Restore default locale
        conf.locale = savedLocale;
        res.updateConfiguration(conf, null);
        //return the string that you want
        return string;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}