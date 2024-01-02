package com.example.coloring_app.ImagePreview;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private List<String> imagePaths;
    public ImagePagerAdapter(FragmentManager fragmentManager, List<String> imagePaths) {
        super(fragmentManager);
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size(); // Total number of images
    }

    @Override
    public Fragment getItem(int position) {
        // Create a new instance of ImageFragment for the image at the specified position
        return ImageFragment.newInstance(imagePaths.get(position));
    }
}
