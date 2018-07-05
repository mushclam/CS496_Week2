package com.example.q.cs496_app1.tabs.third;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.example.q.cs496_app1.R;

public class Third_Setting_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third__setting_);

        final Context mContext = this;

        final SeekBar sb_l  = findViewById(R.id.seekBar_L);
        final SeekBar sb_r  = findViewById(R.id.seekBar_R);
        final SeekBar sb_u  = findViewById(R.id.seekBar_U);
        final SeekBar sb_d  = findViewById(R.id.seekBar_D);

        Button button_finish = findViewById(R.id.button_finish);

        SharedPreferences sensPref = getSharedPreferences("Sensitivity", MODE_PRIVATE);

        sb_l.setProgress(sensPref.getInt("left", 5));
        sb_r.setProgress(sensPref.getInt("right", 5));
        sb_u.setProgress(sensPref.getInt("up", 5));
        sb_d.setProgress(sensPref.getInt("down", 5));

        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sensPref = getSharedPreferences("Sensitivity", MODE_PRIVATE);
                SharedPreferences.Editor editor = sensPref.edit();
                editor.putInt("left", sb_l.getProgress());
                editor.putInt("right", sb_r.getProgress());
                editor.putInt("up", sb_u.getProgress());
                editor.putInt("down", sb_d.getProgress());
                editor.apply();

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);

                Toast.makeText(mContext, R.string.sensitive_change, Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }
}
