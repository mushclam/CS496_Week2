package com.example.q.cs496_app1.tabs.contact;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.q.cs496_app1.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int NOT_EXPANDED = 0;
    private static final int EXPANDED = 1;
    private int isExpanded = NOT_EXPANDED;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context mContext;

    // variable for recycler view.
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    SwipeRefreshLayout swipeRefreshLayout;

    TextView viewEmpty;

    public static ContactFragment CONTACT_FRAGMENT_CONTEXT;

//    private OnFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // indicate context of fragment
        mContext = getActivity();
        CONTACT_FRAGMENT_CONTEXT = this;

        //link with recycler view of xml
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // add divider between each list items.
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, new LinearLayoutManager(mContext).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        viewEmpty = (TextView) view.findViewById(R.id.isEmpty);

        ArrayList items = new ArrayList<>();

        // Add Contact item to ArrayList
        final List<ContactItem> contactList = this.LoadJson();
        if (contactList == null || contactList.isEmpty()) {
            viewEmpty.setText("Any Contact isn't exist");
        } else {
            Collections.sort(contactList, new ContactSorting());
            for (int i = 0; i < contactList.size(); i++) {
                items.add(contactList.get(i));
            }
        }

        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        // link to adapter
        adapter = new ContactAdapter(mContext, items, new RecyclerViewClickListener() {
            @Override
            public void onClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ContactFragment.this.onRefresh();
            }
        });

        return view;
    }

    // Load Json file and read content. convert json string to contact list.
    public List<ContactItem> LoadJson() {
        String json = null;
        List<ContactItem> itemList = null;
        Gson gson = new Gson();

        try {
            File file = new File(mContext.getFilesDir() + "/test.json");
            if(!file.exists()) {
                FileOutputStream fos = getActivity().openFileOutput("test.json", Context.MODE_PRIVATE);
                fos.close();
            }
            StringBuffer data = new StringBuffer();
            FileInputStream fis = getActivity().openFileInput("test.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String str = br.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = br.readLine();
            }

            itemList = gson.fromJson(data.toString(), new TypeToken<List<ContactItem>>(){}.getType());
            return itemList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onRefresh() {
        adapter.notifyDataSetChanged();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(ContactFragment.this).attach(ContactFragment.this).commit();
        swipeRefreshLayout.setRefreshing(false);
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
