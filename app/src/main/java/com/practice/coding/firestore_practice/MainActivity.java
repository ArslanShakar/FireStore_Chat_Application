package com.practice.coding.firestore_practice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etEmail, etPassword;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String user_id = "";

    //private DocumentReference myRef = db.collection("Users").document();

    private CollectionReference collectionReference = db.collection("Users");
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(this);
    }

    public void insert(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();


        if (email.isEmpty() || password.isEmpty()) {
            msg("Field empty...");
            return;
        }

       /* String id = myRef.getId();
        DataModel dataModel = new DataModel(id, email, password);
        DataModel dataModel = new DataModel(id, email, password);
        //db.collection("Users").document("User_1").set(dataModel).addOnSuccessListener(new OnSuccessListener<Void>() {...}
        myRef.set(dataModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                msg("User Registered!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg("Registration Failed...");
            }
        });*/

        DataModel dataModel = new DataModel();
        dataModel.setUserEmail(email);
        dataModel.setUserPassword(password);

        String id = collectionReference.document().getId();
        dataModel.setUserId(id);

        collectionReference.add(dataModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    msg("User Registered!");
                } else {
                    msg("Registration Failed...");
                }
            }
        });
    }

    public void update(View view) {
        if(!user_id.equals(""))
        {
            collectionReference.document(user_id).update(Keys.PASSWORD, etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        msg("Updated");
                    }else
                    {
                        msg("Failed!");
                    }
                }
            });
        }else {
            msg("Please select user from list");
        }
    }

    public void delete(View view) {

        //delete single field
        /*myRef.update(Keys.PASSWORD, FieldValue.delete()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                msg("Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg("Deletion Failed!");
            }
        });*/

       /* myRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                msg("Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg("Deletion Failed!");
            }
        });*/

        if (!user_id.equals("")) {
            collectionReference.document(user_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    msg("Deleted");
                    etEmail.setText(null);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    msg("Deletion Failed!");
                }
            });
        } else {
            msg("Please select user from list");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
       /* myRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    msg("Error while loading data...");
                    return;
                }
                setData(documentSnapshot);
            }
        });*/

        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    msg("Error while loading data...");
                    return;
                }

                setCollectionData(queryDocumentSnapshots);
            }
        });
    }

    public void msg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void fetchData(View view) {
      /*  myRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setData(documentSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg(e.getMessage());
            }
        });*/

        //collectionReference.whereEqualTo(Keys.ID, null).limit(2).get()
        //collectionReference.whereEqualTo(Keys.EMAIL, "abc").limit(2).get()
        //collectionReference.orderBy(Keys.EMAIL).limit(2).get() //by default ascending order
        //collectionReference.orderBy(Keys.EMAIL, Query.Direction.DESCENDING).get()
        //Compound query
        collectionReference.whereEqualTo(Keys.ID, null).orderBy(Keys.EMAIL).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        setCollectionData(queryDocumentSnapshots);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg(e.getMessage());
                Log.d("TAG", e.getMessage());
            }
        });
    }

    private void setData(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            arrayList.clear();
            DataModel model = documentSnapshot.toObject(DataModel.class);

            String email = model.getUserEmail();
            String password = model.getUserPassword();
            arrayList.add(email + " : " + password);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            msg("Document does not exists!");
            etEmail.setText("");
        }
    }

    private void setCollectionData(QuerySnapshot querySnapshots) {
        arrayList.clear();
        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshots) {
            DataModel model = queryDocumentSnapshot.toObject(DataModel.class);

            String uid = queryDocumentSnapshot.getId();
            String email = model.getUserEmail();
            String password = model.getUserPassword();
            arrayList.add(uid);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String data = (String) listView.getItemAtPosition(position);
        user_id = data;
        etEmail.setText(data);
    }
}
