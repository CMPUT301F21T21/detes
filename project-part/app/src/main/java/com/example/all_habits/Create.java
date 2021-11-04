package com.example.all_habits;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a habit
 * @author Derek
 */
public class Create extends AppCompatActivity {

    Button cancelButton;
    Button createButton;

    EditText habitName;
    EditText reasonName;
    EditText startDate;
    EditText weekDays;
    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        int habitNum = 1;
        habitName = findViewById(R.id.habitName);
        reasonName = findViewById(R.id.habitReason);
        startDate = findViewById(R.id.habitStartDate);
        weekDays = findViewById(R.id.habitDays);
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        cancelButton = findViewById(R.id.cancelButton);
        createButton = findViewById(R.id.createButton);


        Map<String, Object> habit = new HashMap<>();
        CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        /*query = collectionReference.orderBy("habitNum", Query.Direction.DESCENDING).limit(1);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("Query", "Worked");
                                habit.put("habitNum",document.getLong("habitNum") + 1);
                            }else{
                                Log.d("Query", "Didn't work");
                            }
                        }
                    }
                });*/

        //Updates text with what is written on the EditText boxes.
        createButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                habit.put("habitName", habitName.getText().toString());
                habit.put("reason", reasonName.getText().toString());
                habit.put("startDate", startDate.getText().toString());
                habit.put("weekDays", weekDays.getText().toString());
                collectionReference
                        .document("habit6")
                        .set(habit);
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
