package com.practice.coding.firestore_practice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etName, etPassword, etPriority;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String user_id = "";
    private DocumentSnapshot resultDocumentSnapshot;


    private CollectionReference collectionReference = db.collection("Users");
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> arrayListPagination = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        listView = findViewById(R.id.listView);
        etPriority = findViewById(R.id.etPriority);

        listView.setOnItemClickListener(this);
    }

    public void insert(View view) {

        DocumentReference documentReference = collectionReference.document();

        String name = etName.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() || password.isEmpty() || etPriority.getText().toString().isEmpty()) {
            msg("Field empty...");
            return;
        }


        DataModel dataModel = new DataModel();
        dataModel.setUserName(name);
        dataModel.setUserPassword(password);

        dataModel.setPriority(Integer.parseInt(etPriority.getText().toString()));

        String uid = documentReference.getId();
        dataModel.setUserId(uid);

        String tags = "tag1, tag2,tag3";
        String[] splittedArray = tags.split("\\s*,\\s*"); // \\s* removes the extras white spaces before and after the comma ,
        // OR simple we can do this like that
        //String[] split = tags.split(",");
        dataModel.setListTags(Arrays.asList(splittedArray));

        Map<String, String> map = new HashMap<>();
        map.put("Key1", "value1");
        map.put("Key2", "value2");
        map.put("Key3", "value3");
        //    map.put("Key4", "value4");

        dataModel.setMapKeyValue(map);

        documentReference.set(dataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    msg("User Registered!");
                } else {
                    msg("Registration Failed...");
                }
            }
        });
    }

    public void update(View view) {
        if (!user_id.equals("")) {
            collectionReference.document(user_id).update(Keys.PASSWORD, etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        msg("Updated");
                    } else {
                        msg("Failed!");
                    }
                }
            });
        } else {
            msg("Please select user from list");
        }
    }

    public void delete(View view) {
        if (!user_id.equals("")) {
            collectionReference.document(user_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    msg("Deleted");
                    etName.setText(null);
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
        /*collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    msg("Error while loading data...");
                    return;
                }

                fetchCollectionData(queryDocumentSnapshots);
            }
        });*/

        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = documentChange.getDocument();

                    String id = documentSnapshot.getId();

                    int oldIndex = documentChange.getOldIndex();
                    int newIndex = documentChange.getNewIndex();

                    ArrayList<String> arrayList = new ArrayList<>();
                    switch (documentChange.getType()) {
                        case ADDED:
                            arrayList.add("Added : " + id + "\nOld Index : " + oldIndex + "\nNew Index : " + newIndex);
                            break;
                        case REMOVED:
                            arrayList.add("Removed : " + id + "\nOld Index : " + oldIndex + "\nNew Index : " + newIndex);
                            break;

                        case MODIFIED:
                            arrayList.add("Modified : " + id + "\nOld Index : " + oldIndex + "\nNew Index : " + newIndex);
                            break;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void msg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void loadData(View view) {
        //collectionReference.whereEqualTo(Keys.ID, null).limit(2).get()
        //collectionReference.whereEqualTo(Keys.NAME, "abc").limit(2).get()
        //collectionReference.orderBy(Keys.NAME).limit(2).get() //by default ascending order
        //collectionReference.orderBy(Keys.NAME, Query.Direction.DESCENDING).get()
        //Compound query
        collectionReference.whereEqualTo(Keys.ID, null).orderBy(Keys.NAME, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        fetchCollectionData(queryDocumentSnapshots);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg(e.getMessage());
                Log.d("TAG", e.getMessage());
            }
        });

        /*//How merge multiple tasks
        Task task1 = collectionReference.whereLessThan(Keys.PRIORITY, 2).orderBy(Keys.PRIORITY).get();
        Task task2 = collectionReference.whereGreaterThan(Keys.PRIORITY, 2).orderBy(Keys.PRIORITY).get();
        arrayList.clear();
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        DataModel dataModel = queryDocumentSnapshot.toObject(DataModel.class);

                        String uid = queryDocumentSnapshot.getId();
                        String name = dataModel.getUserName();

                        arrayList.add(name + " " + uid);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });*/
    }

    private void fetchCollectionData(QuerySnapshot querySnapshots) {
        arrayList.clear();
        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshots) {
            DataModel model = queryDocumentSnapshot.toObject(DataModel.class);

            model.setUserId(queryDocumentSnapshot.getId());
            String uid = model.getUserId();
            String name = model.getUserName();
            String password = model.getUserPassword();

            List<String> listTags = model.getListTags();
            String tags = "";
            for (String data : listTags) {
                tags += data + "\n";
            }
            Map<String, String> map = model.getMapKeyValue();
            String data = "";
            for (String key : model.getMapKeyValue().keySet()) {
                data += key + "\n";
            }

            arrayList.add(name + " " + uid);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String data = (String) listView.getItemAtPosition(position);
        String[] array = data.split(" ", 2);

        user_id = array[1];

        etName.setText(array[0]);
    }

    public void paginationOnClick(View view) {
        Query query;
        if (resultDocumentSnapshot == null) {
            query = collectionReference.orderBy(Keys.PRIORITY).limit(3);
        } else {
            query = collectionReference.orderBy(Keys.PRIORITY).startAfter(resultDocumentSnapshot).limit(3);
        }
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DataModel dataModel = documentSnapshot.toObject(DataModel.class);

                            String uid = documentSnapshot.getId();
                            String name = dataModel.getUserName();

                            /*List<String> listTags = dataModel.getListTags();
                            String tags = "";
                            for(String data : listTags)
                            {
                                tags += data+"\n";
                            }
*/
                            Map<String, String> map = dataModel.getMapKeyValue();
                            String data = "";
                            for (String key : dataModel.getMapKeyValue().keySet()) {
                                data += key + "\n";
                            }
                            arrayListPagination.add(name + " " + uid);
                        }

                        if (queryDocumentSnapshots.size() > 0) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayListPagination);
                            listView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();

                            resultDocumentSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                });

        /*
        * //.startAt(10) //start fetching record from 10
                .startAt(resultDocumentSnapshot)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DataModel dataModel = documentSnapshot.toObject(DataModel.class);

                            String uid = documentSnapshot.getId();
                            String name = dataModel.getUserName();

                            arrayListPagination.add(name + " " + uid);
                        }

                        if (queryDocumentSnapshots.size() > 0) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayListPagination);
                            listView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
                        }


                    }
                });
        * */
    }

    public void batchWrite(View view) {
        WriteBatch batch = db.batch();

        DocumentReference doc = collectionReference.document("New User");
        batch.set(doc, new DataModel(doc.getId(), "New Name", "123", 4));

        DocumentReference doc2 = collectionReference.document("sHNWOSP8D4buVSUPJDTQ");
        batch.update(doc2, Keys.NAME, "updated Name", Keys.PRIORITY, "121");


        /*DocumentReference doc3 = collectionReference.document("dF2qvR095E6ohbs3uMXg");
        batch.delete(doc3);*/

        DocumentReference doc4 = collectionReference.document();
        batch.set(doc4, new DataModel(doc4.getId(), "Other User", "123", 1));

        batch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        msg("Failed! Batch write \n" + e.getMessage());
                    }
                });
    }

    public void transactionOnClick(View view) {
        db.runTransaction(new Transaction.Function<Long>() {
            @Nullable
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference reference = collectionReference.document("New User");
                DocumentSnapshot snapshot = transaction.get(reference);

                long newPriority = snapshot.getLong(Keys.PRIORITY) + 1;

                transaction.update(reference, Keys.PRIORITY, newPriority);

                return newPriority;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                msg(result + "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msg(e.getMessage());
            }
        });

    }

    public void updateArray(View view) {
        //update array
        collectionReference.document("bFLeUdLEHYRvGWFswFlr").update(Keys.ARRAY, FieldValue.arrayUnion("new tag"));

        /*//delete array
        collectionReference.document("bFLeUdLEHYRvGWFswFlr").update(Keys.ARRAY, FieldValue.arrayRemove("new tag"));
        //delete entire array from that specific record
        collectionReference.document("bFLeUdLEHYRvGWFswFlr").delete();*/
    }

    public void searchArray(View view) {
        collectionReference.whereArrayContains(Keys.ARRAY, "new tag").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    fetchCollectionData(task.getResult());
                }
            }
        });
    }

    public void updateMap(View view) {
        //update map nested value (update nested object)
        collectionReference.document("bFLeUdLEHYRvGWFswFlr").update(Keys.MAP_KEY + ".Key4", "value44444");

        /*//more nested object just add dot . and then nested object key
        collectionReference.document("bFLeUdLEHYRvGWFswFlr").update(Keys.MAP_KEY + ".Key4.nested4", "value44444");*/

       /* //delete nested key from map
        collectionReference.document("bFLeUdLEHYRvGWFswFlr").update(Keys.MAP_KEY +".Key2", FieldValue.delete());*/

        /*//delete entire map object from the record
        collectionReference.document("bFLeUdLEHYRvGWFswFlr").delete();*/

    }

    public void searchMap(View view) {
        collectionReference.whereEqualTo(Keys.MAP_KEY + ".Key1", "value1").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                fetchCollectionData(queryDocumentSnapshots);
            }
        });
    }

    public void subCollection(View view) {
        Map<String, String> mapData = new HashMap<>();
        mapData.put("name", "Arslan Shakar");
        mapData.put("age", "22-years");
        
        collectionReference.document("New User").collection("Sub_Collection").add(mapData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    msg("Sub Collection data insertion successful");
                } else {
                    msg("Failed to insert data in sub collection");
                }
            }
        });
    }
}
