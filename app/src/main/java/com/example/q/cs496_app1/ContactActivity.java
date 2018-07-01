package com.example.q.cs496_app1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity {

    public static Context CONTACT_CONTEXT;

    TextView tvName;
    TextView tvPhoneNumber;
    Button editButton;

    int itemPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        CONTACT_CONTEXT = this;

        Intent intent = new Intent(this.getIntent());
        String name = intent.getStringExtra("name");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        itemPosition = intent.getIntExtra("itemPosition", 0);

        tvName = (TextView)findViewById(R.id.ind_name);
        tvPhoneNumber = (TextView)findViewById(R.id.ind_phoneNumber);
        tvName.setText(name);
        tvPhoneNumber.setText(phoneNumber);

        editButton = (Button)findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, EditContactActivity.class);
                intent.putExtra("itemPosition", itemPosition);
                intent.putExtra("name", tvName.getText());
                intent.putExtra("phoneNumber", tvPhoneNumber.getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
