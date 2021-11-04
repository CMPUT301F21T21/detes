package com.example.all_habits;
import android.content.Context;
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
    Switch privateSwitch;
    TextView habitTextView;

    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Context context = getApplicationContext();
        int habitNum = 1;
        habitName = findViewById(R.id.habitName);
        reasonName = findViewById(R.id.habitReason);
        startDate = findViewById(R.id.habitStartDate);
        weekDays = findViewById(R.id.habitDays);
        habitTextView = findViewById(R.id.habitNumber);
        privateSwitch = findViewById(R.id.privateSwitch);

        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        cancelButton = findViewById(R.id.cancelButton);
        createButton = findViewById(R.id.createButton);

        Map<String, Object> habit = new HashMap<>();
        CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        //Finds the number of the last habit.
        query = collectionReference.orderBy("habitNum", Query.Direction.DESCENDING).limit(1);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("Query", "Worked " + document.get("habitNum"));
                                habit.put("habitNum", document.getLong("habitNum").intValue() + 1);

                                //Set top habit title to the current habit.
                                habitTextView.setText("Habit #" + (document.getLong("habitNum").intValue() + 1));
                            }
                            }else{
                            Log.d("Query", "Didn't work");
                        }

                        //If the query is empty, set habitNum to 1.
                        if(task.getResult().isEmpty()){
                            habit.put("habitNum", habitNum);
                            habitTextView.setText("Habit #" + habitNum);
                        }
                    }
                });

        //Updates text with what is written on the EditText boxes.
        createButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(habitName.getText().length() > 20){
                    Toast.makeText(context,"Habit name has to be under 20 characters long.",Toast.LENGTH_SHORT).show();
                }else if(reasonName.getText().length() > 30) {
                    Toast.makeText(context, "Reason has to be under 30 characters long.", Toast.LENGTH_SHORT).show();
                }else if(habitName.getText().toString().isEmpty() || reasonName.getText().toString().isEmpty()|| startDate.getText().toString().isEmpty() || weekDays.getText().toString().isEmpty()){
                    Toast.makeText(context, "Fill in all the data fields", Toast.LENGTH_SHORT).show();
                }else {
                    habit.put("habitName", habitName.getText().toString());
                    habit.put("reason", reasonName.getText().toString());
                    habit.put("startDate", startDate.getText().toString());
                    habit.put("habitDays", weekDays.getText().toString());
                    if (privateSwitch.isChecked()) {
                        habit.put("Private", true);
                    } else {
                        habit.put("Private", false);
                    }

                    collectionReference
                            .add(habit);
                    finish();
                }
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
