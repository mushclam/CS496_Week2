package com.example.q.cs496_week2.tabs.contact;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.q.cs496_week2.CameraProcessing;
import com.example.q.cs496_week2.MainActivity;
import com.example.q.cs496_week2.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EditContactActivity extends Activity {
    private final static int MY_PERMISSION_CAMERA = 300;
    private final static int GALLERY_CODE = 0;
    private final static int CAMERA_CODE = 400;

    private CameraProcessing cameraProcessing;

    private String imagePath;
    private ImageView editPreview;
    private ImageButton editProfile;

    private EditText editName;
    private EditText editPhoneNumber;
    private EditText editEmail;
    private Button save_button;

    private int itemPosition;
    private ContactTestItem item;
    private String phoneNumber;
    private String email;

    private String orgImage;
    private String orgName;
    private String orgPhoneNumber;
    private String orgEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcontact);
        cameraProcessing = new CameraProcessing(this);

        Intent intent = new Intent(this.getIntent());

        editPreview = (ImageView)findViewById(R.id.editPreview);

        editName = (EditText)findViewById(R.id.editName);
        editProfile = (ImageButton)findViewById(R.id.edit_profile);
        editPhoneNumber = (EditText)findViewById(R.id.editPhoneNumber);
        editEmail = (EditText)findViewById(R.id.editEmail);
        save_button = (Button)findViewById(R.id.save_button);

        itemPosition = intent.getIntExtra("itemPosition", 0);
        item = (ContactTestItem) intent.getSerializableExtra("item");
        if (!item.getPhoneNumbers().isEmpty()) {
            phoneNumber = item.getPhoneNumbers().get(0).getPhoneNumber();
        }
        if (!item.getEmails().isEmpty()) {
            email = item.getEmails().get(0).getEmailAddress();
        }

        if (orgImage != null) {
//            Glide.with(EditContactActivity.this).load(orgImage).into(editPreview);
//            Bitmap bitmap = new BitmapFactory().decodeFile(orgImage);
//            editPreview.setImageBitmap(bitmap);
//            editPreview.setColorFilter(Color.argb(128,0,0,0));
        }
//        imagePath = orgImage;
        editName.setText(item.getName());
        editPhoneNumber.setText(phoneNumber);
        editEmail.setText(email);

        editPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditContactActivity.this)
                        .setMessage(R.string.load_image)
                        .setNegativeButton(R.string.load_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectGallery();
                            }
                        })
                        .setPositiveButton(R.string.load_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int permissionCheck = ContextCompat.checkSelfPermission(EditContactActivity.this,
                                        Manifest.permission.CAMERA);

                                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                                    checkCameraPermission();
                                } else {
                                    cameraProcessing.sendTakePhotoIntent();
                                }
                            }
                        })
                        .create().show();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editName.getText().toString().equals("")) {
                    Toast.makeText(EditContactActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                Gson gson = new Gson();

                ArrayList<Phone> savePhone = new ArrayList<>();
                item.getPhoneNumbers().get(0).setPhoneNumber(editPhoneNumber.getText().toString());
                savePhone.add(item.getPhoneNumbers().get(0));

                ArrayList<Email> saveEmail = new ArrayList<>();
                item.getEmails().get(0).setEmailAddress(editEmail.getText().toString());
                saveEmail.add(item.getEmails().get(0));

                ContactTestItem editItem = new ContactTestItem(
                        item.getId(),
                        imagePath,
                        editName.getText().toString(),
                        savePhone,
                        saveEmail,
                        item.getNote(),
                        item.getStarred()
                        );

                updateContact(editItem);

                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            cameraProcessing.sendTakePhotoIntent();
                        } else {
                            Toast.makeText(this,R.string.require_camera, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } break;
        }

    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.camera_permission)
                        .setNeutralButton(R.string.settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSION_CAMERA);
            }
        }
    }

    private class getImage extends AsyncTask<String, String, Bitmap> {
        Uri imgUri;

        public getImage(Uri imgUri) {
            this.imgUri = imgUri;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return sendPicture(imgUri);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            editPreview.setImageBitmap(bitmap);
        }
    }

    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case GALLERY_CODE: {
                    new EditContactActivity.getImage(data.getData()).execute();
                } break;
                case CAMERA_CODE: {
                    Bitmap result = cameraProcessing.resultProcessing();
                    editPreview.setImageBitmap(result);
                    imagePath = cameraProcessing.imagePath;
                }
            }
        }
    }

    private Bitmap sendPicture(Uri imgUri) {
        imagePath = getRealPathFromURI(imgUri);
        Log.e("PATH", imagePath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = rotate(BitmapFactory.decodeFile(imagePath), exifDegree);
//        editPreview.setImageBitmap(rotate(bitmap, exifDegree));
        return bitmap;
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int columnIdx = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            columnIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(columnIdx);
    }

    public boolean updateContact(ContactTestItem item) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();


        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                        + "=?", new String[]{item.getId(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, item.getName())
                .build());


        for (int i = 0; i < item.getPhoneNumbers().size(); ++i){
            Phone pair = item.getPhoneNumbers().get(i);
            String number = pair.getPhoneNumber();
            String data_id = pair.getDataId();
            assert data_id != null;
            ops.add(ContentProviderOperation
                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                                    + "=? AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=? AND " + ContactsContract.Data._ID + "=?"
                            , new String[]{item.getId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                    String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE), data_id})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());
        }

        for (int i = 0; i < item.getEmails().size(); ++i){
            Email email_info = item.getEmails().get(i);
            String contactEmail = email_info.getEmailAddress();
            String contactEmailType = email_info.getEmailType();
            String data_id = email_info.getDataId();
            int emailType = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
            switch (contactEmailType) {
                case "개인":
                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
                    break;
                case "직장":
                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
                    break;
            }
            System.out.println("Updated string : " + contactEmail);

            assert data_id != null;
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data._ID + "=?"
                            , new String[]{item.getId(), ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, data_id})
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                    .build());
        }

        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?"
                        , new String[]{item.getId(), ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, item.getNote())
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
