package com.example.q.cs496_week2.tabs.contact;

import java.io.Serializable;

public class Phone implements Serializable {
    private String phoneNumber;
    private String dataId;

    public Phone(String phoneNumber, String dataId) {
        this.phoneNumber = phoneNumber;
        this.dataId = dataId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
}
