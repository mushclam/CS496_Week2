package com.example.q.cs496_app1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Intent intent = new Intent(this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(intent);
                finish();
            }
        }, 500);


    }
}
