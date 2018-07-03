package com.example.q.cs496_app1.tabs.gallery;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.q.cs496_app1.R;

import java.io.File;

public class DialogActivity extends Activity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_dialog);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String filePath = getIntent().getStringExtra("FILEPATH");

//                if(new File(filePath).getAbsoluteFile().delete()) {
//                    // Log.e("잘 지워졌음.", filePath);
//                    Toast.makeText(getApplicationContext(), "지워짐 " + filePath, Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    // Log.e("안 지워졌음.", filePath);
//                    Toast.makeText(getApplicationContext(), "안 지워짐 " + filePath, Toast.LENGTH_SHORT).show();
//                }



//                String[] projection = { MediaStore.Images.Media. };
//
//// Match on the file path
//                String selection = MediaStore.Images.Media.DATA + " = ?";
//                String[] selectionArgs = new String[] { imageFile.getAbsolutePath() };
//
//                // Query for the ID of the media matching the file path
//                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                ContentResolver contentResolver = getActivity().getContentResolver();
//                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
//                if (c.moveToFirst()) {
//                    // We found the ID. Deleting the item via the content provider will also remove the file
//                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
//                    contentResolver.delete(deleteUri, null, null);
//                } else {
//                    // File not found in media store DB
//                }
//                c.close();



                finish();
            }
        });
    }


}
