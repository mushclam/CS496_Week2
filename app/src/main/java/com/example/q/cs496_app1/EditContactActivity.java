package com.example.q.cs496_app1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class EditContactActivity extends Activity {

    private EditText editName;
    private EditText editPhoneNumber;
    private Button save_button;

    private int itemPosition;
    private String orgName;
    private String orgPhoneNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcontact);

        Intent intent = new Intent(this.getIntent());

        editName = (EditText)findViewById(R.id.editName);
        editPhoneNumber = (EditText)findViewById(R.id.editPhoneNumber);
        save_button = (Button)findViewById(R.id.save_button);

        editPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        orgName = intent.getStringExtra("name");
        orgPhoneNumber = intent.getStringExtra("phoneNumber");
        itemPosition = intent.getIntExtra("itemPosition", 0);

        editName.setText(orgName);
        editPhoneNumber.setText(orgPhoneNumber);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();

                ContactItem editContact = new ContactItem(
                        R.drawable.ic_launcher_foreground,
                        editName.getText().toString(),
                        editPhoneNumber.getText().toString());

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
                    if(orgName.equals(orgList.get(itemPosition).getName())) {
                        orgList.set(itemPosition, editContact);
                    } else {
                        Toast.makeText(EditContactActivity.this,
                                "Not exist contact : " + orgName + " != " +
                                        itemPosition + "=" + orgList.get(itemPosition).getName(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String json = gson.toJson(orgList);

                    FileOutputStream fos = openFileOutput("test.json", Context.MODE_PRIVATE);
                    fos.write(json.getBytes());
                    fos.close();
                    Toast.makeText(EditContactActivity.this, "Edit Success", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(EditContactActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                ((ContactActivity)ContactActivity.CONTACT_CONTEXT).onPause();
                onPause();
            }
        });
    }

    public void onPause() {
        super.onPause();
        finish();
    }
}
