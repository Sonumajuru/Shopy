package com.genesistech.njangi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.genesistech.njangi.R;
import com.genesistech.njangi.interfaces.FragmentCallback;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private final List<String> offerList;
    private final FragmentCallback callback;
    public SliderAdapter(List<String> offerList, FragmentCallback callback) {
        this.offerList = offerList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
        ImageView imageView = view.findViewById(R.id.sliderImage);

        String slider = offerList.get(position);

        // Example of top shop companies should display here
        switch (slider) {
            case "Fokou":
                imageView.setBackgroundResource(R.drawable.black_friday);
                break;
            case "Nikki":
                imageView.setBackgroundResource(R.drawable.discounts);
                break;
            case "Shoprite":
                imageView.setBackgroundResource(R.drawable.hot_deals);
                break;
            case "Njangi":
                imageView.setBackgroundResource(R.drawable.njangi_day);
                break;
        }

        imageView.setOnClickListener(view1 -> callback.onItemClicked(position, slider));

        parent.addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
    @Override
    public int getCount() {
        return offerList.size();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}