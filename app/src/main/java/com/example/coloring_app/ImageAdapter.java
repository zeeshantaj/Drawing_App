package com.example.coloring_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coloring_app.ImagePreview.ImagePreviewActvity;
import com.example.coloring_app.Model.ImageData;

import java.io.File;
import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<ImageData> fileList;
    private int spanCount;

    public ImageAdapter(List<ImageData> fileList,int spanCount) {
        this.fileList = fileList;
        this.spanCount = spanCount;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {


        //File imageFile = fileList.get(position);
        // Load image into ImageView (assuming you have an ImageView named imageView in item_image layout)

        Glide.with(holder.itemView)
                .load(fileList.get(position).getImagePath())
                .into(holder.imageView);

        int pos = position;
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> imagePaths = new ArrayList<>();
                for (ImageData imageData : fileList) {
                    imagePaths.add(imageData.getImagePath()); // Assuming getImagePath() returns the image path as a string
                }

                Intent intent = new Intent(v.getContext(), ImagePreviewActvity.class);
                //intent.putExtra("imagePath",imagesData.getImagePath());
                intent.putStringArrayListExtra("imagePath",new ArrayList<>(imagePaths));
                intent.putExtra("position",pos);
                v.getContext().startActivity(intent);

            }
        });


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File selectedImageFile = fileList.get(position); // Replace this with your data retrieval logic
//
//                // Pass the selected image file path/data to MainActivity
//                Intent intent = new Intent(v.getContext(), ImagePreviewActvity.class);
//                intent.putExtra("isImageSend",true);
//                intent.putExtra("SELECTED_IMAGE_PATH", selectedImageFile.getAbsolutePath()); // Pass file path
//                // Alternatively, if using Parcelable or Serializable, pass the entire file object
//
//                v.getContext().startActivity(intent);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
    // Update the span count
    public void updateSpanCount(int spanCount) {
        this.spanCount = spanCount;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
