package com.example.q.cs496_app1;

import android.util.JsonReader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

public class JsonProcessing {
    ContactItem item;
    Gson gson = new Gson();

    public JsonProcessing(ContactItem item) {
        this.item = item;
    }

//    public void ObjectToJson (String json) {
//        String fileName = "test.json";
//        try {
//            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
//
//            fw.write(json);
//            fw.flush();
//            fw.close();
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    public ContactItem JsonToObject() {
        String json = gson.toJson(item);
        ContactItem extItem = gson.fromJson(json, ContactItem.class);
        return extItem;
    }

    public String JsonToString() {
        String json = gson.toJson(item);
        return json;
    }
}
