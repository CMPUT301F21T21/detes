package com.example.all_habits;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HabitEvents extends AppCompatActivity {

    private TextView commentEditText;
    private Button saveCommentButton;

    private int habitNum;
    private String habitId;
    private String commentString;
    private ArrayAdapter<Comment> commentAdapter;

    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private CollectionReference habitReference; // collection of selected habit
    private CollectionReference userCollectionReference; // collection of signed in user
    private DocumentReference documentRef;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_events);

        Intent intent = getIntent();
        habitNum = intent.getIntExtra("habitNum", 1); // gets the habit that was clicked on

        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
        //userCollectionReference = db.collection(currentFireBaseUser.getUid());

        commentEditText = findViewById(R.id.commentEditText);
        saveCommentButton = findViewById(R.id.saveCommentButton);

        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                habitId = document.getId();
                                documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                                commentString = document.getString("comment");

                                // if the comment string is not empty
                                if (!commentString.equals("")) {
                                    commentEditText.setText(document.getString("comment"));
                                }


                            }
                        }

        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (commentEditText.getText().toString().length() > 20){
                    Toast.makeText(getApplicationContext(), "The comment has to be under 20 characters long.", Toast.LENGTH_SHORT).show();
                    return;
                }

                documentRef.update("comment", commentEditText.getText().toString());
                Toast.makeText(getApplicationContext(), "Your comment has been saved", Toast.LENGTH_SHORT).show();


              }});
            }
        });

    }
}
