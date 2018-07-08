package com.example.q.cs496_week2.tabs.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;

import com.example.q.cs496_week2.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetContactTask extends AsyncTask<String, String, ContactList> {

    private Context mContext;
    private ContactList contactList;

    private AlertDialog pDialog;

    public GetContactTask(Context mContext) {
        this.mContext = mContext;
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
    protected ContactList doInBackground(String... Params) {
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                ArrayList<Pair<String, String>> phone_numbers = new ArrayList<>();
                ArrayList<Triplet<String, String, String>> emails = new ArrayList<>();
                String note = "";
                String starred = "";

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Log.d("HAS_PHONE_NUMBER","name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String data_id = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.Data._ID));
                        Log.d("PHONE", phone);
                        phone_numbers.add(new Pair<String, String>(phone, data_id));
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

                        Log.d("EMAIL","Email " + email + " Email Type : " + emailType + " data_id : " + data_id);

                        emails.add(new Triplet<String, String, String>(email, emailType, data_id));
                    }
                    emailCur.close();

                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        Log.d("Note ", note);
                    }
                    noteCur.close();

                    // get starred info
                    Cursor starCur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " =?", new String[]{id}, null);
                    if (starCur.moveToFirst()) {
                        starred = starCur.getString(starCur.getColumnIndex(ContactsContract.Contacts.STARRED));
                        Log.d("Starred ", starred);
                    }

//                    try {
//                        contactList.addContact(id, name, phone_numbers, emails, note, starred);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        return contactList;
    }

    @Override
    protected void onPostExecute(ContactList result) {
        super.onPostExecute(result);

//        contactList.sorting(false);
//        contactList.sorting(true);

        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
