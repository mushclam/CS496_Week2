package com.example.q.cs496_week2.tabs.contact;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactTestItem implements Serializable{
    private String id;
    private String profile;
    private String name;
    private ArrayList<Phone> phoneNumbers;
    private ArrayList<Email> emails;
    private String note;
    private String starred;

    public ContactTestItem(String id,
                           String profile,
                           String name,
                           ArrayList<Phone> phoneNumbers,
                           ArrayList<Email> emails,
                           String note,
                           String starred) {
        this.id = id;
        this.profile = profile;
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
        this.note = note;
        this.starred = starred;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Phone> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<Phone> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public ArrayList<Email> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStarred() {
        return starred;
    }

    public void setStarred(String starred) {
        this.starred = starred;
    }
}
