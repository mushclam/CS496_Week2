package com.example.q.cs496_app1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.q.cs496_app1.tabs.contact.RecyclerViewClickListener;

import java.util.ArrayList;

public class ThirdAdapter extends RecyclerView.Adapter<ThirdAdapter.ViewHolder> {
    private Context pContext;
    private ArrayList mItems;
    private RecyclerViewClickListener listener;

    public ThirdAdapter(Context pContext, ArrayList mItems, RecyclerViewClickListener listener) {
        this.pContext = pContext;
        this.mItems = mItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        ViewHolder holder = new ViewHolder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostItem item = (PostItem) mItems.get(position);
        holder.postTitle.setText(item.getTitle());
        holder.postContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout postItem;
        public TextView postTitle;
        public TextView postContent;

        public ViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);

            postItem = (LinearLayout) view.findViewById(R.id.postItem);
            postTitle = (TextView) view.findViewById(R.id.postTitle);
            postContent = (TextView) view.findViewById(R.id.postContent);

            postItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == postItem.getId()) {

            }
        }
    }
}
