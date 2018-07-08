package com.example.q.cs496_app1.tabs.gallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.q.cs496_app1.R;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final MyImage myImage = images.get(i);

        Glide.with(context).load(myImage.getFile()).centerCrop().into(viewHolder.img);
        if(fragment.isSelectingMode()) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            if(fragment.isSelected(i)) {
                viewHolder.checkBox.setChecked(true);
                viewHolder.img.setColorFilter(Color.parseColor("#979797"), PorterDuff.Mode.MULTIPLY);
            } else {
                viewHolder.checkBox.setChecked(false);
                viewHolder.img.setColorFilter(null);
            }
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.img.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ConstraintLayout imageAdapter;
        private ImageView img;
        private CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);

            imageAdapter = view.findViewById(R.id.image_adapter);
            img = view.findViewById(R.id.imageView);
            checkBox = view.findViewById(R.id.checkBox_temp);
            imageAdapter.setOnLongClickListener(this);
            imageAdapter.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(30);

            if(!fragment.isSelectingMode()) {
                fragment.setSelectingMode(true);
                notifyDataSetChanged();
            }

            Log.e("LongClick Pos ", String.valueOf(getLayoutPosition()));

            if(fragment.isSelected(getLayoutPosition())) {
                fragment.removeFromSelectedImages(getLayoutPosition());
            } else {
                fragment.addToSelectedImages(getLayoutPosition());
            }

            notifyItemChanged(getLayoutPosition());

            return true;
        }



        @Override
        public void onClick(View view) {
            if(fragment.isSelectingMode()) {
                if(fragment.isSelected(getLayoutPosition())) {
                    fragment.removeFromSelectedImages(getLayoutPosition());
                } else {
                    fragment.addToSelectedImages(getLayoutPosition());
                }

                notifyItemChanged(getLayoutPosition());
            } else {
                Intent imageIntent = new Intent(context, ImageActivity.class);

                imageIntent.putExtra("INDEX", getLayoutPosition());
                imageIntent.putExtra("IMAGE", images);

                fragment.getMainActivity().startActivityForResult(imageIntent, 3000);
            }
        }
    }
}