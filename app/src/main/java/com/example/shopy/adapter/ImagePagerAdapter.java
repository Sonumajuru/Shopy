package com.example.shopy.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;
import com.example.shopy.R;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<String> images;
    private final LayoutInflater layoutInflater;

    public ImagePagerAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
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

        //listening to image click
        imageView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("images", (ArrayList<String>) images);
            Navigation.findNavController(v).navigate(R.id.navigation_image, bundle);
//                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}