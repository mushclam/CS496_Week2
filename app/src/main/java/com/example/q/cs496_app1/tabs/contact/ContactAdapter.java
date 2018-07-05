package com.example.q.cs496_app1.tabs.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.q.cs496_app1.R;
import com.example.q.cs496_app1.tabs.gallery.GalleryFragment;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private Context context;
    private ArrayList mItems;
    private RecyclerViewClickListener listener;
    private static ContactFragment fragment;

    private int lastPosition = -1;

    public ContactAdapter(Context context, ArrayList mItems, RecyclerViewClickListener listener, ContactFragment fragment) {
        this.context = context;
        this.mItems = mItems;
        this.listener = listener;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview, parent, false);
        ViewHolder holder = new ViewHolder(v, listener);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ContactItem item = (ContactItem)mItems.get(position);

        Glide.with(context).load(item.image).into(holder.image);

        holder.name.setText(item.getName());
        holder.phoneNumber.setText(item.getPhoneNumber());
        holder.image.setBackground(new ShapeDrawable(new OvalShape()));
        holder.image.setClipToOutline(true);
        if (fragment.isSelectingMode()) {
            holder.selectingBox.setVisibility(View.VISIBLE);
            if(fragment.isSelected(position)) {
                holder.selectingBox.setChecked(true);
            } else {
                holder.selectingBox.setChecked(false);
            }
        } else {
            holder.selectingBox.setVisibility(View.GONE);
        }

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public LinearLayout allMenu;

        public RelativeLayout upperMenu;
        public TextView name;
        public ImageView image;
        public TextView phoneNumber;
        public CheckBox selectingBox;

        public LinearLayout expandMenu;
        private Button buttonEdit;
        private Button buttonDelete;
        private Button buttonDetails;

        private int itemPosition;
        private WeakReference<RecyclerViewClickListener> listenerRef;

        public ViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);

            listenerRef = new WeakReference<>(listener);

            allMenu = (LinearLayout) view.findViewById(R.id.allMenu);

            upperMenu = (RelativeLayout) view.findViewById(R.id.upperMenu);
            name = (TextView)view.findViewById(R.id.name);
            image = (ImageView)view.findViewById(R.id.image);
            phoneNumber = (TextView)view.findViewById(R.id.phoneNumber);
            selectingBox = (CheckBox)view.findViewById(R.id.selectingBox);

            expandMenu = (LinearLayout)view.findViewById(R.id.expand_menu);
            buttonEdit = (Button)view.findViewById(R.id.button_edit);
            buttonDelete = (Button)view.findViewById(R.id.button_delete);
            buttonDetails = (Button)view.findViewById(R.id.button_details);

            upperMenu.setOnClickListener(this);
            selectingBox.setOnClickListener(this);

            buttonEdit.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
            buttonDetails.setOnClickListener(this);

            upperMenu.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemPosition = getAdapterPosition();
            Log.e("OnClick ", String.valueOf(itemPosition));
            ContactItem item = (ContactItem) mItems.get(itemPosition);

            if (fragment.isSelectingMode()) {
                if (v.getId() == upperMenu.getId()) {
                    if(fragment.isSelected(itemPosition)) {
                        fragment.removeFromSelectedItems(itemPosition);
                    } else {
                        fragment.addToSelectedItems(itemPosition);
                    }

                    notifyItemChanged(itemPosition);
                }
                listenerRef.get().onClicked(getAdapterPosition());
            } else {
                if (v.getId() == upperMenu.getId()) {
                    if (expandMenu.getVisibility() == LinearLayout.GONE) {
                        expand(expandMenu);
                    } else if (expandMenu.getVisibility() == LinearLayout.VISIBLE) {
                        collapse(expandMenu);
                    }

                } else if (v.getId() == buttonEdit.getId()) {
                    Intent intent = new Intent(context, EditContactActivity.class);
                    intent.putExtra("itemPosition", itemPosition);
                    intent.putExtra("image", String.valueOf(item.getImage()));
                    intent.putExtra("name", String.valueOf(item.getName()));
                    intent.putExtra("phoneNumber", String.valueOf(item.getPhoneNumber()));

                    context.startActivity(intent);

                } else if (v.getId() == buttonDelete.getId()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Are you sure to DELETE?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Gson gson = new Gson();

                                    try {
                                        mItems.remove(itemPosition);

                                        String json = gson.toJson(mItems);

                                        FileOutputStream fos = context.getApplicationContext()
                                                .openFileOutput("test.json", Context.MODE_PRIVATE);
                                        fos.write(json.getBytes());
                                        fos.close();
                                        Toast.makeText(context, "Delete Success", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context, "CANCELED", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create().show();

                } else if (v.getId() == buttonDetails.getId()) {
                    Intent intent = new Intent(context, DetailsContactActivity.class);
                    intent.putExtra("itemPosition", itemPosition);
                    intent.putExtra("image", String.valueOf(item.getImage()));
                    intent.putExtra("name", String.valueOf(item.getName()));
                    intent.putExtra("phoneNumber", String.valueOf(item.getPhoneNumber()));
                    intent.putExtra("email", String.valueOf(item.getEmail()));

                    context.startActivity(intent);

                } else {
                    Toast.makeText(context, "NOT ANY BUTTON" + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }

                listenerRef.get().onClicked(getAdapterPosition());
            }

        }

        @Override
        public boolean onLongClick(View view) {
            final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(30);

            Log.e("onLongClick", String.valueOf(itemPosition));

            itemPosition = getAdapterPosition();

            if (!fragment.isSelectingMode()) {
                fragment.setSelectingMode(true);
                notifyDataSetChanged();
            }

            if(fragment.isSelected(itemPosition)) {
                fragment.removeFromSelectedItems(itemPosition);
            } else {
                fragment.addToSelectedItems(itemPosition);
            }

            notifyItemChanged(itemPosition);

            listenerRef.get().onClicked(itemPosition);

            return true;
        }
    }
    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

        };

        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density) * 4);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
//        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 4);
        v.startAnimation(a);
    }

    private Bitmap sendPicture(String imagePath) {
        Log.e("PATH", imagePath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        return rotate(bitmap, exifDegree);
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

}
