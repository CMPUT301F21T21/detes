package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Allows you to see the public habits of a specific user you follow
 */

public class FollowingHabits extends AppCompatActivity {

    //initialize
    private ListView followingHabitList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionRef;
    private ArrayList<Habit> habitArrayList;
    private ArrayAdapter<Habit> habitArrayAdapter;
    private ArrayList<String> requestsListArray;
    private ArrayList<String> followers;
    private String followingUID;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    //getting user and a list of their public habits and their progress
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_habits);
        followingHabitList = findViewById(R.id.followingHabitList);
        Intent retrieval = getIntent();
        followingUID = retrieval.getStringExtra("followingId");
        collectionRef = db.collection(followingUID);
        habitArrayList = new ArrayList<Habit>();
        habitArrayAdapter = new FollowingHabitList(this,habitArrayList);
        followingHabitList.setAdapter(habitArrayAdapter);
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        if((Boolean)document.get("Private") == false) {
                            habitArrayList.add(document.toObject(Habit.class));
                        }
                    }

                    habitArrayAdapter.notifyDataSetChanged();
                }
            }
        });


    }
}