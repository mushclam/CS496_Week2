package com.example.q.cs496_week2.tabs.contact;

import android.support.annotation.NonNull;

public class ContactItem {
    String image;
    String name;
    String phoneNumber;
    String email;

    public ContactItem(String image, @NonNull String name, String phoneNumber, String email) {
        this.image = image;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() { return email; }
}
