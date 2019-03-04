package com.practice.coding.firestore_chat_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<ChatModel> arrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ChatAdapter adapter;
    private String receiverId;
    private static String senderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //received Intent data
        if (getIntent() != null) {
            receiverId = getIntent().getStringExtra(Constants.RECEIVER_ID_KEY);
        }
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);

        adapter = new ChatAdapter(this, arrayList);

        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    public void sendMessage(View view) {
        if (etMessage.getText().toString().isEmpty()) {
            etMessage.setError("Type Message...");
            return;
        }

        if (senderId.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "Please Log In First...", Toast.LENGTH_SHORT).show();
        }
        ChatModel model = new ChatModel();
        String message = etMessage.getText().toString().replace("\n", "").trim();


        if (!receiverId.isEmpty()) {
            model.setReceiverId(receiverId);
        } else {
            Toast.makeText(this, "Receiver id not registered...", Toast.LENGTH_SHORT).show();
            return;
        }

        model.setUserId(senderId);
        model.setMessage(message);

        Map<String, String> map = new HashMap<>();
        map.put(Keys.MESSAGE_KEY, message);
        map.put(Keys.SENDER_ID, senderId);

        // model.setChatMap(map);

        FirestoreKeys.chatReference.add(model);
        etMessage.setText(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreKeys.chatReference.
                orderBy(Keys.TIME_STAMP, Query.Direction.ASCENDING).
                addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ChatModel chatModel = documentSnapshot.toObject(ChatModel.class);
                            if ((senderId.equals(chatModel.getUserId()) && receiverId.equals(chatModel.getReceiverId())) || (receiverId.equals(chatModel.getUserId()) && senderId.equals(chatModel.getReceiverId()))) {
                                chatModel.setReceiverId("");
                                arrayList.add(chatModel);
                            }
                        }

                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        recyclerView.scrollToPosition(arrayList.size() - 1);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
