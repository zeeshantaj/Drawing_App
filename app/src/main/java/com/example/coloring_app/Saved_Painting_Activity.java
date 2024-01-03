package com.example.coloring_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.example.coloring_app.Model.ImageData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Saved_Painting_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<ImageData> fileList = new ArrayList<>();
    private FloatingActionButton actionButton;
    private GridLayoutManager layoutManager;
    private int initialSpanCount = 4;

    private ScaleGestureDetector scaleGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_painting);

        actionButton = findViewById(R.id.floatingActionBtn);
        recyclerView = findViewById(R.id.imageRecyclerView);
        //fileList = getImageFilesFromDirectory();

        adapter = new ImageAdapter(fileList,initialSpanCount);

        layoutManager = new GridLayoutManager(this, initialSpanCount);
        recyclerView.setLayoutManager(layoutManager); // Adjust the layout manager as needed
        recyclerView.setAdapter(adapter);


        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("isImageSend",false);
            startActivity(intent);
        });
        getImageFilesFromDirectory();
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener());
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        arcMenu();
    }
    private void arcMenu(){
        CircleMenu circleMenu = findViewById(R.id.arcMenuPaint);
        circleMenu.setMainMenu(Color.parseColor("#CBB0FA"),R.drawable.menu_icon,R.drawable.cancel_icon)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.color_picker)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.brush_size)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.eraser)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.save_icon)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        switch (index){
                            case 0:
                                Toast.makeText(Saved_Painting_Activity.this, "Color picker", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(Saved_Painting_Activity.this, "Brush Size", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(Saved_Painting_Activity.this, "eraser", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(Saved_Painting_Activity.this, "Save", Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });
    }

    private void updateSpanCount(int newSpanCount) {
        final int oldSpanCount = layoutManager.getSpanCount();

        // Notify adapter about the new span count
        adapter.updateSpanCount(newSpanCount);

        // Smoothly scroll to the first visible item to maintain the position
        final int firstVisiblePosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisiblePosition != RecyclerView.NO_POSITION) {
            final int columnWidth = recyclerView.getWidth() / newSpanCount;
            final int offset = (firstVisiblePosition % oldSpanCount) * columnWidth;
            recyclerView.post(() -> {
                recyclerView.scrollBy(offset, 0);
            });
        }

        // Invalidate the layout manager to force RecyclerView to redraw items with new sizes
        recyclerView.postDelayed(() -> {
            layoutManager.setSpanCount(newSpanCount);
            layoutManager.requestLayout();
        }, 100); // Delay added to ensure the RecyclerView layout is updated after the adapter change
    }
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            if (scaleFactor > 1.0f && layoutManager.getSpanCount() < 14) {
                // Zoom in - Limit the maximum span count to 8 (adjust as needed)
                updateSpanCount(layoutManager.getSpanCount() + 1);
                return true;
            }
            else if (scaleFactor < 1.0f && layoutManager.getSpanCount() > 4) {
                // Zoom out - Limit the minimum span count to 1 (adjust as needed)
                updateSpanCount(layoutManager.getSpanCount() - 1);
                return true;
            }

            return false;
        }
    }
    private void getImageFilesFromDirectory(){
        // Define the columns you want to retrieve
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "saved_painting_work");

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Add only image files (you might refine this check based on your file types)
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                        String path = file.getAbsolutePath();
                        ImageData imagesData1 = new ImageData(path);
                        fileList.add(imagesData1);

                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
//    private List<File> getImageFilesFromDirectory() {
//        List<File> imageFiles = new ArrayList<>();
//
//        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "saved_painting_work");
//
//        if (directory.exists() && directory.isDirectory()) {
//            File[] files = directory.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    // Add only image files (you might refine this check based on your file types)
//                    if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
//                        imageFiles.add(file);
//                    }
//                }
//            }
//        }
//
//        return imageFiles;
//    }
}