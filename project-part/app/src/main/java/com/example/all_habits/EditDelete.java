//Derek
package com.example.all_habits;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditDelete extends AppCompatActivity {

    Button cancelButton;
    Button editButton;
    Button deleteButton;

    EditText habitName;
    EditText reasonName;
    EditText startDate;
    EditText weekDays;
    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdelete);

        int habitNum = 1;
        habitName = findViewById(R.id.habitName);
        reasonName = findViewById(R.id.habitReason);
        startDate = findViewById(R.id.habitStartDate);
        weekDays = findViewById(R.id.habitDays);
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Retrieve habit document.
        DocumentReference documentRef = db.collection("testCollectionDerek").document("habit");
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        //Sets all the Edit Text fields to their database counterparts.
                        habitName.setText(document.getString("name"));
                        reasonName.setText(document.getString("reason"));
                        startDate.setText(document.getString("startDate"));
                        weekDays.setText(document.getString("weekDays"));
                    }else{
                        Log.d("TAG","No such document");
                    }
                }
            }
        });

        cancelButton = findViewById(R.id.cancelButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        //Updates text with what is written on the EditText boxes.
        editButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                documentRef.update("name", currentFireBaseUser.getUid());
                documentRef.update("reason", reasonName.getText().toString());
                documentRef.update("startDate", 10102);
                documentRef.update("weekDays", "Wednesdays");
            }
        });

        //Deletes the habit.
        deleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                documentRef.delete();

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
