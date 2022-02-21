package com.example.shopy.ui.image;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import com.example.shopy.adapter.ImagePagerAdapter;
import com.example.shopy.databinding.FragmentImageBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ImageFragment extends Fragment {

    private ImageViewModel imageViewModel;
    private FragmentImageBinding binding;
    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        binding = FragmentImageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImagePagerAdapter imagePagerAdapter;
        tabLayout = binding.tabDots;
        ViewPager viewPager = binding.imagePager;

        assert getArguments() != null;
        List<String> images = getArguments().getStringArrayList("images");

        imagePagerAdapter = new ImagePagerAdapter(requireContext(), images);
        viewPager.setAdapter(imagePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);

        return root;
    }

    private void getImageHeight()
    {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}