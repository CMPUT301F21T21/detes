package com.example.all_habits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Custom ArrayList for comments.
 */
public class Comments extends AppCompatActivity implements addCommentFragment.OnFragmentInteractionListener{

    private ArrayList<Comment> commentArrayList;
    private Button backButton;
    private ImageView user;
    private int habitNum;
    private TextView habitNumText;

    Button deleteComment;
    Button addButton;
    ListView commentListView;
    ArrayAdapter<Comment> commentAdapter;
    Comment testComment;

    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        Intent intent = getIntent();


        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //final CollectionReference habitReference = db.collection(currentFireBaseUser.getUid());
        final CollectionReference commentReference = db.collection("comments");

        int commentNum = 0;

        commentListView = findViewById(R.id.comments_list);
        commentArrayList = new ArrayList<>();

        commentAdapter = new CommentsList(this, commentArrayList);

        commentListView.setAdapter(commentAdapter);

        //This does not work properly when data is input.
        /*if(commentArrayList.size() == 0) {
            setContentView(R.layout.empty_comment);
        }
         */

        Intent commentIntent = getIntent();
        habitNum = commentIntent.getIntExtra("habitNum", 1);

        // sets what the habit number is at the top of the comments page
        habitNumText = findViewById(R.id.selectedHabit);
        habitNumText.setText("Habit " + habitNum);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish(); // goes back to the all habits page
            }
        });


        user = findViewById(R.id.User);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Comments.this, DisplayUserProfile.class);
                startActivity(intent);
            }
        });


        addButton = findViewById(R.id.addComment);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new addCommentFragment().show(getSupportFragmentManager(), "ADD_COMMENT");
            }
        });


        commentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Query findComment = db.collection("comments").whereEqualTo("commentNum", position + 1).limit(1);
                findComment.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            String commentId = new String();
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(QueryDocumentSnapshot document:task.getResult()){
                                        commentId = document.getId();
                                        commentReference.document(commentId).delete();
                                    }
                            }
                        });
                return false;
            }
        });

        // getting data from firebase to your local device (snapshot of database)
        commentReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                commentArrayList.clear();
                for(QueryDocumentSnapshot comments: queryDocumentSnapshots)
                {
                    Comment comment = comments.toObject(Comment.class);
                        commentArrayList.add(comment);
                }
                commentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
                //from the cloud
            }
        });


    }

    /**
     * Adds the comment into the collection
     * @param comment
     */
    @Override
    public void onOkPressed(Comment comment){
        CollectionReference collectionReference = db.collection("comments");
        collectionReference.add(comment);
    }


}
