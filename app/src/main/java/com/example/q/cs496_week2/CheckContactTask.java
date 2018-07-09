package com.example.q.cs496_week2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.q.cs496_week2.tabs.contact.ContactTestItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CheckContactTask extends AsyncTask<String, String, String>{

    private Context mContext;
    private Fragment mFragment;

    public CheckContactTask(Context mContext, Fragment mFragment) {
        this.mContext = mContext;
        this.mFragment = mFragment;
    }

    @Override
    protected String doInBackground(String... strings) {
        List<ContactTestItem> item = LoadJson();
        Iterator<ContactTestItem> itemIterator = item.iterator();

        String[] mProjection =
                {
                        ContactsContract.RawContacts._ID,
                        ContactsContract.RawContacts.VERSION
                };

        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.RawContacts.CONTENT_URI,
                mProjection, null, null, null);

        if (cur == null) {
            Log.e("CHECK_CONTACTS", "Null Cursor");
        } else if (cur.getCount() > 0) {
            while (cur.moveToNext() && itemIterator.hasNext()) {
                String contactId = cur.getString(cur.getColumnIndex(ContactsContract.RawContacts._ID));
                String contactVersion = cur.getString(cur.getColumnIndex(ContactsContract.RawContacts.VERSION));
//                String jsonVersion = itemIterator.next().getVersion();
//                if (!contactVersion.equals(jsonVersion)){
//                    Log.d("UPDATE", contactId + " : is updated : " + contactVersion);
//                }
            }
        }

        return null;
    }

    public List<ContactTestItem> LoadJson() {
        String json = null;
        List<ContactTestItem> itemList;
        Gson gson = new Gson();

        try {
            File file = new File(mContext.getFilesDir() + "/test.json");
            if(!file.exists()) {
                FileOutputStream fos = mContext.openFileOutput("test.json", Context.MODE_PRIVATE);
                fos.close();
            }
            StringBuilder data = new StringBuilder();
            FileInputStream fis = mContext.openFileInput("test.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String str = br.readLine();
            while (str != null) {
                data.append(str).append("\n");
                str = br.readLine();
            }

            itemList = gson.fromJson(data.toString(), new TypeToken<List<ContactTestItem>>(){}.getType());
            return itemList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isJson() {
        File file = new File(mContext.getFilesDir() + "/test.json");
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
