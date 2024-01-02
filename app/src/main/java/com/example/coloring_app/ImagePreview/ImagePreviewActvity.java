package com.example.coloring_app.ImagePreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener;
import com.example.coloring_app.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class ImagePreviewActvity extends AppCompatActivity {

    private ViewPager viewPager;
    private Toolbar toolbar;
    private ElasticDragDismissFrameLayout dragDismissFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.image_preview_activity);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar);
        dragDismissFrameLayout = findViewById(R.id.dragDismiss);

        dragDismissFrameLayout.addListener(new ElasticDragDismissListener() {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {

            }
            @Override
            public void onDragDismissed() {

                finish();
            }
        });

        hideSystemUI();

        setViewPager();
        setToolbar();
    }
    private void setViewPager() {

        ArrayList<String> imagePaths = getIntent().getStringArrayListExtra("imagePath"); // Retrieve the ArrayList
        int selectedPosition = getIntent().getIntExtra("position", 0);
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imagePaths);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(selectedPosition);
    }
    private void hideSystemUI() {
        //todo hide the navigation bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    private void setToolbar() {


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator();
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}