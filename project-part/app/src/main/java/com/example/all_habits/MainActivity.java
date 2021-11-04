package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView habitsListView;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitArrayList;

    // create habit attributes
    Button createhabit;
    EditText addhabitname;
    EditText addreasontext;
    EditText adddaystext;
    EditText addstartingtext;


    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    final String TAG = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get a refernce to the listview abd create an object for the city list
        habitsListView = findViewById(R.id.habits_list);
        habitArrayList = new ArrayList<>();

        habitArrayList.add(new Habit("HabitTitle1"));

        habitAdapter = new HabitsList(this, habitArrayList);
        habitsListView.setAdapter(habitAdapter); //converts data source to ListView

        habitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditDelete.class);
                startActivity(intent);
            }
        });

        // create habit page

        // get references to the corresponding views from the xml file
        //createhabit = findViewById(R.id.create);
        //addhabitname = findViewById(R.id.habit);
        //addreasontext = findViewById(R.id.reason);
        //adddaystext = findViewById(R.id.days);
        //addstartingtext = findViewById(R.id.starting);

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        // then get a top level reference to the firestore collection
        // the assignemnt means that it will create a collection name habits if it does already exist
        // but will append if the collection does exist
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());



        // write the logic for the 'createhabit' button
        // it will extract all of the information enter in the create and add to the firestore once the create button is click
        // and the list of habits would then come from firestore???
        /*createhabit.setOnClickListener( new View.OnClickListener() {
            @Override    public void onClick(View view) {
                // extraction
                final String habitname =  addhabitname.getText().toString();
                final String reasontext = addreasontext.getText().toString();
                // will have to convert the string into a string array
                final String daystext =  adddaystext.getText().toString();
                // will have to convert the string to a date
                final String startingtext =  addstartingtext.getText().toString();

                // create a HashMap to store the extracted data
                HashMap<String, String> data = new HashMap<>();

                // then check to see if there are inputs because we don't want to extract and store data
                if (habitname.length()>0 && reasontext.length()>0 && daystext.length()>0 && startingtext.length()>0) {
                    // if there are inputs then we need to create a new key-value(Habit)
                    // how can we have the habit# increase after every click???
                    data.put("Habit", habitname);

                    // using the code snippet below, the data would be added to firestore
                    collectionReference
                            .document(reasontext)
                             //how can we add all of the information
                            //.document(daystext)
                             //.document(reasontext, daystext, startingtext)???
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                     //these are a method which gets executed when the task is succeeded
                                    Log.d(TAG, "Data has been added successfully!");
                                    addhabitname.setText("");
                                    addreasontext.setText("");
                                    adddaystext.setText("");
                                    addstartingtext.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //these are a method which gets executed if there’s any problem
                                    Log.d(TAG, "Data could not be added!" + e.toString());
                                }
                            });
                }

            }
        });*/


    }


}