package com.example.shopy.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.example.shopy.helper.FirebaseApp;
import com.example.shopy.interfaces.FragmentCallback;
import com.example.shopy.R;
import com.example.shopy.adapter.ParentViewAdapter;
import com.example.shopy.adapter.SliderAdapter;
import com.example.shopy.databinding.FragmentHomeBinding;
import com.example.shopy.model.ParentModel;
import com.example.shopy.model.Product;
import com.google.firebase.database.*;

import java.util.*;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements FragmentCallback {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private FragmentActivity objects;

    //a list to store all the products
    private List<Product> productList;
    private List<Product> sliderList;
    private List<String> offerList;

    //the recyclerview
    private RecyclerView recyclerView;
    private FragmentCallback callback;
    private FirebaseApp firebaseApp;

    private Timer timer;
    private ViewPager page;
    private Product product;

    private SliderAdapter sliderAdapter;
    private ParentViewAdapter parentViewAdapter;

    private List<DataSnapshot> dataSnapshotList;
    private ArrayList<ParentModel> categoryList;
    private ArrayList<ParentModel> parentModelArrayList;
    private RecyclerView.LayoutManager parentLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseApp = new FirebaseApp();
        parentModelArrayList = new ArrayList<>();
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        sliderList = new ArrayList<>();
        offerList = new ArrayList<>();
        timer = new java.util.Timer();
        dataSnapshotList = new Stack<>();
        page = binding.viewPager;
        Button btnFeelLucky = binding.luckBtn;
        Button btnCategory = binding.categoryBtn;
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        parentLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(parentLayoutManager);

        getProducts();
        getSpecialOffers();

        homeViewModel.getUserName();
        final TextView title = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), title::setText);
        btnCategory.setOnClickListener(v -> {
            timer.cancel();
            timer.purge();
            Navigation.findNavController(v).navigate(R.id.navigation_category);
        });
        btnFeelLucky.setOnClickListener(v -> {
            timer.cancel();
            timer.purge();
            Navigation.findNavController(v).navigate(R.id.navigation_detail, randomPick());
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getProducts()
    {
        DatabaseReference eventsRef = firebaseApp.getFirebaseDB().getReference().child("Product");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    product = ds.getValue(Product.class);
                    assert product != null;
                    parentModelArrayList.add(new ParentModel(product.getCategory()));
                    productList.add(product);
                }

                TreeSet<ParentModel> set = parentModelArrayList.stream()
                        .collect(Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(ParentModel::getCategory))));
                categoryList.addAll(set);

                callback = new FragmentCallback() {
                    @Override
                    public void doSomething() {
                        timer.cancel();
                        timer.purge();
                    }

                    @Override
                    public void onItemClicked(int position, Object object) {

                    }
                };

                parentLayoutManager = new LinearLayoutManager(getActivity());
                parentViewAdapter = new ParentViewAdapter(categoryList, productList, getActivity(), callback);
                recyclerView.setAdapter(parentViewAdapter);
                parentViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSpecialOffers()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("Feeling Lucky");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot parentDS : dataSnapshot.getChildren()) {
                    String key = parentDS.getKey();
                    dataSnapshotList.add(parentDS);

                    for (DataSnapshot ds : parentDS.getChildren()) {
//                        key = ds.getKey();
                        product = ds.getValue(Product.class);
                        sliderList.add(product);
                    }
                    offerList.add(key);
                }

                callback = new FragmentCallback() {
                    @Override
                    public void doSomething() {

                        timer.cancel();
                        timer.purge();
                    }

                    @Override
                    public void onItemClicked(int position, Object object) {
                        // Handle Object of list item here
                        String offer  = (String) object;
                        ArrayList<Product> tempList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshotList) {
                            if (offer.equals(snapshot.getKey())) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    product = ds.getValue(Product.class);
                                    tempList.add(product);
                                }
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(requireActivity()
                                .getResources()
                                .getString(R.string.feeling_lucky), tempList);
                        NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                                .findFragmentById(R.id.nav_host_fragment_activity_main);
                        assert navHostFragment != null;
                        NavController navController = navHostFragment.getNavController();
                        navController.navigate(R.id.navigation_product_overview, bundle);
                    }
                };

                sliderAdapter = new SliderAdapter(getActivity(), sliderList, offerList, callback);
                page.setAdapter(sliderAdapter);
                objects = requireActivity();

                // sliderTimer
                timer.scheduleAtFixedRate(new sliderTimer(),3000,5000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public Bundle randomPick() {
        Random rand = new Random();
        Product randomElement = productList.get(rand.nextInt(productList.size()));
        Bundle bundle = new Bundle();
        bundle.putParcelable("product", randomElement);
        return bundle;
    }

    @Override
    public void doSomething() {
    }

    @Override
    public void onItemClicked(int position, Object object) {

    }

    public class sliderTimer extends TimerTask {
        @Override
        public void run() {

            objects.runOnUiThread(() -> {
                if (page.getCurrentItem() < offerList.size()-1)
                {
                    page.setCurrentItem(page.getCurrentItem()+1);
                }
                else
                {
                    page.setCurrentItem(0);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}