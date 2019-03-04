package com.practice.coding.firestore_chat_app;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class UserModel {
    private String userId, userEmail, userPassword;
    private int priority;
    private @ServerTimestamp Date timeStamp;
    private List<String> listTags;
    private Map<String, String> mapKeyValue;

    public UserModel() {
    }

    public UserModel(String userId, String userEmail, String userPassword, int priority) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.priority = priority;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
