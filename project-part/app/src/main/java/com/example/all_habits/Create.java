package com.example.all_habits;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a habit to display on the MainActivity and stored in the HabitsList
 */
public class Create extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Button cancelButton;
    Button createButton;
    EditText habitName;
    EditText reasonName;
    EditText startDate;
    Switch privateSwitch;
    TextView habitTextView;
    CheckBox Saturday;
    CheckBox Monday;
    CheckBox Tuesday;
    CheckBox Wednesday;
    CheckBox Thursday;
    CheckBox Friday;
    CheckBox Sunday;
    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private EditText clickedEditText;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Context context = getApplicationContext();
        int habitNum = 1;
        ArrayList<String> habitDayArray = new ArrayList<String>();

        habitName = findViewById(R.id.habitName);
        reasonName = findViewById(R.id.habitReason);
        startDate = findViewById(R.id.habitStartDate);
        habitTextView = findViewById(R.id.habitNumber);
        privateSwitch = findViewById(R.id.privateSwitch);
        Saturday = findViewById(R.id.saturday);
        Monday = findViewById(R.id.monday);
        Tuesday = findViewById(R.id.tuesday);
        Wednesday = findViewById(R.id.wednesday);
        Thursday = findViewById(R.id.thursday);
        Friday = findViewById(R.id.friday);
        Sunday = findViewById(R.id.sunday);
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        cancelButton = findViewById(R.id.cancelButton);
        createButton = findViewById(R.id.createButton);

        Map<String, Object> habit = new HashMap<>();
        CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        //Creates the DatePickerDialog when StartDate EditText is clicked on.
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                DialogFragment datePicker = new DatePickerDialogFragment();
                clickedEditText = startDate;
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

        //Finds the last habit number and increments it by one.
        query = collectionReference.orderBy("habitNum", Query.Direction.DESCENDING).limit(1);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("Query", "Worked " + document.get("habitNum"));

                                //Puts the next habit number into the hashmap.
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

                //If none of the checkboxes are selected, do nothing.
                if(!Saturday.isChecked() && !Monday.isChecked() && !Tuesday.isChecked() && !Wednesday.isChecked()
                && !Thursday.isChecked() && !Friday.isChecked() && !Sunday.isChecked()){
                    Toast.makeText(context,"Choose a day of the week or multiple to work on your habit.",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(Sunday.isChecked()){
                        habitDayArray.add("Sun");
                    }
                    if(Monday.isChecked()){
                        habitDayArray.add("Mon");
                    }
                    if(Tuesday.isChecked()){
                        habitDayArray.add("Tues");
                    }
                    if(Wednesday.isChecked()){
                        habitDayArray.add("Wed");
                    }
                    if(Thursday.isChecked()){
                        habitDayArray.add("Thurs");
                    }
                    if(Friday.isChecked()){
                        habitDayArray.add("Fri");
                    }
                    if(Saturday.isChecked()){
                        habitDayArray.add("Sat");
                    }
                }
                //If the habit name is less than 20, the reason's length is less than 30, and all the boxes are filled in, add the habit.
                if(habitName.getText().length() > 20){
                    Toast.makeText(context,"Habit name has to be under 20 characters long.",Toast.LENGTH_SHORT).show();
                }else if(reasonName.getText().length() > 30) {
                    Toast.makeText(context, "Reason has to be under 30 characters long.", Toast.LENGTH_SHORT).show();
                }else if(habitName.getText().toString().isEmpty() || reasonName.getText().toString().isEmpty()|| startDate.getText().toString().isEmpty()){
                    Toast.makeText(context, "Fill in all the data fields", Toast.LENGTH_SHORT).show();
                }else {
                    habit.put("habitName", habitName.getText().toString());
                    habit.put("reason", reasonName.getText().toString());
                    habit.put("startDate", startDate.getText().toString());
                    habit.put("habitDays", habitDayArray);
                    if (privateSwitch.isChecked()) {
                        habit.put("Private", true);
                    } else {
                        habit.put("Private", false);
                    }
                    collectionReference.add(habit);
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

    //Sets the start date of the DatePickerDialog.
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = simpleFormat.format(c.getTime());
        clickedEditText.setText(currentDateString);
    }
}
