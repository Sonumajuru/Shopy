package com.genesistech.njangi.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.HomeAdapter;
import com.genesistech.njangi.adapter.ParentViewAdapter;
import com.genesistech.njangi.adapter.SliderAdapter;
import com.genesistech.njangi.databinding.FragmentHomeBinding;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.ParentModel;
import com.genesistech.njangi.model.Product;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements FragmentCallback {

    private FragmentHomeBinding binding;
    private FragmentActivity objects;

    //a list to store all the products
    private List<Product> productList;
    private List<Product> trendingList;
    private List<String> offerList;

    //the recyclerview
    private RecyclerView recyclerView;
    private FragmentCallback callback;
    private FirebaseApp firebaseApp;

    private Timer timer;
    private ViewPager page;
    private Product product;

    private ParentViewAdapter parentViewAdapter;
    private ProgressBar progressBar;

    private ArrayList<ParentModel> categoryList;
    private ArrayList<ParentModel> parentModelArrayList;
    private RecyclerView.LayoutManager parentLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseApp = new FirebaseApp();
        parentModelArrayList = new ArrayList<>();
        productList = new ArrayList<>();
        trendingList = new ArrayList<>();
        categoryList = new ArrayList<>();
        offerList = new ArrayList<>();
        timer = new java.util.Timer();
        progressBar = binding.progressBar;
        page = binding.viewPager;
        recyclerView = binding.recyclerView;
        TabLayout tabLayout = binding.tabLayout;

        tabLayout.addTab(tabLayout.newTab().setText(R.string.recent).setId(1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.trending).setId(2));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.category).setId(3));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getId() == 1)
                {
                    if (productList.size() == 0) return;
                    getRecent();
                }
                else if (tab.getId() == 2)
                {
                    // Display only Trending Products
//                    if (trendingList.size() == 0) return;
                    if (trendingList.size() == 0) {
                        recyclerView.setAdapter(null);
                    }
                    getTrending();
                }
                else
                {
                    timer.cancel();
                    timer.purge();
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_category);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getProducts();

        homeViewModel.getUserName();
        final TextView title = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), title::setText);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                requireActivity().moveTaskToBack(true);
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getRecent() {

        progressBar.setVisibility(View.VISIBLE);
        productList = new ArrayList<>();
        parentModelArrayList = new ArrayList<>();
        categoryList = new ArrayList<>();

        DatabaseReference eventsRef = firebaseApp.getFirebaseDB().getReference().child("ProductDB").child("products");
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

                    @Override
                    public void onItemClicked(int position, Object object, int id) {

                    }
                };

                Collections.reverse(productList);

                parentLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(parentLayoutManager);
                parentViewAdapter = new ParentViewAdapter(categoryList, productList, getActivity(), callback);

                recyclerView.setAdapter(parentViewAdapter);
                parentViewAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void getTrending() {

        progressBar.setVisibility(View.VISIBLE);
        trendingList = new ArrayList<>();
        DatabaseReference eventsRef = firebaseApp.getFirebaseDB().getReference().child("ProductDB").child("products");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    product = ds.getValue(Product.class);
                    assert product != null;
                    parentModelArrayList.add(new ParentModel(product.getCategory()));
                    if (product.getTrending() != null && product.getTrending().equals("1")) {
                        trendingList.add(product);
                    }
                }

                callback = new FragmentCallback() {
                    @Override
                    public void doSomething() {
                        timer.cancel();
                        timer.purge();
                    }

                    @Override
                    public void onItemClicked(int position, Object object) {

                    }

                    @Override
                    public void onItemClicked(int position, Object object, int id) {

                    }
                };

                Collections.reverse(trendingList);

                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), 30));
                recyclerView.setLayoutManager(mGridLayoutManager);

                HomeAdapter homeAdapter = new HomeAdapter(getContext(), trendingList, callback);
                recyclerView.setAdapter(homeAdapter);
                homeAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {
        int spacing;
        public DividerItemDecoration(Context context, int spacing) {
            this.spacing=spacing;
        }
        @Override
        public void getItemOffsets(Rect outRect, @NotNull View view,
                                   @NotNull RecyclerView parent,
                                   @NotNull RecyclerView.State state) {
            outRect.bottom = spacing;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getProducts() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference eventsRef = firebaseApp.getFirebaseDB().getReference().child("ProductDB").child("products");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    product = ds.getValue(Product.class);
                    assert product != null;
                    parentModelArrayList.add(new ParentModel(product.getCategory()));
                    productList.add(product);
                    if (!product.getStore().isEmpty())
                    {
                        offerList.add(product.getStore());
                    }
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

                    @Override
                    public void onItemClicked(int position, Object object, int id) {

                    }
                };

                Collections.reverse(productList);

                for (Product value : productList) {
                    if (value.getTrending() != null && value.getTrending().equals("1")) {
                        trendingList.add(value);
                    }
                }

                parentLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(parentLayoutManager);
                parentViewAdapter = new ParentViewAdapter(categoryList, productList, getActivity(), callback);

                recyclerView.setAdapter(parentViewAdapter);
                parentViewAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                getSpecialOffers();
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
        callback = new FragmentCallback() {
            @Override
            public void doSomething() {

                timer.cancel();
                timer.purge();
            }

            @Override
            public void onItemClicked(int position, Object object) {

                String offer  = (String) object;
                ArrayList<Product> tempList = new ArrayList<>();

                for (Product value : productList)
                {
                    if (offer.equals(value.getStore())) {
                        tempList.add(value);
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(requireContext()
                        .getResources()
                        .getString(R.string.feeling_lucky), tempList);
                NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_activity_main);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_overview, bundle);

                timer.cancel();
            }

            @Override
            public void onItemClicked(int position, Object object, int id) {

            }
        };

        Set<String> offers = new LinkedHashSet<>(offerList);
        offerList.clear();
        offerList.addAll(offers);

        SliderAdapter sliderAdapter = new SliderAdapter(offerList, callback);
        page.setAdapter(sliderAdapter);
        objects = requireActivity();

        // sliderTimer
        timer.scheduleAtFixedRate(new sliderTimer(),3000,5000);
    }

    @Override
    public void doSomething() {
    }

    @Override
    public void onItemClicked(int position, Object object) {

    }

    @Override
    public void onItemClicked(int position, Object object, int id) {

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