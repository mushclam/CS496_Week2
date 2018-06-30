package com.example.q.cs496_app1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ContactActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Intent intent = new Intent(this.getIntent());
        String s = intent.getStringExtra("text");
        TextView textView = (TextView)findViewById(R.id.ind_name);
        textView.setText(s);
    }
}
