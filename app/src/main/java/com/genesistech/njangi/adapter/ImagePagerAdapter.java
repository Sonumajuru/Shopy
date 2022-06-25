package com.genesistech.njangi.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.ui.product.ProductFragment;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
public class ImagePagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<String> images;
    private final LayoutInflater layoutInflater;
    private final Controller controller;
    private final Fragment fragment;
    public FragmentCallback callback;
    public ImagePagerAdapter(Context context, Fragment fragment, List<String> images, FragmentCallback callback) {
        this.context = context;
        controller = Controller.getInstance(context);
        this.images = images;
        this.fragment = fragment;
        this.callback = callback;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return images.size();
    }
    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == object;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.image_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        Uri uri = Uri.parse(images.get(position));
        Picasso.with(context).load(uri).into(imageView);

        container.addView(itemView);

        if (fragment instanceof ProductFragment) {
            ImageButton del = itemView.findViewById(R.id.delButton);
            del.setVisibility(View.VISIBLE);
            del.setOnClickListener(v -> callback.onItemClicked(position, null));
        }

        if (!(fragment instanceof ProductFragment)) {
            imageView.setOnClickListener(v -> {
                if (!controller.getIsFragVisible())
                {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("images", (ArrayList<String>) images);
                    Navigation.findNavController(v).navigate(R.id.navigation_image, bundle);
                    controller.setIsFragVisible(true);
//               Toast.makeText(context, "you clicked image " + (position), Toast.LENGTH_LONG).show();
                }
            });
        }

        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}