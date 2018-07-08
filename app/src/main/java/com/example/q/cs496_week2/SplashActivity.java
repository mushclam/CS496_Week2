package com.example.q.cs496_week2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {

    int permsRequestCode = 200;
    String[] perms = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.WRITE_CONTACTS"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(checkPermission()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            requestPermission();
        }

    }

    private boolean checkPermission() {
        int resultW = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result_contact = ContextCompat.checkSelfPermission(this, WRITE_CONTACTS);
        return resultW == PackageManager.PERMISSION_GRANTED && result_contact == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, perms, permsRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("퍼미션", "결과 받음");
        switch (requestCode) {
            case 200: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("퍼미션", "허용");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();

                } else {
                    Log.e("퍼미션", "거절");
                    Toast.makeText(this, R.string.why_not_grant, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}
