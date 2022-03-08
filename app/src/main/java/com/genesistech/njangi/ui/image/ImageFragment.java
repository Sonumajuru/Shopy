package com.genesistech.njangi.ui.image;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.adapter.ImagePagerAdapter;
import com.genesistech.njangi.databinding.FragmentImageBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ImageFragment extends Fragment {

    private FragmentImageBinding binding;
    private Controller controller;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ImageViewModel imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        binding = FragmentImageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        controller = Controller.getInstance(requireContext());
        ImagePagerAdapter imagePagerAdapter;
        TabLayout tabLayout = binding.tabDots;
        ViewPager viewPager = binding.imagePager;

        assert getArguments() != null;
        List<String> images = getArguments().getStringArrayList("images");

        imagePagerAdapter = new ImagePagerAdapter(requireContext(), ImageFragment.this, images, null);
        viewPager.setAdapter(imagePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        controller.setIsFragVisible(false);
    }
}