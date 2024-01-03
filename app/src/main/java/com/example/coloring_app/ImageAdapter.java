package com.example.coloring_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private Context context;

    public ImageAdapter(List<ImageData> fileList,int spanCount,Context context) {
        this.fileList = fileList;
        this.spanCount = spanCount;
        this.context = context;
    }
    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {


        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int imageViewSize = screenWidth / spanCount; // Adjust according to your needs

        // Set the size of the image view dynamically
        ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
        layoutParams.width = imageViewSize;
        layoutParams.height = imageViewSize;
        holder.imageView.setLayoutParams(layoutParams);

        String imageName = holder.name.getText().toString();
        String replaceName = imageName.replace(".png","");

        Glide.with(holder.itemView)
                .load(fileList.get(position).getImagePath())
                .into(holder.imageView);

        holder.name.setText(replaceName);
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
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this image.")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int clickedPosition = holder.getAdapterPosition();
                                ImageData imageData = fileList.get(clickedPosition);
                                String imagePath = imageData.getImagePath();

                                File fileToDelete = new File(imagePath);
                                if (fileToDelete.exists()){
                                    if (fileToDelete.delete()){
                                        fileList.remove(clickedPosition);
                                        notifyItemRemoved(clickedPosition);
                                        Toast.makeText(v.getContext(), "Image Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(v.getContext(), "Image not deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(v.getContext(), "Image not exist", Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();
                            }

                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                return true;
            }
        });

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
        private TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.imgName);
        }
    }
}
