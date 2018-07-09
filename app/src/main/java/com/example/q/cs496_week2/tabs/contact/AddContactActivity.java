package com.example.q.cs496_week2.tabs.contact;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.q.cs496_week2.CameraProcessing;
import com.example.q.cs496_week2.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends Activity {
    private final static int MY_PERMISSION_CAMERA = 300;

    private final static int GALLERY_CODE = 1;
    private final static int CAMERA_CODE = 400;

    List<ContactItem> orgList;

    private ImageButton addProfile;
    private ImageView preview;
    private String imagePath;

    private EditText addName;
    private EditText addPhoneNumber;
    private EditText addEmail;

    private Button save_button;

    private String imageFilePath;
    private Uri photoUri;
    private CameraProcessing cameraProcessing;

    private String state;
    private File file;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);
        cameraProcessing = new CameraProcessing(this);

        addProfile = (ImageButton)findViewById(R.id.profile_button);
        preview = (ImageView)findViewById(R.id.preview);

        addName = (EditText)findViewById(R.id.addName);
        addPhoneNumber = (EditText)findViewById(R.id.addPhoneNumber);
        addEmail = (EditText)findViewById(R.id.addEmail);
        save_button = (Button)findViewById(R.id.save_button);

        preview.setColorFilter(Color.argb(128,0,0,0));

        addPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AddContactActivity.this)
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
                                int permissionCheck = ContextCompat.checkSelfPermission(AddContactActivity.this,
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
            Gson gson = new Gson();

            @Override
            public void onClick(View v) {
                if(addName.getText().toString().equals("")) {
                    Toast.makeText(AddContactActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Phone> savePhone = new ArrayList<>();
                savePhone.add(new Phone(addPhoneNumber.getText().toString(), null));
                ArrayList<Email> saveEmail = new ArrayList<>();
                saveEmail.add(new Email(addEmail.getText().toString(), null, null));

                ContactTestItem addItem = new ContactTestItem(
                        null,
                        imagePath,
                        addName.getText().toString(),
                        savePhone,
                        saveEmail,
                        null,
                        null
                );

                addContact(addItem);
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
//                            finish();
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
            preview.setImageBitmap(bitmap);
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
                    new getImage(data.getData()).execute();
                } break;
                case CAMERA_CODE: {
                    Bitmap result = cameraProcessing.resultProcessing();
                    preview.setImageBitmap(result);
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
//        preview.setImageBitmap(rotate(bitmap, exifDegree));
        return bitmap;
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
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

    public boolean addContact(ContactTestItem item){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        int index = ops.size();

        // Adding insert operation to operations list
        // For insert a new raw contact in the ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // For insert display name in the ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, item.getName())
                .build());

        // For insert Mobile Number in the ContactsContract.Data
        for (int i = 0; i < item.getPhoneNumbers().size(); ++i){ // contactNumbers.size() should be > 0 (since I did not added size=0 in DetailActivity
            String number = item.getPhoneNumbers().get(i).getPhoneNumber();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }


        // For insert Work Email in the ContactsContract.Data
        for (int i = 0; i < item.getEmails().size(); ++i){
            Email email_info = item.getEmails().get(i);
            String contactEmail = email_info.getEmailAddress();
            String contactEmailType = email_info.getEmailType();
            int emailType = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
//            switch (contactEmailType) {
//                case "개인":
//                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
//                    break;
//                case "직장":
//                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
//                    break;
//            }
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                    .build());
        }

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
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
