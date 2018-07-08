package com.example.q.cs496_week2.tabs.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetContactTask extends AsyncTask<String, String, ArrayList<ContactTestItem>> {

    private Context mContext;
    private ContactAdapter adapter;
    private Fragment fragment;
    private RecyclerView recyclerView;

    private ContactList contactList;
    public ArrayList<ContactTestItem> contactTestItemList = new ArrayList<>();

    private AlertDialog pDialog;

    public GetContactTask(Context mContext, ContactAdapter adapter, Fragment fragment, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.adapter = adapter;
        this.fragment = fragment;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new AlertDialog.Builder(mContext)
                .setMessage("Fetching Contacts...")
                .setCancelable(false)
                .create();
    }

    @Override
    protected ArrayList<ContactTestItem> doInBackground(String... Params) {
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                ArrayList<Phone> phone_numbers = new ArrayList<>();
                ArrayList<Email> emails = new ArrayList<>();
                String note = "";
                String starred = "";

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String data_id = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.Data._ID));
                        phone_numbers.add(new Phone(phone, data_id));
                    }
                    pCur.close();


                    // get email and type
                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        String data_id = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.Data._ID)
                        );
                        emails.add(new Email(email, emailType, data_id));
                    }
                    emailCur.close();

                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    }
                    noteCur.close();

                    // get starred info
                    Cursor starCur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " =?", new String[]{id}, null);
                    if (starCur.moveToFirst()) {
                        starred = starCur.getString(starCur.getColumnIndex(ContactsContract.Contacts.STARRED));
                    }

                    ContactTestItem contactTestItem = new ContactTestItem(
                            id,
                            null,
                            name,
                            phone_numbers,
                            emails,
                            note,
                            starred
                    );
                    contactTestItemList.add(contactTestItem);

                }
            }
        }
        Gson gson = new Gson();
        String result = gson.toJson(contactTestItemList);
        Log.d("RESULT", result);

        return contactTestItemList;
    }

    @Override
    protected void onPostExecute(ArrayList<ContactTestItem> result) {
        super.onPostExecute(result);

//        contactList.sorting(false);
//        contactList.sorting(true);

        if(result != null) {
            Collections.sort(result, new ContactTestSorting());
        }

        if (pDialog.isShowing()) { pDialog.dismiss(); }

        adapter = new ContactAdapter(mContext, result, new RecyclerViewClickListener() {
            @Override
            public void onClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        }, (ContactFragment)fragment);
        recyclerView.setAdapter(adapter);
    }
}
