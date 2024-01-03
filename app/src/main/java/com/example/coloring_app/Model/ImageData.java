package com.example.coloring_app.Model;

public class ImageData {
    private String imagePath;
    private String imageTitle;

    public ImageData(String imagePath,String imageTitle) {
        this.imagePath = imagePath;
        this.imageTitle = imageTitle;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
