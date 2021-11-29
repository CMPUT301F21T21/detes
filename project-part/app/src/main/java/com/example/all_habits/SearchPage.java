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
import android.widget.Button;
import android.widget.EditText;
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
 * Searches for users based on search
 */

public class SearchPage extends AppCompatActivity {

    //initialize
    private EditText searchName;
    private ListView searchListView;
    private String searchedName;
    private ArrayList<String> requestsList = new ArrayList<String>();
    private ArrayList<User> userArrayList;
    private ArrayAdapter<User> userArrayAdapter;

    private Button followButton;
    private Button checkRequestButton;
    private Button searchButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    //searches for users and displays them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        userArrayList = new ArrayList<User>();
        searchName = findViewById(R.id.searchEditText);
        searchListView = findViewById(R.id.searchListView);
        checkRequestButton = findViewById(R.id.checkRequestsButton);
        searchButton = findViewById(R.id.searchButton);

        userArrayAdapter = new SearchList(this, userArrayList);
        searchListView.setAdapter(userArrayAdapter);

        //Searches for users with the same name
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersRef.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    userArrayList.clear();
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        searchedName = searchName.getText().toString();
                                        if(searchedName.equals(document.get("name")) && !document.getId().equals(uid)){
                                            userArrayList.add(document.toObject(User.class));
                                        }
                                    }
                                    userArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
        });

        //Creates a DialogBox when a name is clicked to ask if you want to follow that user.
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchPage.this);
                builder.setMessage("Do you want to follow this person?");
                builder.setCancelable(false);
                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestsList = new ArrayList<String>();
                            usersRef.document(userArrayList.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        requestsList = (ArrayList<String>) task.getResult().get("requestsList");
                                    }
                                    for(int i = 0; i < requestsList.size();i++){
                                        if(requestsList.get(i).equals(uid)){
                                            Toast.makeText(getApplicationContext(),"Request already sent",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    userArrayList.get(position).getRequestsList().add(uid);
                                    usersRef.document(userArrayList.get(position).getUid()).update("requestsList", userArrayList.get(position).getRequestsList());
                                    Toast.makeText(getApplicationContext(),"Follow Request Sent",Toast.LENGTH_SHORT).show();

                                }
                            });
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Dialog", "Closed");
                            }
                        }
                );
                AlertDialog searchDialog = builder.create();
                searchDialog.show();
                }
            });

        //Opens up the Following Request activity.
        checkRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPage.this,Requests.class);
                startActivity(intent);
            }
        });
    }



}