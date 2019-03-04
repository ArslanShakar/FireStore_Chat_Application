package com.practice.coding.firestore_chat_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

public class AllChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ChatModel> arrayList = new ArrayList<>();
    private static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        recyclerView = findViewById(R.id.rcvAllChats);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ArrayList<String> arrayListCompareReceiverIds = new ArrayList<>();
        final ArrayList<String> arrayListCompareSenderIds = new ArrayList<>();
        arrayList.clear();
        FirestoreKeys.chatReference.orderBy(Keys.TIME_STAMP, Query.Direction.DESCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ChatModel chatModel = document.toObject(ChatModel.class);
                    if (userId.equals(chatModel.getUserId()) || (userId.equals(chatModel.getReceiverId()))) {
                        if (arrayList.isEmpty()) {
                            arrayList.add(chatModel);
                            arrayListCompareReceiverIds.add(chatModel.getReceiverId());
                            arrayListCompareSenderIds.add(chatModel.getUserId());
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (!arrayListCompareReceiverIds.contains(chatModel.getReceiverId()) && !arrayListCompareSenderIds.contains(chatModel.getReceiverId())) {
                                    arrayList.add(chatModel);
                                    arrayListCompareReceiverIds.add(chatModel.getReceiverId());
                                    arrayListCompareSenderIds.add(chatModel.getUserId());
                                }
                            }
                        }
                    }
                }
                AllChatsAdapter adapter = new AllChatsAdapter(AllChatsActivity.this, arrayList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllChatsActivity.this));
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void fabStartChatOnClick(View view) {

        // Start listing users from the beginning, 1000 at a time.
        final ArrayList<String> arrayListUsers = new ArrayList<>();

        FirestoreKeys.userReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        if (!documentSnapshot.getId().equals(userId)) {
                            arrayListUsers.add(documentSnapshot.getId());
                        }
                    }
                    if (!arrayListUsers.isEmpty()) {
                        final String[] users = arrayListUsers.toArray(new String[arrayListUsers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AllChatsActivity.this);
                        builder.setTitle("Select One");
                        builder.setItems(users, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String chatWithUserId = Arrays.asList(users).get(which);
                                Intent intent = new Intent(AllChatsActivity.this, ChatActivity.class);
                                intent.putExtra(Constants.SENDER_ID_KEY, userId);
                                intent.putExtra(Constants.RECEIVER_ID_KEY, chatWithUserId);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                } else {
                    Toast.makeText(AllChatsActivity.this, "Some thing went wrong..Record fetching failed...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mLogout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
