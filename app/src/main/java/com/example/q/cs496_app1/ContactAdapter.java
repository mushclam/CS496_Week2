package com.example.q.cs496_app1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.textView);
            imageView = (ImageView)view.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactItem item = (ContactItem)mItems.get(position);
        holder.textView.setText(item.getImageTitle());
        holder.imageView.setImageResource(item.getImage());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

//    private void setAnimation(View viewToAnimate, int position) {
//        if(position < lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
//        }
//    }
}
