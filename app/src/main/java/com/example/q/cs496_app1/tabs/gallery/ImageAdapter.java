package com.example.q.cs496_app1.tabs.gallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.q.cs496_app1.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
//    private MyImage myImage;
    private Context context;
    private static GalleryFragment fragment;
    private ArrayList<MyImage> images;


    public ImageAdapter(Context context, GalleryFragment fragment, ArrayList<MyImage> images) {
        this.context = context;
        this.fragment = fragment;
        this.images = images;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final MyImage myImage = images.get(i);

        Glide.with(context).load(myImage.getFile()).centerCrop().into(viewHolder.img);

        viewHolder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                vibrator.vibrate(30);

//                Intent dialogIntent = new Intent(context, DialogActivity.class);
//                dialogIntent.putExtra("FILEPATH", myImage.getFilePath());
//
//                context.startActivity(dialogIntent);
                DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Set up the projection (we only need the ID)
                        String[] projection = { MediaStore.Images.Media._ID };

                        // Match on the file path
                        String selection = MediaStore.Images.Media.DATA + " = ?";
                        String[] selectionArgs = new String[] { myImage.getFilePath() };

                        // Query for the ID of the media matching the file path
                        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver contentResolver = context.getContentResolver();
                        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                        if (c.moveToFirst()) {
                            // We found the ID. Deleting the item via the content provider will also remove the file
                            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                            contentResolver.delete(deleteUri, null, null);
                        } else {
                            // File not found in media store DB
                        }

                        c.close();

                        fragment.onRefresh();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(context)
                        .setTitle("사진 삭제하기")
                        .setPositiveButton("삭제", deleteListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();

                return true;
            }
        });

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("PATH : ", myImage.getFileName());

                Intent imageIntent = new Intent(context, ImageActivity.class);

                imageIntent.putExtra("INDEX", i);
                imageIntent.putExtra("IMAGE", images);

                context.startActivity(imageIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            img = view.findViewById(R.id.imageView);
        }
    }
}