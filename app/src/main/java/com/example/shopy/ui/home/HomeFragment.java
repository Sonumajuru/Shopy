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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.example.shopy.R;
import com.example.shopy.adapter.ParentViewAdapter;
import com.example.shopy.adapter.SliderAdapter;
import com.example.shopy.databinding.FragmentHomeBinding;
import com.example.shopy.model.ParentModel;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.shopy.R.id.navigation_category;
import static com.example.shopy.R.id.navigation_detail;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    //a list to store all the products
    private List<Product> productList;
    private List<Product> sliderList;
    private List<String> offerList;

    //the recyclerview
    private RecyclerView recyclerView;
    private Product product;

    private ParentViewAdapter ParentAdapter;
    private ArrayList<ParentModel> categoryList;
    private final ArrayList<ParentModel> parentModelArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager parentLayoutManager;
    private SliderAdapter sliderAdapter;
    private ViewPager page;
    private FragmentActivity objects;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        page = binding.viewPager;
        Button btnOrder = binding.orderBtn;
        Button btnCategory = binding.categoryBtn;
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        parentLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(parentLayoutManager);
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        sliderList = new ArrayList<>();
        offerList = new ArrayList<>();

        getProducts();
        getSpecialOffers();

        setUserName();
        btnCategory.setOnClickListener(v -> navController.navigate(navigation_category));

        return root;
    }

    private void setUserName()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView title = binding.textHome;
        if (user == null) return;
        String userid = Objects.requireNonNull(user).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("User");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                title.setText("Welcome back " + Objects.requireNonNull(dataSnapshot.getValue(User.class)).getName());
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getProducts()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("Product");
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

                parentLayoutManager = new LinearLayoutManager(getActivity());
                ParentAdapter = new ParentViewAdapter(categoryList, productList, getActivity());
                recyclerView.setAdapter(ParentAdapter);
                ParentAdapter.notifyDataSetChanged();
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

                    for (DataSnapshot ds : parentDS.getChildren()) {
//                        key = ds.getKey();
                        product = ds.getValue(Product.class);
                        sliderList.add(product);
                    }
                    offerList.add(key);
                }

                sliderAdapter = new SliderAdapter(getActivity(), sliderList, offerList, new SliderAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClicked(int position, Object object) {

                        // Handle Object of list item here
                        String offer  = (String) object;


//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("product", product);
//                        NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
//                                .findFragmentById(R.id.nav_host_fragment_activity_main);
//                        assert navHostFragment != null;
//                        NavController navController = navHostFragment.getNavController();
//                        navController.navigate(navigation_detail, bundle);

                    }
                });
                page.setAdapter(sliderAdapter);
                objects = requireActivity();

                // sliderTimer
                java.util.Timer timer = new java.util.Timer();
                timer.scheduleAtFixedRate(new sliderTimer(),3000,5000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
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