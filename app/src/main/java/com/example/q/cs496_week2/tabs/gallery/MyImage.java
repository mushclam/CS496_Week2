package com.example.q.cs496_week2.tabs.gallery;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.Date;

public class MyImage implements Parcelable, Comparable {
    String filePath;
    long dateTaken;
    float longitude;
    float latitude;

    public MyImage(String filePath, long dateTaken, float longitude, float latitude) {
        this.filePath = filePath;
        this.dateTaken = dateTaken;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    protected MyImage(Parcel in) {
        filePath = in.readString();
        dateTaken = in.readLong();
        longitude = in.readFloat();
        latitude = in.readFloat();
    }

    public static final Creator<MyImage> CREATOR = new Creator<MyImage>() {
        @Override
        public MyImage createFromParcel(Parcel in) {
            return new MyImage(in);
        }

        @Override
        public MyImage[] newArray(int size) {
            return new MyImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filePath);
        parcel.writeLong(dateTaken);
        parcel.writeFloat(longitude);
        parcel.writeFloat(latitude);
    }

    public String getFilePath() {
        return filePath;
    }

    public long getDate() {
        return dateTaken;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getFileName() {
        String[] temp = filePath.split("/");
        return temp[temp.length - 1];
    }

    public File getFile() {
        return new File(filePath);
    }

    public String getDirectory() {
        String directory = filePath.substring(0, filePath.lastIndexOf('/'));
        return directory;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Date myDate = new Date(this.getDate());
        Date compareDate = new Date(((MyImage) o).getDate());
        return myDate.compareTo(compareDate);
    }
}
