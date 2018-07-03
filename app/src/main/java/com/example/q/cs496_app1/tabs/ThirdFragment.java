package com.example.q.cs496_app1.tabs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.cs496_app1.R;

import java.util.Arrays;

public class ThirdFragment extends Fragment {

    private SensorManager mSensorManager = null;
    private SensorEventListener mAccLis;
    private Sensor mAccelerometerSensor = null;

    private TextView xyzView;
    private boolean accOn;
    private long detectedTime;

    private Vibrator vibrator;

    private double[] Xs, Zs;



    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_third, container, false);
        accOn = false;


        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new AccelerometerListener();

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        view.findViewById(R.id.acc_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accOn) {
                    mSensorManager.unregisterListener(mAccLis);
                    accOn = false;
                } else {
                    mSensorManager.registerListener(mAccLis, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
                    accOn = true;
                }
            }
        });

        xyzView = view.findViewById(R.id.xyz_view);

        Xs = new double[20];
        Zs = new double[20];
        Arrays.fill(Xs, 0);
        Arrays.fill(Zs, 0);

        return view;
    }

    private class AccelerometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

//            double angleXZ = Math.atan2(x,  z) * 180/Math.PI;
//            double angleYZ = Math.atan2(y,  z) * 180/Math.PI;


            xyzView.setText(
                    "[X]:" + String.format("%.4f", x)
                            + "\n[Y]:" + String.format("%.4f", y)
                            + "\n[Z]:" + String.format("%.4f", z)
            );

            Log.e(
                    "변화 ", "\n[X]:" + String.format("%.4f", x)
                            + " [Y]:" + String.format("%.4f", y)
                            + " [Z]:" + String.format("%.4f", z)
            );

            for(int i=0; i<19; i++) {
                Xs[i] = Xs[i+1];
                Zs[i] = Zs[i+1];
            }

            Xs[19] = x;
            Zs[19] = z;


            double avgZ = (Zs[0] + Zs[1] + Zs[2] + Zs[3] + Zs[4]) / 5.0;

            if(System.currentTimeMillis() - detectedTime > 1000) {
                if(isLeft(Xs)) {
                    vibrator.vibrate(10);
                    Toast.makeText(getContext(), "Left shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "왼쪽");
                    detectedTime = System.currentTimeMillis();
                }
                else if(isRight(Xs)) {
                    vibrator.vibrate(10);
                    Toast.makeText(getContext(), "Right shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "오른쪽");
                    detectedTime = System.currentTimeMillis();
                }
                else if(isBack(Zs)) {
                    vibrator.vibrate(10);
                    Toast.makeText(getContext(), "Back shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "뒤쪽");
                    detectedTime = System.currentTimeMillis();
                }
                else if(isFront(Zs)) {
                    vibrator.vibrate(10);
                    Toast.makeText(getContext(), "Front shake detected", Toast.LENGTH_SHORT).show();
                    Log.e("잡힘 ", "앞쪽");
                    detectedTime = System.currentTimeMillis();
                }
            }






//            // only allow one update every 100ms.
//
//            if ((curTime - lastTime) > 20) {
//                long diffTime = (curTime - lastTime);
//                lastTime = curTime;





//                if(Round(x,4)>20.0000) {
//                    Log.d("sensor", "X Right axis: " + x);
//
//                    vibrator.vibrate(10);
//                    Toast.makeText(getContext(), "Right shake detected", Toast.LENGTH_SHORT).show();
//                }
//                else if(Round(x,4)<-20.0000) {
//                    Log.d("sensor", "X Left axis: " + x);
//                    vibrator.vibrate(10);
//                    Toast.makeText(getContext(), "Left shake detected", Toast.LENGTH_SHORT).show();
//                }
//                else if(Round(z,4)>20.0000) {
//                    Log.d("sensor", "X Left axis: " + x);
//                    vibrator.vibrate(10);
//                    Toast.makeText(getContext(), "Front shake detected", Toast.LENGTH_SHORT).show();
//                }
//                else if(Round(z,4)<-20.0000) {
//                    Log.d("sensor", "X Left axis: " + x);
//                    vibrator.vibrate(10);
//                    Toast.makeText(getContext(), "Back shake detected", Toast.LENGTH_SHORT).show();
//                }

//                double speed = Math.abs(x+y+z - lastX - lastY - lastZ) / diffTime * 10000;
//
//                // Log.d("sensor", "diff: " + diffTime + " - speed: " + speed);
//                if (speed > 1200) {
//                    //Log.d("sensor", "shake detected w/ speed: " + speed);
//                    //Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
//                }
//                lastX = x;
//                lastY = y;
//                lastZ = z;
//            }




//            long currentTime = System.currentTimeMillis();
//            long gabOfTime = (currentTime - lastTime);
//            if (gabOfTime > 100) {
//                lastTime = currentTime;
//
//                if (Math.abs(accX + accY + accZ - lastX - lastY - lastZ) / gabOfTime * 10000 > 1600) {
//                    final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
//                    vibrator.vibrate(30);
//                }
//
//                lastX = accX;
//                lastY = accY;
//                lastZ = accZ;
//            }



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public static float Round(double Rval, int Rpl) {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }

    public boolean isLeft(double[] Xs) {
        double avgX = (Xs[0] + Xs[1] + Xs[2] + Xs[3] + Xs[4]) / 5.0;
        int firstX = 20;
        int secondX;
        for(int i=5; i<20; i++) {
            if(Xs[i] < avgX - 15) {
                firstX = i;
            }
            if(Xs[i] > avgX + 50) {
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
            if(Xs[i] > avgX + 8) {
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
            if(Zs[i] < avgZ - 8) {
                firstZ = i;
            }
            if(Xs[i] > avgZ + 30) {
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
            if(Zs[i] > avgZ + 5) {
                firstZ = i;
            }
            if(Xs[i] < avgZ - 20) {
                secondZ = i;
                if(firstZ < secondZ) {
                    return true;
                }
            }
        }
        return false;
    }
}
