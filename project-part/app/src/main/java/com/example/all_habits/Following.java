package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

/**
 * Shows you a list of who you are following
 */

public class Following extends AppCompatActivity {

    //initialize
    private ListView followingList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");
    private ArrayList<User> userArrayList;
    private ArrayAdapter<User> userArrayAdapter;
    private ArrayList<String> followers;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        followingList = findViewById(R.id.followingHabitList);

        userArrayList = new ArrayList<User>();
        followers = new ArrayList<String>();
        userArrayAdapter = new SearchList(this, userArrayList);
        followingList.setAdapter(userArrayAdapter);

        //Retrieves all of your followers.
        usersRef.document().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            followers = (ArrayList<String>) task.getResult().get("followers");
                        }

                        usersRef.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            userArrayList.clear();
                                            for(QueryDocumentSnapshot document : task.getResult()){
                                                followers = (ArrayList<String>) document.get("followers");
                                                if(followers.contains(uid)){
                                                    userArrayList.add(document.toObject(User.class));
                                                }
                                            }
                                            userArrayAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
                });

        //takes you to follow their habits
        followingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(Following.this, FollowingHabits.class);
                intent.putExtra("followingId", userArrayList.get(position).getUid());
                startActivity(intent);
            }
        });
    }
}