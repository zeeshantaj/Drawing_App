package com.example.coloring_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.coloring_app.Listener.OnLongPressListener;

public class ColorView extends View {
    private Paint paint;
    private Path path;
    public Bitmap bitmap;
    private Canvas canvas;

    private float currentPaintSize;
    private int paintColor;

    private boolean isEraserMode = false;

    public Bitmap originalBitmap; // Store the original content before erasing

    private boolean isPathEmpty = true;
    private OnLongPressListener longPressListener;

    public void setOnLongPressListener(OnLongPressListener listener) {
        this.longPressListener = listener;
    }
    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupDraw();
    }
    public void setImageBitmap(Bitmap bitmap){
        if (bitmap != null) {
            this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            this.canvas = new Canvas(this.bitmap);
            invalidate(); // Trigger redraw
            Log.e("MyApp","setImageBitmap"+bitmap.getHeight());
        }
    }
    private void setupDraw(){
        currentPaintSize = 20;
        paintColor = ContextCompat.getColor(getContext(),R.color.black);
        paint =new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(currentPaintSize);
    }
    public void setEraserMode(boolean eraserMode) {
        isEraserMode = eraserMode;
        if (isEraserMode) {
            // Store the current bitmap as the original content only if the path is empty
            if (isPathEmpty) {
                originalBitmap = Bitmap.createBitmap(bitmap);
            }
            paint.setColor(Color.TRANSPARENT); // Set paint color to transparent for erasing
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            paint.setXfermode(null); // Set the paint to normal mode
            if (originalBitmap != null) {
                // Restore the original content only if the path is empty
                if (isPathEmpty) {
                    bitmap = Bitmap.createBitmap(originalBitmap);
                    canvas = new Canvas(bitmap);
                    invalidate();
                }
            }
            paint.setColor(paintColor);
        }
    }
    public void setPaintColor(int color){
        paintColor = color;
        paint.setColor(paintColor);
    }
    public void setPaintSize(float size) {
        currentPaintSize = size;
        paint.setStrokeWidth(currentPaintSize); // Update paint size
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.bitmap, 0, 0, paint); // Always draw the bitmap
        canvas.drawPath(path, paint);
        Log.e("MyApp","onDraw");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (longPressListener != null) {
                    longPressListener.onLongPressed();
                }
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public Bitmap getBitmapFromView() {
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        draw(canvas);
        return returnedBitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void clearCanvas() {
        canvas.drawColor(getResources().getColor(R.color.white));
        invalidate();
    }
}
