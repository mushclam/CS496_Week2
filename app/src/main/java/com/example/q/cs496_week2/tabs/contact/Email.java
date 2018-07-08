package com.example.q.cs496_week2.tabs.contact;

import java.io.Serializable;

public class Email implements Serializable {
    private String emailAddress;
    private String emailType;
    private String dataId;

    public Email(String emailAddress, String emailType, String dataId) {
        this.emailAddress = emailAddress;
        this.emailType = emailType;
        this.dataId = dataId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
}
