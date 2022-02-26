package com.njangi.shop.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.njangi.shop.R;
import com.njangi.shop.adapter.ParentViewAdapter;
import com.njangi.shop.adapter.SliderAdapter;
import com.njangi.shop.databinding.FragmentHomeBinding;
import com.njangi.shop.helper.FirebaseApp;
import com.njangi.shop.interfaces.FragmentCallback;
import com.njangi.shop.model.ParentModel;
import com.njangi.shop.model.Product;
import com.google.firebase.database.*;

import java.util.*;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements FragmentCallback {

    private FragmentHomeBinding binding;
    private FragmentActivity objects;

    //a list to store all the products
    private List<Product> productList;
    private List<String> offerList;

    //the recyclerview
    private RecyclerView recyclerView;
    private FragmentCallback callback;
    private FirebaseApp firebaseApp;

    private Timer timer;
    private ViewPager page;
    private Product product;

    private ParentViewAdapter parentViewAdapter;

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
        categoryList = new ArrayList<>();
        offerList = new ArrayList<>();
        timer = new java.util.Timer();
        page = binding.viewPager;
        Button btnFeelLucky = binding.luckBtn;
        Button btnCategory = binding.categoryBtn;
        recyclerView = root.findViewById(R.id.recyclerView);
        parentLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(parentLayoutManager);

        getProducts();

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
            if (productList.size() == 0) return;
            Navigation.findNavController(v).navigate(R.id.navigation_detail, randomPick());
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                requireActivity().moveTaskToBack(true);
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getProducts()
    {
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

                parentLayoutManager = new LinearLayoutManager(getActivity());
                parentViewAdapter = new ParentViewAdapter(categoryList, productList, getActivity(), callback);
                recyclerView.setAdapter(parentViewAdapter);
                parentViewAdapter.notifyDataSetChanged();
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

        SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), offerList, callback);
        page.setAdapter(sliderAdapter);
        objects = requireActivity();

        // sliderTimer
        timer.scheduleAtFixedRate(new sliderTimer(),3000,5000);
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