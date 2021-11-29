package com.example.all_habits;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Creates an add comment fragment on the Comments page.
 */
public class addCommentFragment extends DialogFragment {

    //intialize
    private int commentNum;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener{
        void onOkPressed(Comment comment);
    }

    addCommentFragment(int commentNum){
        this.commentNum = commentNum;
    }

    //comment for specific habit
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString());
        }
    }

    //add comment no more than 20 characters
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_comment_fragment_layout, null);

        EditText commentTextFragment = view.findViewById(R.id.commentFragmentText);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Comment")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogueInterface, int i) {
                        String commentText = commentTextFragment.getText().toString();
                        if(commentText.length() > 20) {
                            Toast.makeText(getContext(), "The comment has to be under 20 characters long.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listener.onOkPressed(new Comment(commentText,commentNum));
                    }
                }).create();
    }

}