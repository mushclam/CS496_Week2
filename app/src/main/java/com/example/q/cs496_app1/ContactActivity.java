package com.example.q.cs496_app1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity {

    TextView tvName;
    TextView tvPhoneNumber;
    Button editButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Intent intent = new Intent(this.getIntent());
        String name = intent.getStringExtra("name");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        tvName = (TextView)findViewById(R.id.ind_name);
        tvPhoneNumber = (TextView)findViewById(R.id.ind_phoneNumber);
        tvName.setText(name);
        tvPhoneNumber.setText(phoneNumber);

        editButton = (Button)findViewById(R.id.edit_button)
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactActivity.this, "Not Implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
