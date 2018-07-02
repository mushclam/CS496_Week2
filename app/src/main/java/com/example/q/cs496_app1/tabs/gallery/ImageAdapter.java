package com.example.q.cs496_app1.tabs.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    private ArrayList<MyImage> images;

    public ImageAdapter(Context context, ArrayList<MyImage> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder viewHolder, final int i) {
//        viewHolder.title.setText("사진 " + String.valueOf(myImage.getImageID(i)));
        final MyImage myImage = images.get(i);
//        viewHolder.title.setText("사진 " + myUri.getFileName());

        Glide.with(context).load(myImage.getFile()).centerCrop().into(viewHolder.img);

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