package com.example.all_habits;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Edit or delete's a habit created from the create activity.
 */
public class EditDelete extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Button cancelButton;
    Button editButton;
    Button deleteButton;
    Switch privateSwitch;
    Button commentButton;

    int habitNum;
    int size;
    int swapPos = 0;
    String habitId;
    ArrayList<String> completedDaysList;
    int newProgress;

    DocumentReference documentRef;
    EditText habitName;
    EditText reason;
    EditText startDate;
    EditText endDate;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdelete);
        Intent intent = getIntent();
        ArrayList<String> habitDayArray = new ArrayList<String>();
        ArrayList<String> items = new ArrayList<String>();

        habitNum = intent.getIntExtra("habitNum",1);
        size = intent.getIntExtra("size", 0);
        completedDaysList = intent.getStringArrayListExtra("completedDaysList");
        habitName = findViewById(R.id.habitName);
        reason = findViewById(R.id.habitReason);
        startDate = findViewById(R.id.habitStartDate);
        endDate = findViewById(R.id.habitEndDate);
        habitTextView = findViewById(R.id.habitNumber);
        privateSwitch = findViewById(R.id.privateSwitch);

        Saturday = findViewById(R.id.saturday);
        Monday = findViewById(R.id.monday);
        Tuesday = findViewById(R.id.tuesday);
        Wednesday = findViewById(R.id.wednesday);
        Thursday = findViewById(R.id.thursday);
        Friday = findViewById(R.id.friday);
        Sunday = findViewById(R.id.sunday);

        Spinner dropdown = findViewById(R.id.habitPosition);
        cancelButton = findViewById(R.id.cancelButton);
        editButton = findViewById(R.id.createButton);
        deleteButton = findViewById(R.id.deleteComment);
        commentButton = findViewById(R.id.takePhotoButton);

        Context context = getApplicationContext();

        //Populates the spinner.
        for(int i = 0; i < size; i++){
            items.add(String.valueOf(i + 1));
        }

        //Puts the Spinner values into the Spinner.
        ArrayAdapter<String>adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(habitNum - 1);

        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //CollectionReference collectionRef = db.collection(currentFireBaseUser.getUid());

        //Creates the DatePickerDialog when StartDate EditText is clicked on.
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                DialogFragment datePicker = new DatePickerDialogFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        //Creates the DatePickerDialog2 when EndDate EditText is clicked on.
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                DialogFragment datePicker2 = new DatePickerDialogFragment2();
                datePicker2.show(getSupportFragmentManager(), "date picker");

            }
        });

        //Retrieve the habit document that has the same value in the field habitNum.
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

                            //Finds the current habit document with it's habitId so the fields can be edited.
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                ArrayList<String> habitDays =  new ArrayList<String>();
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                                        //Sets all the Edit Text fields to their database counterparts.
                                        habitName.setText(document.getString("habitName"));
                                        reason.setText(document.getString("reason"));
                                        startDate.setText(document.getString("startDate"));
                                        habitTextView.setText("Habit #" + habitNum);
                                        habitDays = (ArrayList<String>) document.get("habitDays");
                                        for(int i = 0; i < habitDays.size();i++) {
                                            if (habitDays.get(i).equals("Sun")) {
                                                Sunday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Mon")) {
                                                Monday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Tues")) {
                                                Tuesday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Wed")) {
                                                Wednesday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Thurs")) {
                                                Thursday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Fri")) {
                                                Friday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Sat")) {
                                                Saturday.setChecked(true);
                                            }
                                        }
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

                //Moves the current habit to the position that the spinner has designated.
                swapPos = Integer.parseInt(dropdown.getSelectedItem().toString());
                if(swapPos < habitNum) {

                    //Returns a query where the habitNum value is greater than or equal to the position the habit wants to swap to.
                    Query swapHabit = db.collection(currentFireBaseUser.getUid()).whereGreaterThanOrEqualTo("habitNum", swapPos);
                    swapHabit.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int tempHabitNum = swapPos + 1;
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (tempHabitNum <= habitNum) {
                                                db.collection(currentFireBaseUser.getUid()).document(document.getId()).update("habitNum", tempHabitNum);
                                                tempHabitNum++;
                                            }
                                            documentRef.update("habitNum", swapPos);
                                        }
                                    }
                                }
                            });
                }else{
                    documentRef.update("habitNum",swapPos + 1);
                }

                //Reorders the habitNumbers starting at 1.
                //Helps keep an ordered list.
                Query fixHabit = db.collection(currentFireBaseUser.getUid()).orderBy("habitNum", Query.Direction.ASCENDING);
                fixHabit.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int tempHabitNum = 1;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection(currentFireBaseUser.getUid()).document(document.getId()).update("habitNum", tempHabitNum);
                                    tempHabitNum++;
                                }
                            }
                        });

                //If the habit name is less than 20, the reason's length is less than 30, and all the boxes are filled in, add the habit.
                if(habitName.getText().length() > 20){
                    Toast.makeText(context,"Habit name has to be under 20 characters long.",Toast.LENGTH_SHORT).show();
                }else if(reason.getText().length() > 30) {
                    Toast.makeText(context, "Reason has to be under 30 characters long.", Toast.LENGTH_SHORT).show();
                }else if(habitName.getText().toString().isEmpty() || reason.getText().toString().isEmpty()|| startDate.getText().toString().isEmpty()){
                    Toast.makeText(context, "Fill in all the data fields", Toast.LENGTH_SHORT).show();
                }else {
                    documentRef.update("habitName", habitName.getText().toString());
                    documentRef.update("reason", reason.getText().toString());
                    documentRef.update("startDate", startDate.getText().toString());
                    documentRef.update("habitDays", habitDayArray);
                    if (privateSwitch.isChecked()) {
                        documentRef.update("Private", true);
                    } else {
                        documentRef.update("Private", false);
                    }

                    // check if the user removed a "completed" day (eg. habit completed on Monday, but the habit days
                    // no longer include Monday
                    if (completedDaysList != null) {
                        for (String completedDay : completedDaysList) {
                            if (completedDay.equals("Tuesday") || completedDay.equals("Thursday")) {

                                // remove "Tues" or "Thurs" (4 characters long)
                                if (!habitDayArray.contains(completedDay.substring(0, 4))) {
                                    completedDaysList.remove(completedDay);
                                }
                            } else if (!habitDayArray.contains(completedDay.substring(0, 3))) {
                                completedDaysList.remove(completedDay);
                            }
                        }

                        // update the progress and the completed days
                        newProgress = (int) (((float) completedDaysList.size() / habitDayArray.size()) * 100);
                        documentRef.update("progress", newProgress);
                        documentRef.update("completedDaysList", completedDaysList);
                    }
                    finish();
                }
            }
        });

        //Deletes the habit.
        deleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
                documentRef.delete();

                //Increments the habitNum field of the document
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

    //Sets the start date and end date of the DatePickerDialog.
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        //Sets the calendar date to c.
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        //Formats the string into the form 01/01/1990
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = simpleFormat.format(c.getTime());
        startDate.setText(currentDateString);
        endDate.setText(currentDateString);
    }
}
