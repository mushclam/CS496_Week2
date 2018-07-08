package com.example.q.cs496_week2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.q.cs496_week2.tabs.contact.ContactFragment;
import com.example.q.cs496_week2.tabs.gallery.GalleryFragment;
import com.example.q.cs496_week2.tabs.third.ThirdFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mFragments.add(0, new ContactFragment());
        mFragments.add(1, new GalleryFragment());
        mFragments.add(2, new ThirdFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return mFragments.size();
    }
}
