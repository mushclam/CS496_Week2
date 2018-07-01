package com.example.q.cs496_app1;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private Context context;
    private ArrayList mItems;

    private int lastPosition = -1;

    public ContactAdapter(Context context, ArrayList mItems) {
        this.context = context;
        this.mItems = mItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout allMenu;
        public TextView name;
        public ImageView image;
        public TextView phoneNumber;

        public ViewHolder(View view) {
            super(view);
            allMenu = (RelativeLayout)view.findViewById(R.id.allMenu);
            name = (TextView)view.findViewById(R.id.name);
            image = (ImageView)view.findViewById(R.id.image);
            phoneNumber = (TextView)view.findViewById(R.id.phoneNumber);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ContactItem item = (ContactItem)mItems.get(position);
        holder.name.setText(item.getName());
        holder.image.setImageResource(item.getImage());
        holder.phoneNumber.setText(item.getPhoneNumber());
        holder.image.setBackground(new ShapeDrawable(new OvalShape()));
        holder.image.setClipToOutline(true);

        setAnimation(holder.image, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if(position < lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
