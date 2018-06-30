package com.example.q.cs496_app1.tabs.gallery;

import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.q.cs496_app1.R;
import java.util.ArrayList;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class GalleryFragment extends Fragment {
//    private OnFragmentInteractionListener mListener;

    int permsRequestCode = 200;
    String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

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
//        if (getArguments() != null) {
//
//        }
    }


//    private ArrayList<MyImage> prepareData() {
//        ArrayList<MyImage> images = new ArrayList<>();
//        Images img = new Images();
//        for(int i = 0; i < img.image_titles.length; i++) {
//            MyImage myImage = new MyImage();
//            myImage.setImageTitle(img.image_titles[i]);
//            myImage.setImageID(img.image_ids[i]);
//            images.add(myImage);
//        }
//        return images;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        // 권한
        if (!checkPermission()) {
            Toast.makeText(getActivity(), "권한 없음!", Toast.LENGTH_SHORT).show();
            requestPermission();
        }

        RecyclerView galleryRecyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler);
        galleryRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        galleryRecyclerView.setLayoutManager(layoutManager);
        galleryRecyclerView.scrollToPosition(0);

        //ArrayList<MyImage> images = prepareData();
        MyImage myImage = new MyImage();
        // myImage.fetchAllImages(getActivity());
        ImageAdapter galleryAdapter = new ImageAdapter(getActivity(), myImage);
        galleryRecyclerView.setAdapter(galleryAdapter);
        galleryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return view;
    }

    private boolean checkPermission() {
        int resultR = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);

        return resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), perms, permsRequestCode);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 20:
////                boolean locationAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
////                boolean cameraAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
//
//                break;
//        }
//    }

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

