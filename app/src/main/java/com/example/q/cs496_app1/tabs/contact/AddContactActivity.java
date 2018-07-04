package com.example.q.cs496_app1.tabs.contact;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.q.cs496_app1.MainActivity;
import com.example.q.cs496_app1.R;
import com.example.q.cs496_app1.tabs.gallery.GalleryFragment;
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
import java.util.Date;
import java.util.List;

public class AddContactActivity extends Activity {
    private final static int MY_PERMISSION_CAMERA = 300;
    private static final int REQUEST_IMAGE_CAPTURE = 400;

    private final static int GALLERY_CODE = 1;
    private final static int CAMERA_CODE = 2;

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

    private String state;
    private File file;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);

        addProfile = (ImageButton)findViewById(R.id.profile_button);
        preview = (ImageView)findViewById(R.id.preview);

        addName = (EditText)findViewById(R.id.addName);
        addPhoneNumber = (EditText)findViewById(R.id.addPhoneNumber);
        addEmail = (EditText)findViewById(R.id.addEmail);
        save_button = (Button)findViewById(R.id.save_button);

        addPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AddContactActivity.this)
                        .setMessage("이미지를 불러올 방법을 선택하세요")
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectGallery();
                            }
                        })
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkCameraPermission();
                                sendTakePhotoIntent();
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
                    Toast.makeText(AddContactActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContactItem addContact = new ContactItem(
                        imagePath,
                        addName.getText().toString(),
                        addPhoneNumber.getText().toString(),
                        addEmail.getText().toString()
                );

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
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                        } else {
                            Toast.makeText(this,"Should have camera permission to run", Toast.LENGTH_LONG).show();
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
                        .setMessage("Camera permission not granted")
                        .setNeutralButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                    processPicture();
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

    private Bitmap processPicture() {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(imageFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exifOrientation;
        int exifDegree;

        if(exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }

        Bitmap savedImage = rotate(bitmap, exifDegree);
        preview.setImageBitmap(savedImage);
        saveImage(savedImage);
        return savedImage;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getCacheDir();
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
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

    private void saveImage(Bitmap finalBitmap) {
        OutputStream fout = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File saveDir = new File("/sdcard/DCIM/");
            if (!saveDir.exists()) { saveDir.mkdirs(); }

            File internalImage = new File(saveDir, "image_" + timeStamp + ".jpg");
            imagePath = internalImage.toString();
            Log.e("FILE", internalImage.toString());
            if(!internalImage.exists()) { internalImage.createNewFile(); }

            fout = new FileOutputStream(internalImage);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
