package com.practice.coding.firestore_chat_app;


import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class ChatModel {
    private String msgId, message, userId, receiverId;
    private @ServerTimestamp Date timeStamp;
    private Map<String, String> chatMap;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, String> getChatMap() {
        return chatMap;
    }

    public void setChatMap(Map<String, String> chatMap) {
        this.chatMap = chatMap;
    }
}
