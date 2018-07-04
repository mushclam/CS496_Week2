package com.example.q.cs496_app1;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.q.cs496_app1.tabs.contact.AddContactActivity;
import com.example.q.cs496_app1.tabs.contact.ContactFragment;
import com.example.q.cs496_app1.tabs.gallery.GalleryFragment;
import com.example.q.cs496_app1.tabs.third.ThirdFragment;

import java.io.File;
import java.util.Arrays;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static final int MY_PERMISSION_CAMERA = 300;
    public static final int REQUEST_IMAGE_CAPTURE = 400;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Fragment[] mFragments;
    private FloatingActionButton fab;
    private FloatingActionButton fab2;

    private SensorManager mSensorManager = null;
    private SensorEventListener mAccLis;
    private Sensor mAccelerometerSensor = null;

    private ImageView direction_arrow;
    private long detectedTime;

    private Vibrator vibrator;

    private double[] Xs, Zs;
    private int sensitivityL, sensitivityR, sensitivityU, sensitivityD;

    private String imageFilePath;
    private Uri photoUri;

    public static Context MAIN_CONTEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MAIN_CONTEXT = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mFragments = new Fragment[3];
        mFragments[0] = new ContactFragment();
        mFragments[1] = new GalleryFragment();
        mFragments[2] = new ThirdFragment();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
                sendTakePhotoIntent();
            }
        });

        direction_arrow = findViewById(R.id.direction_arrow);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new AccelerometerListener();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //xyzView = view.findViewById(R.id.xyz_view);

        Xs = new double[20];
        Zs = new double[20];
        Arrays.fill(Xs, 0);
        Arrays.fill(Zs, 0);

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
                        break;
                    case 1:
                        fab.hide();
                        fab2.show();

                        mSensorManager.unregisterListener(mAccLis);

                        break;
                    case 2:
                        fab.hide();
                        fab2.hide();

                        resetSensitivity();
                        mSensorManager.registerListener(mAccLis, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        ThirdFragment thirdFragment = (ThirdFragment) mFragments[2];
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            //Do nothing
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//            thirdFragment.view.game.move(0);
////            view.game.move(2);
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//            thirdFragment.view.game.move(2);
////            view.game.move(0);
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//            thirdFragment.view.game.move(1);
////            view.game.move(3);
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            thirdFragment.view.game.move(3);
////            view.game.move(1);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        if (id == R.id.action_deleteContacts) {
//            try {
//                File file = new File(getFilesDir() + "/test.json");
//                if (!file.exists()){
//                    Toast.makeText(this, getFilesDir() + " + Not Exist", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, getFilesDir() + " + Exist", Toast.LENGTH_SHORT).show();
//                    file.delete();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
            return mFragments[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("퍼미션", "결과 받음");
        switch (requestCode) {
            case 200: {
                finish();
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("퍼미션", "허용");

                    overridePendingTransition( 0, 0);
                    startActivity(getIntent());
                    overridePendingTransition( 0, 0);

                } else {
                    Log.e("퍼미션", "거절");
                }
                return;
            }

            case MY_PERMISSION_CAMERA: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                        } else {
                            Toast.makeText(this,"Should have camera permission to run", Toast.LENGTH_LONG).show();
//                            finish();
                        }
                    }
                }
            } break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void resetSensitivity() {


        SharedPreferences sensPref = getSharedPreferences("Sensitivity", MODE_PRIVATE);
        sensitivityL = sensPref.getInt("left", 5);
        sensitivityR = sensPref.getInt("right", 5);
        sensitivityU = sensPref.getInt("up", 5);
        sensitivityD = sensPref.getInt("down", 5);

        Toast.makeText(this, "민감도 설정됨\nL: " + String.valueOf(sensitivityL) +
                "\nR: " + String.valueOf(sensitivityR) + "\nU: " + String.valueOf(sensitivityU) +
                "\nD: " + String.valueOf(sensitivityD), Toast.LENGTH_SHORT).show();
    }

    public boolean isLeft(double[] Xs) {
        double avgX = (Xs[0] + Xs[1] + Xs[2] + Xs[3] + Xs[4]) / 5.0;
        int firstX = 20;
        int secondX;
        for(int i=5; i<20; i++) {
            if(Xs[i] < avgX - (sensitivityL * 2.8 + 5)) {
                firstX = i;
            }
            if(Xs[i] > avgX + (sensitivityL * 2.8 + 5) * 3) {
                secondX = i;
                if(firstX < secondX) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isRight(double[] Xs) {
        double avgX = (Xs[0] + Xs[1] + Xs[2] + Xs[3] + Xs[4]) / 5.0;
        int firstX = 20;
        int secondX;
        for(int i=5; i<20; i++) {
            if(Xs[i] > avgX + (sensitivityR * 2.8 + 5)) {
                firstX = i;
            }
            if(Xs[i] < avgX - (sensitivityR * 2.8 + 5) * 3) {
                secondX = i;
                if(firstX < secondX) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isBack(double[] Zs) {
        double avgZ = (Zs[0] + Zs[1] + Zs[2] + Zs[3] + Zs[4]) / 5.0;
        int firstZ = 20;
        int secondZ;
        for(int i=5; i<20; i++) {
            if(Zs[i] < avgZ - (sensitivityU * 2.8 + 5)) {
                firstZ = i;
            }
            if(Zs[i] > avgZ + (sensitivityU * 2.8 + 5) * 3) {
                secondZ = i;
                if(firstZ < secondZ) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isFront(double[] Zs) {
        double avgZ = (Zs[0] + Zs[1] + Zs[2] + Zs[3] + Zs[4]) / 5.0;
        int firstZ = 20;
        int secondZ;
        for(int i=5; i<20; i++) {
            if(Zs[i] > avgZ + (sensitivityD * 2.8 + 5)) {
                firstZ = i;
            }
            if(Zs[i] < avgZ - (sensitivityD * 2.8 + 5) * 3) {
                secondZ = i;
                if(firstZ < secondZ) {
                    return true;
                }
            }
        }
        return false;
    }

    private class AccelerometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            double x = event.values[0];
            double z = event.values[2];

            for(int i=0; i<19; i++) {
                Xs[i] = Xs[i+1];
                Zs[i] = Zs[i+1];
            }

            Xs[19] = x;
            Zs[19] = z;


            if(System.currentTimeMillis() - detectedTime > 1000) {
                ThirdFragment thirdFragment = (ThirdFragment) mFragments[2];

                if(isLeft(Xs)) {
                    vibrator.vibrate(10);

                    Log.e("흔들림 감지 ", "왼쪽");
                    detectedTime = System.currentTimeMillis();

                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(700);
                    direction_arrow.setImageResource(R.drawable.direction_left);
                    direction_arrow.setVisibility(View.VISIBLE);
                    direction_arrow.setAnimation(animation);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            direction_arrow.setVisibility(View.GONE);
                        }
                    }, 1500);

                    thirdFragment.view.game.move(3);
                }
                else if(isRight(Xs)) {
                    vibrator.vibrate(10);

                    Log.e("흔들림 감지 ", "오른쪽");
                    detectedTime = System.currentTimeMillis();

                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(700);
                    direction_arrow.setImageResource(R.drawable.direction_right);
                    direction_arrow.setVisibility(View.VISIBLE);
                    direction_arrow.setAnimation(animation);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            direction_arrow.setVisibility(View.GONE);
                        }
                    }, 1500);

                    thirdFragment.view.game.move(1);
                }
                else if(isBack(Zs)) {
                    vibrator.vibrate(10);

                    Log.e("흔들림 감지 ", "뒤쪽");
                    detectedTime = System.currentTimeMillis();

                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(700);
                    direction_arrow.setImageResource(R.drawable.direction_up);
                    direction_arrow.setVisibility(View.VISIBLE);
                    direction_arrow.setAnimation(animation);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            direction_arrow.setVisibility(View.GONE);
                        }
                    }, 1500);

                    thirdFragment.view.game.move(0);
                }
                else if(isFront(Zs)) {
                    vibrator.vibrate(10);

                    Log.e("흔들림 감지 ", "앞쪽");
                    detectedTime = System.currentTimeMillis();

                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(700);
                    direction_arrow.setImageResource(R.drawable.direction_down);
                    direction_arrow.setVisibility(View.VISIBLE);
                    direction_arrow.setAnimation(animation);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            direction_arrow.setVisibility(View.GONE);
                        }
                    }, 1500);

                    thirdFragment.view.game.move(2);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage("Camera permission not granted")
                        .setNeutralButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
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
                    resetSensitivity();
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    ExifInterface exif = null;

                    try {
                        exif = new ExifInterface(imageFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int exifOrientation;
                    int exifDegree;

                    if(exif != null) {
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegrees(exifOrientation);
                    } else {
                        exifDegree = 0;
                    }
                    
                    Bitmap savedImage = rotate(bitmap, exifDegree);
                    saveImage(savedImage);
                    break;
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getCacheDir();
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void saveImage(Bitmap finalBitmap) {
        OutputStream fout = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File saveDir = new File("/sdcard/DCIM/");
            if (!saveDir.exists()) { saveDir.mkdirs(); }

            File internalImage = new File(saveDir, "image_" + timeStamp + ".jpg");
            Log.e("FILE", internalImage.toString());
            if(!internalImage.exists()) { internalImage.createNewFile(); }

            fout = new FileOutputStream(internalImage);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}