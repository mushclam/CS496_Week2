package com.example.q.cs496_app1.tabs.contact;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.example.q.cs496_app1.MainActivity;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ContactFragment extends Fragment {
    // Context mContext;
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
            try {
                File file = new File(activity.getFilesDir() + "/test.json");
                if (!file.exists()){
                    Toast.makeText(activity, activity.getFilesDir() + " + Not Exist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, activity.getFilesDir() + " + Exist", Toast.LENGTH_SHORT).show();
                    if(file.delete()) {
                        Toast.makeText(activity, activity.getFilesDir() + " + Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetContact extends AsyncTask<String, String, List<ContactItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ContactItem> doInBackground(String... Params) {
            List<ContactItem> itemList = new ArrayList<>();
            itemList = LoadJson();

            return itemList;
        }

        @Override
        protected void onPostExecute(List<ContactItem> result) {
            super.onPostExecute(result);
            items = new ArrayList<>();
            viewEmpty.setText("");

            if (result == null) {
                viewEmpty.setText(R.string.empty_contact);
            } else {
                Collections.sort(result, new ContactSorting());
                for (int i = 0; i < result.size(); i++) {
                    items.add(result.get(i));
                }
            }

            adapter = new ContactAdapter(activity, items, new RecyclerViewClickListener() {
                @Override
                public void onClicked(int position) {

                }

                @Override
                public void onLongClicked(int position) {

                }
            }, ContactFragment.this);
            recyclerView.setAdapter(adapter);
        }
    }

    // Load Json file and read content. convert json string to contact list.
    public List<ContactItem> LoadJson() {
        String json = null;
        List<ContactItem> itemList;
        Gson gson = new Gson();

        try {
            File file = new File(activity.getFilesDir() + "/test.json");
            if(!file.exists()) {
                FileOutputStream fos = activity.openFileOutput("test.json", Context.MODE_PRIVATE);
                fos.close();
            }
            StringBuilder data = new StringBuilder();
            FileInputStream fis = activity.openFileInput("test.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String str = br.readLine();
            while (str != null) {
                data.append(str).append("\n");
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
        FragmentTransaction ft = ((MainActivity) activity).getSupportFragmentManager().beginTransaction();
        ft.detach(ContactFragment.this).attach(ContactFragment.this).commit();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void locationAndContactsTask() {
        new GetContact().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
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