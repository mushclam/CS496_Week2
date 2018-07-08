package com.example.q.cs496_week2.tabs.contact;

import java.util.Comparator;

public class ContactSorting implements Comparator<ContactItem> {
    @Override
    public int compare(ContactItem o, ContactItem t) {
        String org = o.getName();
        String comp = t.getName();
        return org.compareTo(comp);
    }
}
