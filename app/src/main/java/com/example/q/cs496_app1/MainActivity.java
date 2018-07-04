package com.example.q.cs496_app1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.cs496_app1.tabs.contact.AddContactActivity;
import com.example.q.cs496_app1.tabs.contact.ContactFragment;
import com.example.q.cs496_app1.tabs.gallery.GalleryFragment;
import com.example.q.cs496_app1.tabs.third.ThirdFragment;

import java.io.File;
import java.util.Arrays;

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

    private TextView xyzView;
    private boolean accOn;
    private long detectedTime;

    private Vibrator vibrator;

    private double[] Xs, Zs;

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
                Toast.makeText(MainActivity.this, "Not Implemented", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
//                startActivity(intent);
            }
        });

        accOn = false;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_deleteContacts) {
            try {
                File file = new File(getFilesDir() + "/test.json");
                if (!file.exists()){
                    Toast.makeText(this, getFilesDir() + " + Not Exist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getFilesDir() + " + Exist", Toast.LENGTH_SHORT).show();
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
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
        }
    }

    public boolean isLeft(double[] Xs) {
        double avgX = (Xs[0] + Xs[1] + Xs[2] + Xs[3] + Xs[4]) / 5.0;
        int firstX = 20;
        int secondX;
        for(int i=5; i<20; i++) {
            if(Xs[i] < avgX - 20) {
                firstX = i;
            }
            if(Xs[i] > avgX + 60) {
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
            if(Xs[i] > avgX + 10) {
                firstX = i;
            }
            if(Xs[i] < avgX - 30) {
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
            if(Zs[i] < avgZ - 15) {
                firstZ = i;
            }
            if(Zs[i] > avgZ + 40) {
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
            if(Zs[i] > avgZ + 10) {
                firstZ = i;
            }
            if(Zs[i] < avgZ - 30) {
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
            // double y = event.values[1];
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
                    Toast.makeText(getApplicationContext(), "Left shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "왼쪽");
                    detectedTime = System.currentTimeMillis();

                    thirdFragment.view.game.move(3);
                }
                else if(isRight(Xs)) {
                    vibrator.vibrate(10);
                    Toast.makeText(getApplicationContext(), "Right shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "오른쪽");
                    detectedTime = System.currentTimeMillis();

                    thirdFragment.view.game.move(1);
                }
                else if(isBack(Zs)) {
                    vibrator.vibrate(10);
                    Toast.makeText(getApplicationContext(), "Back shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "뒤쪽");
                    detectedTime = System.currentTimeMillis();

                    thirdFragment.view.game.move(0);
                }
                else if(isFront(Zs)) {
                    vibrator.vibrate(10);
                    Toast.makeText(getApplicationContext(), "Front shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "앞쪽");
                    detectedTime = System.currentTimeMillis();

                    thirdFragment.view.game.move(2);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
