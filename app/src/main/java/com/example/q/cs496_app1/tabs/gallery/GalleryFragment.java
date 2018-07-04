package com.example.q.cs496_app1.tabs.gallery;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    int permsRequestCode = 200;
    String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    ArrayList<MyImage> images;
    ImageAdapter galleryAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


//    @Override
//    public void onResume() {
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        // 권한
        if(!checkPermission()) {
            requestPermission(); // 거절당했을 때 행동도 만들어야 함.
        }
        if (checkPermission()) {
            fetchAllImages();
            RecyclerView galleryRecyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler);
            galleryRecyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
            galleryRecyclerView.setLayoutManager(layoutManager);
            galleryRecyclerView.scrollToPosition(0);

            galleryAdapter = new ImageAdapter(getActivity(), GalleryFragment.this, images);
            galleryRecyclerView.setAdapter(galleryAdapter);
            galleryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GalleryFragment.this.onRefresh();
            }
        });




        return view;
    }

    private void fetchAllImages() {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.LATITUDE};

        Cursor imageCursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection,
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        ArrayList<MyImage> result = new ArrayList<>(imageCursor.getCount());

        if (imageCursor.moveToFirst()) {
            do {
                String filePath = imageCursor.getString(imageCursor.getColumnIndex(projection[0]));
                long taken = imageCursor.getLong(imageCursor.getColumnIndex(projection[1]));
                float longitude = imageCursor.getFloat(imageCursor.getColumnIndex(projection[2]));
                float latitude = imageCursor.getFloat(imageCursor.getColumnIndex(projection[3]));

//                if(new File(filePath).exists())
                result.add(new MyImage(filePath, taken, longitude, latitude));
            } while(imageCursor.moveToNext());
        } else {
            // imageCursor가 비었습니다.
        }
        imageCursor.close();

        Collections.sort(result, Collections.reverseOrder());
        this.images = result;
    }

    public void onRefresh() {
        fetchAllImages();
        galleryAdapter.notifyDataSetChanged();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(GalleryFragment.this).attach(GalleryFragment.this).commit();
        swipeRefreshLayout.setRefreshing(false);
    }


    private boolean checkPermission() {
        int resultR = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);

        return resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), perms, permsRequestCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("DEBUG", "onResume of LoginFragment");

    }
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }



}

