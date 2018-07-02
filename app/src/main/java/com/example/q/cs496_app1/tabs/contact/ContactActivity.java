package com.example.q.cs496_app1.tabs.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.cs496_app1.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class ContactActivity extends Activity {

    public static Context CONTACT_CONTEXT;

    TextView tvName;
    TextView tvPhoneNumber;
    Button editButton;
    Button deleteButton;

    TextView result;

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

        result = (TextView)findViewById(R.id.result);

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

        deleteButton = (Button)findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ContactActivity.this);
                alert.setMessage("Are you sure to DELETE?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Gson gson = new Gson();

                                try {
                                    StringBuffer data = new StringBuffer();
                                    FileInputStream org = openFileInput("test.json");
                                    BufferedReader br = new BufferedReader(new InputStreamReader(org));
                                    String str = br.readLine();
                                    while (str != null) {
                                        data.append(str + "\n");
                                        str = br.readLine();
                                    }

                                    List<ContactItem> orgList =  gson.fromJson(data.toString(),
                                            new TypeToken<List<ContactItem>>(){}.getType());
                                    Collections.sort(orgList, new ContactSorting());

                                    orgList.remove(itemPosition);

                                    String json = gson.toJson(orgList);
                                    result.setText(json);

                                    FileOutputStream fos = openFileOutput("test.json", Context.MODE_PRIVATE);
                                    fos.write(json.getBytes());
                                    fos.close();
                                    Toast.makeText(ContactActivity.this, "Delete Success", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Toast.makeText(ContactActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                onStop();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(ContactActivity.this, "CANCELED", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create().show();
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
