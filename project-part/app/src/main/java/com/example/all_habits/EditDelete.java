package com.example.all_habits;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Edit or delete's a habit.
 * @author Derek
 */
public class EditDelete extends AppCompatActivity {

    Button cancelButton;
    Button editButton;
    Button deleteButton;
    Switch privateSwitch;
    int habitNum;
    String habitId = "habit1";
    DocumentReference documentRef;
    EditText habitName;
    EditText reason;
    EditText startDate;
    EditText habitDays;
    TextView habitTextView;
    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdelete);
        Intent intent = getIntent();
        habitNum = intent.getIntExtra("habitNum",1);
        habitName = findViewById(R.id.habitName);
        reason = findViewById(R.id.habitReason);
        startDate = findViewById(R.id.habitStartDate);
        habitDays = findViewById(R.id.habitDays);
        habitTextView = findViewById(R.id.habitNumber);
        privateSwitch = findViewById(R.id.privateSwitch);
        Context context = getApplicationContext();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        cancelButton = findViewById(R.id.cancelButton);
        editButton = findViewById(R.id.createButton);
        deleteButton = findViewById(R.id.deleteButton);

        //Retrieve habit document.
        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                habitId = document.getId();
                                Log.d("TAG", habitId);
                            }
                        }

                        documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                                        //Sets all the Edit Text fields to their database counterparts.
                                        habitName.setText(document.getString("habitName"));
                                        reason.setText(document.getString("reason"));
                                        startDate.setText(document.getString("startDate"));
                                        habitDays.setText(document.getString("habitDays"));
                                        habitTextView.setText("Habit #" + habitNum);
                                    }else{
                                        Log.d("TAG","No such document");
                                    }
                                }
                            }
                        });
                    }
                });

        //Updates text with what is written on the EditText boxes.
        editButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(habitName.getText().length() > 20){
                    Toast.makeText(context,"Habit name has to be under 20 characters long.",Toast.LENGTH_SHORT).show();
                }else if(reason.getText().length() > 30) {
                    Toast.makeText(context, "Reason has to be under 30 characters long.", Toast.LENGTH_SHORT).show();
                }else if(habitName.getText().toString().isEmpty() || reason.getText().toString().isEmpty()|| startDate.getText().toString().isEmpty() || habitDays.getText().toString().isEmpty()){
                    Toast.makeText(context, "Fill in all the data fields", Toast.LENGTH_SHORT).show();
                }else {
                    documentRef.update("habitName", habitName.getText().toString());
                    documentRef.update("reason", reason.getText().toString());
                    documentRef.update("startDate", startDate.getText().toString());
                    documentRef.update("habitDays", habitDays.getText().toString());
                    if (privateSwitch.isChecked()) {
                        documentRef.update("Private", true);
                    } else {
                        documentRef.update("Private", false);
                    }
                }
            }
        });

        //Deletes the habit.
        deleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
                documentRef.delete();
                //Sets the ordered documents to 1 and increments up by 1 until the loop ends.
                collectionReference.orderBy("habitNum", Query.Direction.ASCENDING).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int habitNum = 1;
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("Habits ",document.getId());
                                        collectionReference.document(document.getId()).update("habitNum", habitNum);
                                        habitNum++;
                                    }
                                }
                            }
                        });
                //Go back to list of habits.
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Go back to list of habits.
                finish();
            }
        });


    }
}
