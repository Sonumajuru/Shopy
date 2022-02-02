package com.example.shopy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.shopy.interfaces.FragmentCallback;
import com.example.shopy.R;
import com.example.shopy.model.Product;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private final Context mCtx;
    private final List<Product> sliderList;
    private final List<String> offerList;
    private FragmentCallback callback;

    public SliderAdapter(Context mCtx, List<Product> sliderList, List<String> offerList, FragmentCallback callback) {
        this.mCtx = mCtx;
        this.sliderList = sliderList;
        this.offerList = offerList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
        ImageView imageView = view.findViewById(R.id.sliderImage);

        String slider = offerList.get(position);

        if (slider.equals(mCtx.getResources().getString(R.string.black_friday))) {
            imageView.setBackgroundResource(R.drawable.black_friday);
        }
        else if (slider.equals(mCtx.getResources().getString(R.string.discounts))) {
            imageView.setBackgroundResource(R.drawable.discounts);
        }
        else if (slider.equals(mCtx.getResources().getString(R.string.hot_deals))) {
            imageView.setBackgroundResource(R.drawable.hot_deals);
        }
        else if (slider.equals(mCtx.getResources().getString(R.string.njangi_deals))) {
            imageView.setBackgroundResource(R.drawable.njangi_day);
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