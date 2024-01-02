package com.example.coloring_app.ImagePreview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.coloring_app.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageFragment extends Fragment {

    private String imagePath;
    public static ImageFragment newInstance(String imagePath) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("imagePath", imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            imagePath = getArguments().getString("imagePath");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image,container,false);
        PhotoView imageView = view.findViewById(R.id.photo_view);
        //ImageView imageView = rootView.findViewById(R.id.imageView);

        // GestureDetector gestureDetector = new GestureDetector(getActivity(),new MyGestureListener());
        Glide.with(this)
                .load(imagePath)
                .into(imageView);
//

        imageView.setMaximumScale(5); // Adjust the maximum zoom level as needed

        return view;
    }
}
