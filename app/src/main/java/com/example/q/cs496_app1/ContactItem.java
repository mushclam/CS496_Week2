package com.example.q.cs496_app1;

public class ContactItem {
    int image;
    String imageTitle;

    public ContactItem(int image, String imageTitle) {
        this.image = image;
        this.imageTitle = imageTitle;
    }

    public int getImage() {
        return image;
    }

    public String getImageTitle() {
        return imageTitle;
    }
}
