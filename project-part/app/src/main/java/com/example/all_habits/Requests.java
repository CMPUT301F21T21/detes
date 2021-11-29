package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Requests extends AppCompatActivity {

    private ListView requestsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");
    private ArrayList<User> userArrayList;
    private ArrayAdapter<User> userArrayAdapter;
    private ArrayList<String> requestsListArray;
    private ArrayList<String> followers;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        requestsList = findViewById(R.id.requestsList);

        userArrayList = new ArrayList<User>();
        followers = new ArrayList<String>();
        userArrayAdapter = new SearchList(this, userArrayList);
        requestsList.setAdapter(userArrayAdapter);

        //Gets all the users that requested to follow you.
        usersRef.document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            requestsListArray = (ArrayList<String>) task.getResult().get("requestsList");
                        }
                        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(QueryDocumentSnapshot document  : task.getResult()){
                                    if(requestsListArray.contains(document.getId())){
                                        userArrayList.add(document.toObject(User.class));
                                    }
                                }
                                userArrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

        //Opens up a DialogBox to ask if you want to Deny or Accept a Follower Request.
        requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Requests.this);
                builder.setMessage("Accept Follow Request?");
                builder.setCancelable(false);
                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestsListArray = new ArrayList<String>();
                                        usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    requestsListArray = (ArrayList<String>) task.getResult().get("requestsList");
                                                    followers = (ArrayList<String>) task.getResult().get("followers");

                                                }

                                                //Adds a follower and removes a follower request.
                                                followers.add(userArrayList.get(position).getUid());
                                                requestsListArray.remove(userArrayList.get(position).getUid());
                                                usersRef.document(uid).update("requestsList", requestsListArray);
                                                usersRef.document(uid).update("followers", followers);
                                                Toast.makeText(getApplicationContext(),"Accepted Follower Request",Toast.LENGTH_SHORT).show();
                                                userArrayList.remove(position);
                                                userArrayAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Removes a follow request.
                                requestsListArray = new ArrayList<String>();
                                requestsListArray.remove(userArrayList.get(position).getUid());
                                usersRef.document(uid).update("requestsList", requestsListArray);
                                Toast.makeText(getApplicationContext(),"Denied Follower Requests",Toast.LENGTH_SHORT).show();
                                userArrayList.remove(position);
                                userArrayAdapter.notifyDataSetChanged();
                            }
                        }
                );
                AlertDialog searchDialog = builder.create();
                searchDialog.show();
            }
        });
    }
}