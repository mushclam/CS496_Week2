package com.example.q.cs496_week2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraProcessing {

    private final static int CAMERA_CODE = 400;

    private String imageTempPath;
    public String imagePath;
    private Uri photoUri;

    private Context mContext;

    public CameraProcessing(Context mContext) {
        this.mContext = mContext;
    }

    public Bitmap resultProcessing() {
        Bitmap bitmap = BitmapFactory.decodeFile(imageTempPath);
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(imageTempPath);
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
        saveImage(savedImage);
        new File(imageTempPath).delete();
        return savedImage;
    }

    public void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(mContext, mContext.getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                ((Activity)mContext).startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = mContext.getCacheDir();
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageTempPath = image.getAbsolutePath();
        Log.e("createImageFile", imageTempPath);
        return image;
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

    public void saveImage(Bitmap finalBitmap) {
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
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + internalImage.getPath())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
