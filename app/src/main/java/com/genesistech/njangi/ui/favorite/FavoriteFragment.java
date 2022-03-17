package com.genesistech.njangi.ui.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.FavAdapter;
import com.genesistech.njangi.adapter.ProductAdapter;
import com.genesistech.njangi.databinding.FragmentFavoriteBinding;
import com.genesistech.njangi.helper.PrefManager;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.genesistech.njangi.R.id.navigation_detail;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;

    private FavAdapter favAdapter;
    private ProductAdapter adapter;
    private PrefManager prefManager;
    private List<Product> productList;
    private List<Product> newCartItemList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FavoriteViewModel favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prefManager = new PrefManager(requireContext());
        productList = new ArrayList<>();
        newCartItemList = new ArrayList<>();
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            productList = prefManager.getProductList();
            FragmentCallback callback = new FragmentCallback() {
                @Override
                public void doSomething() {

                }

                @Override
                public void onItemClicked(int position, Object object) {

                    // Handle Object of list item here
                    Product favItem = (Product) object;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("product", favItem);
                    NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment_activity_main);
                    assert navHostFragment != null;
                    NavController navController = navHostFragment.getNavController();
                    navController.navigate(navigation_detail, bundle);
                }

                @Override
                public void onItemClicked(int position, Object object, int id) {

                }
            };
            for (Product product : productList) {
                if (product.getFavStatus().equals("1")) {
                    newCartItemList.add(product);
                }
            }
            favAdapter = new FavAdapter(newCartItemList, requireContext(), callback);
//            adapter = new ProductAdapter(getActivity(), FavoriteFragment.this, productList, callback);
            recyclerView.setAdapter(favAdapter);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    // Remove Item after Swipe
    private final ItemTouchHelper.SimpleCallback simpleCallBack
            = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAbsoluteAdapterPosition(); // get swiped position
            final Product favItem = productList.get(position);
            if (direction == ItemTouchHelper.LEFT){ // Left swipe
                favAdapter.notifyItemRemoved(position); // Item removed from recycleView
                newCartItemList.remove(position); // The remove the item
                prefManager.updateQuoteList(favItem.getProdID());
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}