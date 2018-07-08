package com.example.q.cs496_app1.tabs.gallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.q.cs496_app1.MainActivity;
import com.example.q.cs496_app1.R;
import com.example.q.cs496_app1.tabs.contact.ContactFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class GalleryFragment extends Fragment {
//    private OnFragmentInteractionListener mListener;

    private Activity activity;

    RecyclerView.LayoutManager layoutManager;

    ArrayList<MyImage> images;
    ArrayList<Integer> selected_images;
    ImageAdapter galleryAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    boolean selectingMode = false;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        setHasOptionsMenu(true);

        images = new ArrayList<>();
        selected_images = new ArrayList<>();
        selectingMode = false;
        if (checkPermission()) {
            fetchAllImages();
        }

        RecyclerView galleryRecyclerView = view.findViewById(R.id.gallery_recycler);
        galleryRecyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(activity, 3);
        galleryRecyclerView.setLayoutManager(layoutManager);
        galleryRecyclerView.scrollToPosition(0);

        galleryAdapter = new ImageAdapter(activity, GalleryFragment.this, images);
        galleryRecyclerView.setAdapter(galleryAdapter);
        galleryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = view.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GalleryFragment.this.onRefresh(-1);
            }
        });

        //  onRefresh();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_gallery:

                DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set up the projection (we only need the ID)
                        for (int i = 0; i < selected_images.size(); i++) {
                            Integer index = selected_images.get(i);

                            String[] projection = {MediaStore.Images.Media._ID};

                            // Match on the file path
                            String selection = MediaStore.Images.Media.DATA + " = ?";
                            String[] selectionArgs = new String[]{images.get(index).getFilePath()};

                            // Query for the ID of the media matching the file path
                            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            ContentResolver contentResolver = activity.getContentResolver();
                            Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                            if(c != null) {
                                if (c.moveToFirst()) {
                                    // We found the ID. Deleting the item via the content provider will also remove the file
                                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                    contentResolver.delete(deleteUri, null, null);
                                } else {
                                    // File not found in media store DB
                                    Log.e("Delete ", "File not found in media store DB");
                                }

                                c.close();
                            }

                        }

                        onRefresh(-1);
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(activity)
                        .setTitle("사진 삭제하기")
                        .setPositiveButton("삭제", deleteListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();


                return true;

            case R.id.action_all_gallery:
                if(!isSelectingMode()) {
                    setSelectingMode(true);
                }
                selected_images = new ArrayList<>();
                for(Integer i=0; i<images.size(); i++) {
                    addToSelectedImages(i);
                }
                galleryAdapter.notifyDataSetChanged();
                return true;
            default:
                break;
        }

        return false;
    }

    private void fetchAllImages() {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.LATITUDE};

        Cursor imageCursor = activity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection,
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        if(imageCursor != null) {
            ArrayList<MyImage> result = new ArrayList<>(imageCursor.getCount());

            if (imageCursor.moveToFirst()) {
                do {
                    String filePath = imageCursor.getString(imageCursor.getColumnIndex(projection[0]));
                    long taken = imageCursor.getLong(imageCursor.getColumnIndex(projection[1]));
                    float longitude = imageCursor.getFloat(imageCursor.getColumnIndex(projection[2]));
                    float latitude = imageCursor.getFloat(imageCursor.getColumnIndex(projection[3]));

//                if(new File(filePath).exists())
                    result.add(new MyImage(filePath, taken, longitude, latitude));
                } while (imageCursor.moveToNext());
            } else {
                // imageCursor가 비었습니다.
                Log.e("Fetch All Images", "ImageCursor is empty");
            }
            imageCursor.close();

            Collections.sort(result, Collections.reverseOrder());
            this.images = result;
        }
    }

    public void onRefresh(final int i) {
        if (checkPermission()) {
            fetchAllImages();
            // selected_images = new ArrayList<>();
            galleryAdapter.notifyDataSetChanged();
            FragmentTransaction ft = ((MainActivity) activity).getSupportFragmentManager().beginTransaction();
            ft.detach(GalleryFragment.this).attach(GalleryFragment.this).commitAllowingStateLoss();
            if (i >= 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layoutManager.scrollToPosition(i);
                    }
                }, 100);

                Log.e("스크롤? ", String.valueOf(i));
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private boolean checkPermission() {
        int resultW = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        return resultW == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isSelectingMode() {
        return selectingMode;
    }

    public void setSelectingMode(boolean mode) {
        selectingMode = mode;
    }

    public void addToSelectedImages(Integer i) {
        Log.e("ADDED ", String.valueOf(i));
        selected_images.add(i);
    }

    public void removeFromSelectedImages(Integer i) {
        Log.e("REMOVED ", String.valueOf(i));
        selected_images.remove(i);
    }

    public boolean isSelected(Integer i) {
        return selected_images.contains(i);
    }

    public void onBack() {
        if(isSelectingMode()) {
            selected_images = new ArrayList<>();
            setSelectingMode(false);
            galleryAdapter.notifyDataSetChanged();
        } else {
            activity.finish();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    public Activity getMainActivity() {
        return activity;
    }
}

