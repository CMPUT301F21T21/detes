package com.example.all_habits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

//Creates the comments list for the listview.
public class CommentsList extends ArrayAdapter<Comment> {

    private ArrayList<Comment> comments;
    private Context context;
    private View view;

    TextView commentText;

    /**
     * Constructor for the CommentsList
     * @param context
     * @param comments
     */
    public CommentsList(@NonNull Context context, ArrayList<Comment> comments) {
        super(context,0, comments);
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        view = convertView;
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        // access the habits_list.xml to work with the buttons
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.comments_list, parent, false);
        }


        Button deleteComment;
        Comment comment = comments.get(position);
        commentText = view.findViewById(R.id.comments_textview);
        commentText.setText(comment.getCommentString());

        /*deleteComment = view.findViewById(R.id.deleteComment);
        deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CollectionReference commentReference = db.collection("comments");
            }
        });*/


        return view;
    }


}