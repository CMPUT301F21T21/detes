package com.example.all_habits;

import android.content.Intent;
import android.os.Bundle;
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

    //initialize
    private ArrayList<Comment> commentArrayList;
    private Button backButton;
    private ImageView user;
    private int habitNum;
    private int commentNum = 1;
    private TextView habitNumText;

    Button deleteComment;
    Button addButton;
    ListView commentListView;
    ArrayAdapter<Comment> commentAdapter;
    Comment testComment;

    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private CollectionReference habitReference; // collection of selected habit
    private CollectionReference userCollectionReference; // collection of signed in user


    private String habitId; // to keep track of which habit was selected

    //displays list of all the comments for a habit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        Intent intent = getIntent();

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // current user collection reference
        //final CollectionReference commentReference = db.collection("comments");

        //commentNum = 1;

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


        //return to previous page
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish(); // goes back to the all habits page
            }
        });


        //displays user profile
        user = findViewById(R.id.User);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Comments.this, DisplayUserProfile.class);
                startActivity(intent);
            }
        });

        //adds comment
        addButton = findViewById(R.id.addComment);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new addCommentFragment(commentNum).show(getSupportFragmentManager(), "ADD_COMMENT");
                commentNum +=1;
            }
        });


        //view list of comments
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
                                    //commentReference.document(commentId).delete();
                                }
                            }
                        });
                return false;
            }
        });

        //adds comments to database
        userCollectionReference = db.collection(currentFireBaseUser.getUid());
        // finds the specific habit
        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                habitId = document.getId();
                            }
                        }

                        habitReference = userCollectionReference.document(habitId).collection("Comments");

                        // getting data from firebase to your local device (snapshot of database)
                        habitReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                                    FirebaseFirestoreException error) {
                                commentArrayList.clear();
                                for (QueryDocumentSnapshot comments : queryDocumentSnapshots) {
                                    Comment comment = comments.toObject(Comment.class);
                                    commentArrayList.add(comment);
                                }
                                commentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
                                //from the cloud
                            }
                        });

                    }});


    }

    /**
     * Adds the comment into the collection
     * @param comment
     */
    @Override
    public void onOkPressed(Comment comment){
        //CollectionReference collectionReference = db.collection("comments");
        //collectionReference.add(comment);
        comment.setUserId(currentFireBaseUser.getUid());
        comment.setHabitId(habitId);
        habitReference = userCollectionReference.document(habitId).collection("Comments");
        habitReference.add(comment);


    }


}