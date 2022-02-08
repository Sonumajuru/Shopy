package com.example.shopy.ui.favorite;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.shopy.interfaces.FragmentCallback;
import com.example.shopy.R;
import com.example.shopy.adapter.FavAdapter;
import com.example.shopy.databinding.FragmentFavoriteBinding;
import com.example.shopy.db.FavDB;
import com.example.shopy.model.FavItem;
import com.google.firebase.auth.FirebaseAuth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.shopy.R.id.navigation_detail;

public class FavoriteFragment extends Fragment {

    private FavoriteViewModel favoriteViewModel;
    private FragmentFavoriteBinding binding;

    private FavDB favDB;
    private RecyclerView recyclerView;
    private List<FavItem> favItemList;
    private FavAdapter favAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        favDB = new FavDB(getActivity());
        favItemList = new ArrayList<>();
        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            // user loged in already
            loadData();
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView); // Setting swipe to recycleview

        return root;
    }

    @SuppressLint("Range")
    private void loadData() {
        if (favItemList != null) {
            favItemList.clear();
        }
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavDB.KEY_ID));
                String image = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_IMAGE));
                double price = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_PRICE));
                double rating = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_RATING));
                String currency = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CURRENCY));
                String uuid = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_UUID));
                String desc = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CATEGORY));
                FavItem favItem = new FavItem(title, desc, id, image, price, rating, currency, uuid, category);
                favItemList.add(favItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        // Handle Object of list item here
        FragmentCallback callback = new FragmentCallback() {
            @Override
            public void doSomething() {

            }

            @Override
            public void onItemClicked(int position, Object object) {

                // Handle Object of list item here
                FavItem favItem = (FavItem) object;
                Bundle bundle = new Bundle();
                bundle.putParcelable("favItem", favItem);
                NavHost navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_activity_main);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.navigate(navigation_detail, bundle);
            }
        };

        favAdapter = new FavAdapter(favItemList, requireContext(), callback);
        recyclerView.setAdapter(favAdapter);
    }

    // Remove Item after Swipe
    private final ItemTouchHelper.SimpleCallback simpleCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition(); // get swiped position
            final FavItem favItem = favItemList.get(position);
            if (direction == ItemTouchHelper.LEFT){ // Left swipe
                favAdapter.notifyItemRemoved(position); // Item removed from recycleView
                favItemList.remove(position); // The remove the item
                favDB.remove_fav(favItem.getKey_id()); // remove item from DB
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}