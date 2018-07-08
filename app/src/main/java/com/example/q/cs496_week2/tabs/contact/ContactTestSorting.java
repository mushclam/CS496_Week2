package com.example.q.cs496_week2.tabs.contact;

import java.util.Comparator;

public class ContactTestSorting implements Comparator<ContactTestItem> {
    @Override
    public int compare(ContactTestItem o, ContactTestItem t) {
        String org = o.getName();
        String comp = t.getName();
        return org.compareTo(comp);
    }
}
