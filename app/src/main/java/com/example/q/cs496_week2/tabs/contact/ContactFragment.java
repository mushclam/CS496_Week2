package com.example.q.cs496_week2.tabs.contact;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.cs496_week2.MainActivity;
import com.example.q.cs496_week2.R;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ContactFragment extends Fragment {
    Activity activity;

    // variable for recycler view.
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList items = new ArrayList<>();
    ArrayList<Integer> selectedItems;

    boolean selectingMode = false;

    SwipeRefreshLayout swipeRefreshLayout;

    TextView viewEmpty;

    public static ContactFragment CONTACT_FRAGMENT_CONTEXT;

//    private OnFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        selectedItems = new ArrayList<>();
        selectingMode = false;

        setHasOptionsMenu(true);

        // indicate context of fragment
        CONTACT_FRAGMENT_CONTEXT = this;

        //link with recycler view of xml
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // add divider between each list items.
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, new LinearLayoutManager(activity).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        viewEmpty = view.findViewById(R.id.isEmpty);

        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = view.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ContactFragment.this.onRefresh();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_all_contact) {
            if(!isSelectingMode()) {
                setSelectingMode(true);
            }
            selectedItems = new ArrayList<>();
            for(Integer i=0; i<items.size(); i++) {
                addToSelectedItems(i);
            }
            adapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.action_deleteContacts) {
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setMessage(R.string.alert_delete)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Gson gson = new Gson();

                            try {
                                Collections.sort(selectedItems, Collections.reverseOrder());
                                for(int index : selectedItems) {
                                    items.remove(index);
                                }

                                String json = gson.toJson(items);

                                FileOutputStream fos = activity
                                        .openFileOutput("test.json", Context.MODE_PRIVATE);
                                fos.write(json.getBytes());
                                fos.close();
                                Toast.makeText(activity, R.string.success_delete, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            onResume();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(activity, "CANCELED", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create().show();
//            try {
//                File file = new File(activity.getFilesDir() + "/test.json");
//                if (!file.exists()){
//                    Toast.makeText(activity, activity.getFilesDir() + " + Not Exist", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(activity, activity.getFilesDir() + " + Exist", Toast.LENGTH_SHORT).show();
//                    if(file.delete()) {
//                        Toast.makeText(activity, activity.getFilesDir() + " + Deleted", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        return super.onOptionsItemSelected(item);
    }



    public void onRefresh() {
        locationAndContactsTask();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void locationAndContactsTask() {
        new GetContactTask(activity).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedItems = new ArrayList<>();
        setSelectingMode(false);
        locationAndContactsTask();
    }

    public boolean isSelectingMode() {
        return selectingMode;
    }

    public void setSelectingMode(boolean mode) {
        selectingMode = mode;
    }

    public void addToSelectedItems(Integer i) {
        Log.e("ADDED ", String.valueOf(i));
        selectedItems.add(i);
    }

    public void removeFromSelectedItems(Integer i) {
        Log.e("REMOVED ", String.valueOf(i));
        selectedItems.remove(i);
    }

    public boolean isSelected(Integer i) {
        return selectedItems.contains(i);
    }

    public void onBack() {
        if(isSelectingMode()) {
            selectedItems = new ArrayList<>();
            setSelectingMode(false);
            adapter.notifyDataSetChanged();
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
}