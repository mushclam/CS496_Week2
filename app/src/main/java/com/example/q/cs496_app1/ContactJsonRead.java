package com.example.q.cs496_app1;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class ContactJsonRead {
    String json = "[{\"image\":2131165284,\"imageTitle\":\"foreground\"},{\"image\":2131165283,\"imageTitle\":\"background\"}]";
    List<ContactItem> itemList;

    public List<ContactItem> ReadContact() {
        Gson gson = new Gson();
        itemList = gson.fromJson(json, new TypeToken<List<ContactItem>>(){}.getType());
        return itemList;
    }

    public int getSize() {
        return itemList.size();
    }
}
