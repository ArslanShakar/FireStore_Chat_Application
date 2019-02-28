package com.practice.coding.firestore_practice;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class DataModel {
    private String userId, userName, userPassword;
    private int priority;
    private @ServerTimestamp Date timeStamp;
    private List<String> listTags;
    private Map<String, String> mapKeyValue;

    public DataModel() {
    }

    public DataModel(String userId, String userName, String userPassword, int priority) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.priority = priority;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getListTags() {
        return listTags;
    }

    public void setListTags(List<String> listTags) {
        this.listTags = listTags;
    }

    public Map<String, String> getMapKeyValue() {
        return mapKeyValue;
    }

    public void setMapKeyValue(Map<String, String> mapKeyValue) {
        this.mapKeyValue = mapKeyValue;
    }
}
