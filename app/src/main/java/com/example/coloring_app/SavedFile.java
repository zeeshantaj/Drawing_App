package com.example.coloring_app;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SavedFile {
    public static void SavedDrawnToFile(String fileName,ColorView colorView) throws FileNotFoundException {
        Bitmap drawnBitmap = colorView.getBitmapFromView();
        Bitmap drawingBitmap = colorView.getBitmapFromView(); // Get the bitmap from ColorView
        if (drawingBitmap != null){
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Saved_PaintingWork");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File destinationFile = new File(directory, fileName);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4 * 1024];
            int read;

        }
    }
}
