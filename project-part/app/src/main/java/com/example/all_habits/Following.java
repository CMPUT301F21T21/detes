package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    private ArrayList<String> following;

    ImageView backButton;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        followingList = findViewById(R.id.followingHabitList);

        backButton = findViewById(R.id.displayBackButton);

        userArrayList = new ArrayList<User>();
        following = new ArrayList<String>();
        userArrayAdapter = new SearchList(this, userArrayList);
        followingList.setAdapter(userArrayAdapter);

        //returns you back to previous page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Retrieves all of your following.
        usersRef.document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            following = (ArrayList<String>) task.getResult().get("following");
                        }

                        usersRef.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            userArrayList.clear();
                                            for(QueryDocumentSnapshot document : task.getResult()){
                                                if(following.contains(document.get("uid"))){
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

        //On a long press, asks to delete a follower.
        followingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Following.this);
                builder.setMessage("Unfollow?");
                builder.setCancelable(false);
                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        following = new ArrayList<String>();
                                        usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    following = (ArrayList<String>) task.getResult().get("following");

                                                }

                                                //Removes a follower.
                                                following.remove(userArrayList.get(position).getUid());
                                                usersRef.document(uid).update("following", following);
                                                Toast.makeText(getApplicationContext(),"Unfollowed",Toast.LENGTH_SHORT).show();
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
                            }
                        }
                );
                AlertDialog searchDialog = builder.create();
                searchDialog.show();
                return true;
            }
        });

    }
}