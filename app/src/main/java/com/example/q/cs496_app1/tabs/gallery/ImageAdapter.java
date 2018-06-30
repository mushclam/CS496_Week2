package com.example.q.cs496_app1.tabs.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private ArrayList<Uri> images_uri;

    public ImageAdapter(Context context, ArrayList<Uri> uris) {
        this.context = context;
        this.images_uri = uris;
//        this.myImage = myImage;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder viewHolder, final int i) {
//        viewHolder.title.setText("사진 " + String.valueOf(myImage.getImageID(i)));
        viewHolder.title.setText("사진 " + images_uri.get(i).toString());
//        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        viewHolder.img.setImageResource((galleryList.get(i).getImageID()));
//        Glide.with(context).load(myImage.getImageID(i)).centerCrop().into(viewHolder.img);
        Glide.with(context).load(new File(images_uri.get(i).getPath())).centerCrop().into(viewHolder.img);

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(context, "onClick" + galleryList.get(i).getImageTitle(), Toast.LENGTH_SHORT).show();
                Intent imageIntent = new Intent(context, ImageActivity.class);

                imageIntent.putExtra("INDEX", i);
//                imageIntent.putExtra("IMAGE", myImage);
                imageIntent.putExtra("IMAGE", images_uri);
//                Bundle args = new Bundle();
//                args.putSerializable("images", galleryList);
                // imageIntent.putExtra("BUNDLE", args);

                context.startActivity(imageIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
//        return myImage.getSize();
        return images_uri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            title = (TextView)view.findViewById(R.id.imageText);
            img = (ImageView) view.findViewById(R.id.imageView);
        }
    }
}