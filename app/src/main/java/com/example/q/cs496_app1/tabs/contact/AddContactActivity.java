package com.example.q.cs496_app1.tabs.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.q.cs496_app1.MainActivity;
import com.example.q.cs496_app1.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends Activity {

    List<ContactItem> orgList;

    private EditText addName;
    private EditText addPhoneNumber;
    private Button save_button;

    private String state;
    private File file;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);

        addName = (EditText)findViewById(R.id.addName);
        addPhoneNumber = (EditText)findViewById(R.id.addPhoneNumber);
        save_button = (Button)findViewById(R.id.save_button);

        addPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        save_button.setOnClickListener(new View.OnClickListener() {
            Gson gson = new Gson();

            @Override
            public void onClick(View v) {

                ContactItem addContact = new ContactItem(
                        R.drawable.ic_launcher_foreground,
                        addName.getText().toString(),
                        addPhoneNumber.getText().toString());

                try {
                    StringBuffer data = new StringBuffer();
                    FileInputStream org = openFileInput("test.json");
                    BufferedReader br = new BufferedReader(new InputStreamReader(org));
                    String str = br.readLine();
                    while (str != null) {
                        data.append(str + "\n");
                        str = br.readLine();
                    }
                    orgList = gson.fromJson(data.toString(), new TypeToken<List<ContactItem>>(){}.getType());

                    if (orgList == null) {
                        orgList = new ArrayList<ContactItem>();
                    }
                    orgList.add(addContact);
                    String json = gson.toJson(orgList);

                    FileOutputStream fos = openFileOutput("test.json", Context.MODE_PRIVATE);
                    fos.write(json.getBytes());
                    fos.close();
                    Toast.makeText(AddContactActivity.this, "Save Success", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(AddContactActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                ((MainActivity)MainActivity.MAIN_CONTEXT).finish();
                Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                startActivity(intent);
                onPause();

//                if(ContextCompat.checkSelfPermission(AddContactActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if(ActivityCompat.shouldShowRequestPermissionRationale(AddContactActivity.this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    } else {
//                        ActivityCompat.requestPermissions(AddContactActivity.this,
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//                    }
//                }

//                File folder = getFilesDir();
//                if(!folder.exists()) {
//                    folder.mkdirs();
//                    Toast.makeText(AddContactActivity.this, "Mkdirs Success", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//
//                }
//                return;
//        }
//    }

//    public boolean checkExternalStorage() {
//        state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            Toast.makeText(AddContactActivity.this, "External Storage r/w available", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            Toast.makeText(AddContactActivity.this, "External Storage ro available", Toast.LENGTH_SHORT).show();
//            return false;
//        } else {
//            Toast.makeText(AddContactActivity.this, "External Storage can't use", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }
}
