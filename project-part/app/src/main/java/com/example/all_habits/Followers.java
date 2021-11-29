package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
 * Shows a list of your followers and allows you to delete them
 */

public class Followers extends AppCompatActivity {

    //initialize
    private ListView followersList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");
    private ArrayList<User> userArrayList;
    private ArrayAdapter<User> userArrayAdapter;
    private ArrayList<String> followers;

    ImageView backButton;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        followersList = findViewById(R.id.followingHabitList);

        backButton = findViewById(R.id.displayBackButton);

        userArrayList = new ArrayList<User>();
        followers = new ArrayList<String>();
        userArrayAdapter = new SearchList(this, userArrayList);
        followersList.setAdapter(userArrayAdapter);

        //returns you back to previous page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Retrieves all of your followers.
        usersRef.document(uid).get()
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
                                                if(followers.contains(document.get("uid"))){
                                                    userArrayList.add(document.toObject(User.class));
                                                }
                                            }
                                            userArrayAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
                });

        //On a long press, asks to delete a follower.
        followersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Followers.this);
                builder.setMessage("Delete Follower?");
                builder.setCancelable(false);
                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        followers = new ArrayList<String>();
                                        usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    followers = (ArrayList<String>) task.getResult().get("followers");

                                                }

                                                //Removes a follower.
                                                followers.remove(userArrayList.get(position).getUid());
                                                usersRef.document(uid).update("followers", followers);
                                                Toast.makeText(getApplicationContext(),"Removed Follower",Toast.LENGTH_SHORT).show();
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