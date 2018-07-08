package com.example.q.cs496_week2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.example.q.cs496_week2.tabs.contact.AddContactActivity;
import com.example.q.cs496_week2.tabs.contact.ContactFragment;
import com.example.q.cs496_week2.tabs.gallery.GalleryFragment;
import com.example.q.cs496_week2.tabs.third.ThirdFragment;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSION_CAMERA = 300;
    public static final int REQUEST_IMAGE_CAPTURE = 400;

    private Fragment[] mFragments;
    boolean isGalleryFragment = false;
    boolean isContactFragment = true;
    private FloatingActionButton fab;
    private FloatingActionButton fab2;

    private CameraProcessing cameraProcessing;

    public static Context MAIN_CONTEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MAIN_CONTEXT = this;
        cameraProcessing = new CameraProcessing(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mFragments = new Fragment[3];
        mFragments[0] = new ContactFragment();
        mFragments[1] = new GalleryFragment();
        mFragments[2] = new ThirdFragment();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
        fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA);

                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    checkCameraPermission();
                } else {
                    cameraProcessing.sendTakePhotoIntent();
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab.show();
                        fab2.hide();
                        isGalleryFragment = false;
                        isContactFragment = true;

                        break;
                    case 1:
                        fab.hide();
                        fab2.show();
                        isGalleryFragment = true;
                        isContactFragment = false;

                        break;
                    case 2:
                        fab.hide();
                        fab2.hide();
                        isGalleryFragment = false;
                        isContactFragment = false;

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("퍼미션", "결과 받음");
        switch (requestCode) {
            case MY_PERMISSION_CAMERA: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                            cameraProcessing.sendTakePhotoIntent();
                        } else {
                            Toast.makeText(this,R.string.require_camera, Toast.LENGTH_LONG).show();
//                            finish();
                        }
                    }
                }
            } break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.camera_permission)
                        .setNeutralButton(R.string.settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                finish();
                            }
                        })
                        .setCancelable(false).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("onActivityResult", "result code = " + String.valueOf(resultCode) + ", request code = " + String.valueOf(requestCode));
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 3000:
                    GalleryFragment galleryFragment = (GalleryFragment) mFragments[1];
                    galleryFragment.onRefresh(data.getIntExtra("INDEX", 0));
                    break;
                case 4000:
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    cameraProcessing.resultProcessing();
                    break;
            }
        }
    }



    @Override
    public void onBackPressed() {
        if(isGalleryFragment) {
            GalleryFragment galleryFragment = (GalleryFragment) mFragments[1];
            galleryFragment.onBack();
        } else if(isContactFragment) {
            ContactFragment contactFragment = (ContactFragment) mFragments[0];
            contactFragment.onBack();
        } else {
            finish();
        }
    }
}