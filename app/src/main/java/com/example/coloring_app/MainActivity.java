package com.example.coloring_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.coloring_app.Listener.ImageClickedListener;
import com.example.coloring_app.Listener.OnLongPressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class MainActivity extends AppCompatActivity  implements OnLongPressListener {

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

        colorView = findViewById(R.id.colorView);
        circleSize1 = findViewById(R.id.circleSize1);
        circleSize2 = findViewById(R.id.circleSize2);
        circleSize3 = findViewById(R.id.circleSize3);
        circleSize4 = findViewById(R.id.circleSize4);
        circleSize5 = findViewById(R.id.circleSize5);
        eraseBtn = findViewById(R.id.eraserBtn);
        buttonsLayout = findViewById(R.id.linearLayout);
        saveBtn = findViewById(R.id.saveBtn);
        colorPreview = findViewById(R.id.previewSelectedColor);

        previousSelectedView = circleSize1;
        previousSelectedView.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        mDefaultColor = 0;

        colorPreview.setOnClickListener(v -> {
            openColorPickerDialogue();
        });
        circleSize1.setOnClickListener(v -> {
            colorView.setPaintSize(20);
            updateViewBackgroundTint(circleSize1);
//            colorView.setEraserMode(false);

        });
        circleSize2.setOnClickListener(v -> {
            colorView.setPaintSize(30);
            updateViewBackgroundTint(circleSize2);

        });
        circleSize3.setOnClickListener(v -> {
            colorView.setPaintSize(40);
            updateViewBackgroundTint(circleSize3);

        });
        circleSize4.setOnClickListener(v -> {
            colorView.setPaintSize(50);
            updateViewBackgroundTint(circleSize4);

        });
        circleSize5.setOnClickListener(v -> {
            colorView.setPaintSize(60);
            updateViewBackgroundTint(circleSize5);
        });

//        colorView.setEraserMode(false);
        eraseBtn.setOnClickListener(v -> {
  //          colorView.setEraserMode(true);
            colorView.clearCanvas();
        });
        saveBtn.setOnClickListener(v -> {
            showCustomSaveDialog();
        });
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

        colorView.setOnLongPressListener(this);



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
                        colorPreview.setBackgroundColor(mDefaultColor);
                    }
                });
        colorPickerDialogue.show();
    }

    @Override
    public void onLongPressed() {
        if (isVisible){
            isVisible = false;
            buttonsLayout.setVisibility(View.GONE);
        }
        else {
            isVisible = true;
            buttonsLayout.setVisibility(View.VISIBLE);
        }
    }
}