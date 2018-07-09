package com.example.q.cs496_week2.tabs.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.q.cs496_week2.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailsContactActivity extends Activity {

    public static Context CONTACT_CONTEXT;

    Button editButton;
    Button deleteButton;

    ContactTestItem item;
    String phoneNumber;
    String email;

    ImageView tvImage;
    TextView tvName;

    RelativeLayout phoneNumberLayout;
    TextView tvPhoneNumber;

    RelativeLayout emailLayout;
    TextView tvEmail;

    int itemPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailcontact);
        CONTACT_CONTEXT = this;

        Intent intent = new Intent(this.getIntent());
        itemPosition = intent.getIntExtra("itemPosition", 0);
        item = (ContactTestItem)intent.getSerializableExtra("item");
        if (!item.getPhoneNumbers().isEmpty()) {
            phoneNumber = item.getPhoneNumbers().get(0).getPhoneNumber();
        }
        if (!item.getEmails().isEmpty()) {
            email = item.getEmails().get(0).getEmailAddress();
        }

        tvImage = (ImageView)findViewById(R.id.ind_preview);
        tvName = (TextView)findViewById(R.id.ind_name);
        tvPhoneNumber = (TextView)findViewById(R.id.ind_phoneNumber);
        tvEmail = (TextView)findViewById(R.id.ind_email);

//        Glide.with(DetailsContactActivity.this).load(image).into(tvImage);
        tvImage.setColorFilter(Color.argb(128,0,0,0));
        tvName.setText(item.getName());
        tvPhoneNumber.setText(item.getPhoneNumbers().get(0).getPhoneNumber());
        tvEmail.setText(email);

        phoneNumberLayout = (RelativeLayout)findViewById(R.id.phoneNumberLayout);
        phoneNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        });

        emailLayout = (RelativeLayout)findViewById(R.id.emailLayout);
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent 0(emailIntent);
            }
        });

        editButton = (Button)findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsContactActivity.this, EditContactActivity.class);
                intent.putExtra("itemPosition", itemPosition);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });

        deleteButton = (Button)findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DetailsContactActivity.this);
                alert.setMessage("Are you sure to DELETE?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteContact();
                                onStop();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DetailsContactActivity.this, "CANCELED", Toast.LENGTH_SHORT).show();
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

    private void deleteContact() {
        ContentResolver contactHelper = getApplicationContext().getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[]{item.getId()};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try{
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
