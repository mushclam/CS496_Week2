package com.example.q.cs496_app1.tabs.gallery;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.q.cs496_app1.R;
import com.example.q.cs496_app1.TouchImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int index;

    private static ArrayList<MyImage> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        index = intent.getIntExtra("INDEX", 0);
        images = intent.getParcelableArrayListExtra("IMAGE");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(10);
        mViewPager.setCurrentItem(index);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                index= position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deleteImage) {

            DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Set up the projection (we only need the ID)
                    String[] projection = { MediaStore.Images.Media._ID };

                    // Match on the file path
                    String selection = MediaStore.Images.Media.DATA + " = ?";
                    String[] selectionArgs = new String[] { images.get(index).getFilePath() };

                    // Query for the ID of the media matching the file path
                    Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver contentResolver = getContentResolver();
                    Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                    if (c.moveToFirst()) {
                        // We found the ID. Deleting the item via the content provider will also remove the file
                        long _id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, _id);
                        contentResolver.delete(deleteUri, null, null);

                        //
//                Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_move_top);


                        images.remove(index);
                        mSectionsPagerAdapter.notifyDataSetChanged();
//                mViewPager.setCurrentItem(index);

                        Toast.makeText(getApplicationContext(), "사진이 지워졌습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        // File not found in media store DB
                    }

                    c.close();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("정말 삭제하시겠습니까?")
                    .setPositiveButton("삭제", deleteListener)
                    .setNegativeButton("취소", cancelListener)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("INDEX", index);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int index) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("INDEX", index);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image, container, false);

            // https://stackoverflow.com/questions/43639971/i-used-glide-library-to-load-image-into-imageview-and-i-dont-know-how-to-make-i
            final TouchImageView fullImage = (TouchImageView) rootView.findViewById(R.id.full_image);
            SimpleTarget target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    fullImage.setImageBitmap(bitmap);
                }
            };

            int idx = getArguments().getInt("INDEX");
            MyImage myImage = images.get(idx);
            Glide.with(getActivity()).load(myImage.getFile()).asBitmap().thumbnail(0.01f).into(target);

            TextView description = rootView.findViewById(R.id.describe_image_view);

            Date dateTaken = new Date(myImage.getDate());
            description.setText((new SimpleDateFormat("yyyy년 MM월 dd일(E) HH시 mm분 ss초")).format(dateTaken));
//            Log.e("날짜 ", (new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분(E)")).format(dateTaken));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }

//        @Override
//        public long getItemId(int position) {
//            // give an ID different from position when position has been changed
//            return baseId + position;
//        }
//
//        public void notifyChangeInPosition(int n) {
//            // shift the ID returned by getItemId outside the range of all previous fragments
//            baseId += getCount() + n;
//        }
    }


}
