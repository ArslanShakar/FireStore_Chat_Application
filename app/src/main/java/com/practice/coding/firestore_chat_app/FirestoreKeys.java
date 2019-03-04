package com.practice.coding.firestore_chat_app;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreKeys {

    public static CollectionReference userReference = FirebaseFirestore.getInstance().collection("Users");

    private static final String CHAT_KEY = "Chat";
    private static final String USERS_CHAT = "Users_Chat";
    private static final String RECEIVERS_IDS = "Receivers";
    public static final String MESSAGES_KEY = "Messages";


    public static final CollectionReference chatReference = FirebaseFirestore.getInstance().collection(CHAT_KEY);

    public static CollectionReference getAllChats(String senderId)
    {
        return FirebaseFirestore.getInstance().collection(CHAT_KEY).document(senderId).collection(MESSAGES_KEY);
    }

    public static CollectionReference getChatReference(String senderId, String receiverId) {

        /*
        * Chat > Sender_Id > Receiver_Id > Chat_Id > chatting details

        Chat > A vs B > message...

        Chat > A vs C > message...

        Chat > A vs D > message...

        Chat > B vs A > message...

        Chat > B vs C > message...

        * */

        return FirebaseFirestore.getInstance().collection(CHAT_KEY).document(senderId).collection(RECEIVERS_IDS);
    }
}
