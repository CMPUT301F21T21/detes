package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;

/**
 * The main activity where the habit list is shown and navigation to the other activities happens here.
 */
public class MainActivity extends AppCompatActivity {

    //initialize
    ListView habitsListView;
    ImageView addButton;
    ImageView homeButton;
    ImageView habitButton;
    ImageView profileButton;
    ImageView addUserButton;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitArrayList;
    CollectionReference collectionReference;
    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;


    @Override
    protected void onRestart() {
        super.onRestart();
        CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
        collectionReference.orderBy("habitNum", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        // get a reference to the listview and create an object for the city list
        habitsListView = findViewById(R.id.habits_list);
        habitArrayList = new ArrayList<>();

        habitAdapter = new HabitsList(this, habitArrayList);
        habitsListView.setAdapter(habitAdapter); //converts data source to ListView

        //Edit a habit that you click on the listView.
        habitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditDelete.class);
                intent.putExtra("habitNum", position + 1);
                intent.putExtra("size", habitArrayList.size());
                intent.putStringArrayListExtra("completedDaysList", habitArrayList.get(position).getCompletedDaysList());
                startActivity(intent);
            }
        });

        //to return to the all habits button
        habitButton = findViewById(R.id.allHabits);
        habitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Already showing All Habits page", Toast.LENGTH_SHORT).show();
            }


        });

        //Add a new habit with the create activity.
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Create.class);
                startActivity(intent);
            }
        });

        //Opens the DisplayUserProfile page.
        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DisplayUserProfile.class);
                startActivity(intent);
            }
        });


        //Opens the todaysHabit page.
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodaysHabits.class);
                startActivity(intent);
            }
        });


        addUserButton = findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchPage.class);
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
    }
}

