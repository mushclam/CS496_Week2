package com.example.q.cs496_app1.tabs.contact;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EditContactActivity extends Activity {
    private final static int MY_PERMISSION_CAMERA = 300;
    private final static int GALLERY_CODE = 0;
    private final static int CAMERA_CODE = 1;

    private String imageFilePath;
    private Uri photoUri;

    private String imagePath;
    private ImageView editPreview;
    private ImageButton editProfile;

    private EditText editName;
    private EditText editPhoneNumber;
    private EditText editEmail;
    private Button save_button;

    private int itemPosition;
    private String orgImage;
    private String orgName;
    private String orgPhoneNumber;
    private String orgEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcontact);

        Intent intent = new Intent(this.getIntent());

        editPreview = (ImageView)findViewById(R.id.editPreview);

        editName = (EditText)findViewById(R.id.editName);
        editProfile = (ImageButton)findViewById(R.id.edit_profile);
        editPhoneNumber = (EditText)findViewById(R.id.editPhoneNumber);
        editEmail = (EditText)findViewById(R.id.editEmail);
        save_button = (Button)findViewById(R.id.save_button);

        orgImage = intent.getStringExtra("image");
        orgName = intent.getStringExtra("name");
        orgPhoneNumber = intent.getStringExtra("phoneNumber");
        orgEmail = intent.getStringExtra("email");
        itemPosition = intent.getIntExtra("itemPosition", 0);

        if (orgImage != null) {
//            Glide.with(EditContactActivity.this).load(orgImage).into(editPreview);
            Bitmap bitmap = new BitmapFactory().decodeFile(orgImage);
            editPreview.setImageBitmap(bitmap);
            editPreview.setColorFilter(Color.argb(128,0,0,0));
        }
        imagePath = orgImage;
        editName.setText(orgName);
        editPhoneNumber.setText(orgPhoneNumber);
        editEmail.setText(orgEmail);

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
                                    sendTakePhotoIntent();
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

                ContactItem editContact = new ContactItem(
                        imagePath,
                        editName.getText().toString(),
                        editPhoneNumber.getText().toString(),
                        editEmail.getText().toString()
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
                    Toast.makeText(EditContactActivity.this, R.string.success_edit, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(EditContactActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            sendTakePhotoIntent();
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
//        editPreview.setImageBitmap(rotate(bitmap, exifDegree));
        return bitmap;
    }

    private Bitmap processPicture() {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
        Log.e("IFP", imageFilePath);
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
        editPreview.setImageBitmap(savedImage);
        saveImage(savedImage);
        new File(imageFilePath).delete();
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
            File saveDir = new File("/sdcard/DCIM");
            if (!saveDir.exists()) { saveDir.mkdirs(); }

            File internalImage = new File(saveDir, "image_" + timeStamp + ".jpg");
            imagePath = internalImage.toString();
            Log.e("FILE", internalImage.toString());
            if(!internalImage.exists()) { internalImage.createNewFile(); }

            fout = new FileOutputStream(internalImage);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + internalImage.getPath())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
