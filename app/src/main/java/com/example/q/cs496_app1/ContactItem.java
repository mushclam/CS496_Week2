package com.example.q.cs496_app1;

public class ContactItem {
    int image;
    String name;
    String phoneNumber;

    public ContactItem(int image, String name, String phoneNumber) {
        this.image = image;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
