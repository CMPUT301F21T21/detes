package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

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

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView habitsListView;
    ImageView addButton;
    ImageView homeButton;
    ImageView profileButton;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitArrayList;

    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    final String TAG = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        // get a reference to the listview and create an object for the city list
        habitsListView = findViewById(R.id.habits_list);
        habitArrayList = new ArrayList<>();

        habitAdapter = new HabitsList(this, habitArrayList);
        habitsListView.setAdapter(habitAdapter); //converts data source to ListView

        habitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditDelete.class);
                intent.putExtra("habitNum", position + 1);
                intent.putExtra("size", habitArrayList.size());
                startActivity(intent);
            }
        });

        //Add a new habit.
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Create.class);
                startActivity(intent);
            }
        });

        //Opens the profile page.
        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DisplayUserProfile.class);
                startActivity(intent);
            }
        });


        //today's habit list
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodaysHabits.class);
                startActivity(intent);
            }
        });


        // getting data from firebase to your local device (snapshot of database)
        collectionReference.orderBy("habitNum",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                habitArrayList.clear();
                for(QueryDocumentSnapshot habits: queryDocumentSnapshots)
                {
                    Habit habit= habits.toObject(Habit.class);
                    if(habit.getHabitName() != null) {
                        habitArrayList.add(habit);
                    }
                }

                habitAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
                //from the cloud
            }
        });


        //Sets the ordered documents to 1 and increments up by 1 until the loop ends.
        collectionReference.orderBy("habitNum", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int habitNum = 1;
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                collectionReference.document(document.getId()).update("habitNum", habitNum);
                                habitNum++;
                            }
                        }
                    }
                });
    }

}