package com.example.coloring_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coloring_app.Listener.ImageClickedListener;
import com.example.coloring_app.Listener.OnLongPressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity  {

    private ColorView colorView;
    private View circleSize1, circleSize2, circleSize3, circleSize4, circleSize5;
    private View colorPreview;
    private int mDefaultColor;
    private View previousSelectedView  = null;
    private ImageView eraseBtn;
    private Button saveBtn;
    private LinearLayout buttonsLayout;

    private boolean isVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        arcMenu();
//        previousSelectedView = circleSize1;
//        previousSelectedView.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
//        mDefaultColor = 0;


//        Intent intent = getIntent();
//
//        if(isImage) {
//            String selectedImagePath = intent.getStringExtra("SELECTED_IMAGE_PATH");
//            Log.d("MyApp", "Selected Image Path: " + selectedImagePath); // Check if the path is correct
//            if (selectedImagePath != null) {
//                File selectedImageFile = new File(selectedImagePath);
//                if (selectedImageFile.exists()) {
//                    try {
//                        InputStream imageStream = new FileInputStream(selectedImageFile);
//                    } catch (FileNotFoundException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImageFile.getAbsolutePath());
//                    if (bitmap != null) {
//                        colorView.setImageBitmap(bitmap);
//                    } else {
//                        Log.e("MyApp", "Bitmap decoding failed");
//                    }
//                } else {
//                    Log.e("MyApp", "Selected image file does not exist");
//                }
//            }
//        }
        colorView = findViewById(R.id.colorView);

        Intent intent = getIntent();
        boolean isImage = intent.getBooleanExtra("isImageSend",false);
        Log.e("MyApp","isImage"+isImage);
        if (isImage) {
            String selectedImagePath = intent.getStringExtra("SELECTED_IMAGE_PATH");
            Log.d("MyApp", "Selected Image Path: " + selectedImagePath);

            File selectedImageFile = new File(selectedImagePath);
            if (selectedImageFile.exists()) {
                // Try decoding with default options first:
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImageFile.getAbsolutePath());

                // If decoding fails, check options for compatibility:
                if (bitmap == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // Adjust options as needed, ensuring compatibility with PNG
                    bitmap = BitmapFactory.decodeFile(selectedImageFile.getAbsolutePath(), options);

                    // Log any decoding issues:
                    if (bitmap == null) {
                        Log.e("MyApp", "Bitmap decoding failed even with options");
                    } else {
                        Log.d("MyApp", "Bitmap decoded successfully with options");
                    }
                }

                if (bitmap != null) {
                    colorView.setImageBitmap(bitmap);
                    Log.d("MyApp", "Image set to ColorView");
                } else {
                    Log.e("MyApp", "Bitmap decoding failed");
                }
            } else {
                Log.e("MyApp", "Selected image file does not exist");
            }
        }
    }

    private void showCustomSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_save_dialog, null);
        builder.setView(dialogView);

        EditText editTextFileName = dialogView.findViewById(R.id.editText);
        Button buttonSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonSave.setOnClickListener(v -> {
            String fileName = editTextFileName.getText().toString();
            if (!fileName.isEmpty()) {
                // Save drawing content using the provided file name (fileName)
                saveDrawingToFile(fileName);

                dialog.dismiss();
            } else {
                // Handle empty file name
                Toast.makeText(this, "Please enter a file name", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void showBrushSizeDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.brush_size_selection_dialugue, null);
        builder.setView(dialogView);

        SeekBar seekBar = dialogView.findViewById(R.id.seekBar);
        Button buttonSave = dialogView.findViewById(R.id.brushSizeSetBtn);
        TextView progressTxt = dialogView.findViewById(R.id.progresTxt);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonSave.setOnClickListener(v -> {
            colorView.setPaintSize(seekBar.getProgress());
            Toast.makeText(this, "Brush Size"+seekBar.getProgress(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressTxt.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    private void saveDrawingToFile(String fileName) {
        Bitmap drawingBitmap = colorView.getBitmapFromView(); // Get the bitmap from ColorView
        if (drawingBitmap != null) {
            File publicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "saved_painting_work");

            if (!publicDirectory.exists()) {
                if (!publicDirectory.mkdirs()) {
                    // Handle directory creation failure
                    Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            File file = new File(publicDirectory, fileName + ".png"); // Add file extension (e.g., ".png")
            try {
                FileOutputStream fos = new FileOutputStream(file);
                drawingBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                // Show success message with the absolute path of the saved file
                Toast.makeText(this, "File saved: \n" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                //Log.e("MyApp","directory"+file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                // Handle file saving error
                Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateViewBackgroundTint(View selectedView) {
        // If a previous view was selected, reset its background tint
        if (previousSelectedView != null) {
            previousSelectedView.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
        }

        // Set the selected view's tint to red
        selectedView.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));

        // Update the previously selected view reference
        previousSelectedView = selectedView;
    }
    public void openColorPickerDialogue() {

        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // leave this function body as
                        // blank, as the dialog
                        // automatically closes when
                        // clicked on cancel button
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // change the mDefaultColor to
                        // change the GFG text color as
                        // it is returned when the OK
                        // button is clicked from the
                        // color picker dialog
                        mDefaultColor = color;
                        colorView.setPaintColor(color);
                        // now change the picked color
                        // preview box to mDefaultColor
//                        Drawable backgroundDrawable = getResources().getDrawable(R.drawable.color_picker);
//                        backgroundDrawable.setTint(mDefaultColor);
//                        colorPreview.setBackground(backgroundDrawable);
                    }
                });
        colorPickerDialogue.show();

    }
    public void changeCanvasBackground() {

        int whiteColor = ContextCompat.getColor(this,R.color.white);

        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, whiteColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // leave this function body as
                        // blank, as the dialog
                        // automatically closes when
                        // clicked on cancel button
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // change the mDefaultColor to
                        // change the GFG text color as
                        // it is returned when the OK
                        // button is clicked from the
                        // color picker dialog

                        colorView.setBackgroundColor(color);
                        // now change the picked color
                        // preview box to mDefaultColor
//                        Drawable backgroundDrawable = getResources().getDrawable(R.drawable.color_picker);
//                        backgroundDrawable.setTint(mDefaultColor);
//                        colorPreview.setBackground(backgroundDrawable);
                    }
                });
        colorPickerDialogue.show();

    }

    private void arcMenu(){
        CircleMenu circleMenu = findViewById(R.id.arcMenu);

        circleMenu.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        // Capture the initial touch position relative to the view
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Update view position as the user drags their finger
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        circleMenu.setMainMenu(Color.parseColor("#CBB0FA"),R.drawable.menu_icon,R.drawable.cancel_icon)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.color_picker)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.brush_size)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.eraser)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.save_icon)
                .addSubMenu(Color.parseColor("#CBB0FA"),R.drawable.bg_icon)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        switch (index){
                            case 0:
                                Toast.makeText(MainActivity.this, "Color picker", Toast.LENGTH_SHORT).show();
                                openColorPickerDialogue();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "Brush Size", Toast.LENGTH_SHORT).show();
                                showBrushSizeDialogue();
                                break;
                            case 2:
                                Toast.makeText(MainActivity.this, "Eraser", Toast.LENGTH_SHORT).show();
                                colorView.clearCanvas();
                                break;
                            case 3:
                                Toast.makeText(MainActivity.this, "Save", Toast.LENGTH_SHORT).show();
                                showCustomSaveDialog();
                                break;
                            case 4:
                                Toast.makeText(MainActivity.this, "Change Background", Toast.LENGTH_SHORT).show();
                                changeCanvasBackground();
                                break;


                        }
                    }
                });
    }


    private void deprecatedCodes(){

//        circleSize1 = findViewById(R.id.circleSize1);
//        circleSize2 = findViewById(R.id.circleSize2);
//        circleSize3 = findViewById(R.id.circleSize3);
//        circleSize4 = findViewById(R.id.circleSize4);
//        circleSize5 = findViewById(R.id.circleSize5);
//        eraseBtn = findViewById(R.id.eraserBtn);
//        buttonsLayout = findViewById(R.id.linearLayout);
//        saveBtn = findViewById(R.id.saveBtn);
//        colorPreview = findViewById(R.id.previewSelectedColor);
//        colorPreview.setOnClickListener(v -> {
//
//        });
//        circleSize1.setOnClickListener(v -> {
//            colorView.setPaintSize(20);
//            updateViewBackgroundTint(circleSize1);
////            colorView.setEraserMode(false);
//
//        });
//        circleSize2.setOnClickListener(v -> {
//            colorView.setPaintSize(30);
//            updateViewBackgroundTint(circleSize2);
//
//        });
//        circleSize3.setOnClickListener(v -> {
//            colorView.setPaintSize(40);
//            updateViewBackgroundTint(circleSize3);
//
//        });
//        circleSize4.setOnClickListener(v -> {
//            colorView.setPaintSize(50);
//            updateViewBackgroundTint(circleSize4);
//
//        });
//        circleSize5.setOnClickListener(v -> {
//            colorView.setPaintSize(60);
//            updateViewBackgroundTint(circleSize5);
//        });
//
////        colorView.setEraserMode(false);
//        eraseBtn.setOnClickListener(v -> {
//            //          colorView.setEraserMode(true);
//
//        });
//        saveBtn.setOnClickListener(v -> {
//
//        });
    }
}