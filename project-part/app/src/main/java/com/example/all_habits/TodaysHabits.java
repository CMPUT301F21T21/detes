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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Lists today's habits
 */

public class TodaysHabits extends AppCompatActivity {

    //initialize
    ImageView habitButton;
    ImageView addButton;
    ImageView homeButton;
    ImageView profileButton;
    ImageView addUserButton;

    ListView todayListView;
    ArrayList<Habit> todayArrayList;
    ArrayAdapter<Habit> todayAdapter;
    String currentDateString;
    ArrayList<Integer> completedHabitsList; // tracked using habit nums

    ListView completedListView;
    ArrayList<Habit> completedArrayList;
    ArrayAdapter<Habit> completedAdapter;

    String[] daysOfTheWeek = {"Sun","Mon","Tues","Wed","Thurs","Fri","Sat"};

    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todays_habits);

        //gets date today
        Calendar c = Calendar.getInstance();
        todayArrayList = new ArrayList<Habit>();
        int dayNum = c.get(Calendar.DAY_OF_WEEK) - 1;
        String day = daysOfTheWeek[dayNum];
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
        // get a reference to the listview and create an object for the todaylist
        todayListView = findViewById(R.id.todays_list_pending);
        todayArrayList = new ArrayList<>();

        todayAdapter = new TodaysList(this, todayArrayList, true);
        todayListView.setAdapter(todayAdapter); //converts data source to ListView

        completedListView = findViewById(R.id.todays_list_completed);
        completedArrayList = new ArrayList<>();
        completedAdapter = new TodaysList(this, completedArrayList, false);
        completedListView.setAdapter(completedAdapter); //converts data source to ListView


        // getting data from firebase to your local device (snapshot of database)
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                todayArrayList.clear();
                completedArrayList.clear();
                ArrayList<Integer> trackList = new ArrayList<Integer>();

                ArrayList<String> habitDays;
                String dateString;
                for(QueryDocumentSnapshot habits: queryDocumentSnapshots)
                {
                    Habit habit= habits.toObject(Habit.class);
                    habitDays = habit.getHabitDays();
                    dateString = habit.getStartDate();

                    //Formats the string into the form 01/01/1990
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
                    currentDateString = simpleFormat.format(c.getTime());
                    Date date1 = null;

                    c.set(Calendar.MILLISECOND, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.HOUR, 0);
                    c.set(Calendar.MINUTE, 0);
                    Date date2 = c.getTime();
                    try {
                        date1 = simpleFormat.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(habit.getHabitName() != null) {
                        for(int i = 0;i < habitDays.size();i++){
                            if(habitDays.get(i).toString().equals(day)){
                                if(date1.before(date2) || (date1.compareTo(date2) == 0) && !trackList.contains(habit.getHabitNum()))
                                    todayArrayList.add(habit);
                                trackList.add(habit.getHabitNum());
                            }
                            Log.d("Tag", date1.toString());
                        }
                    }

                    completedHabitsList = new ArrayList<Integer>();
                    if (habit.getCompletedDaysList().contains(currentDateString) && !completedHabitsList.contains(habit.getHabitNum())){
                        completedHabitsList.add(habit.getHabitNum());
                        todayArrayList.remove(habit);
                        completedArrayList.add(habit);
                    }
                }
                todayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
                //from the cloud
                completedAdapter.notifyDataSetChanged();

                Log.d("todayArrayList_", String.valueOf(todayArrayList));
                Log.d("completedArrayList_", String.valueOf(completedArrayList));
            }
        });

        //click on item in listview pending to move to completed
        todayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Habit habit = todayArrayList.get(position);

                if (!completedHabitsList.contains(habit.getHabitNum())) {
                    completedArrayList.add(todayArrayList.get(position)); // adding today's habit that was completed
                }
                todayArrayList.remove(todayArrayList.get(position));

                if (!(habit.getCompletedDaysList().contains(currentDateString))) {
                    habit.addToCompletedDaysList(currentDateString);
                }
                habit.setProgress();

                Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habit.getHabitNum()).limit(1);
                findHabit.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String habitId = document.getId();
                                        DocumentReference documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                                        documentRef.update("completedDaysList", habit.getCompletedDaysList());
                                        documentRef.update("progress", habit.getProgress());
                                    }

                                }

                            }
                        });

                todayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
                //from the cloud
                completedAdapter.notifyDataSetChanged();
            }

        });

        completedAdapter.notifyDataSetChanged();
        todayAdapter.notifyDataSetChanged();

        //to return to the all habits button
        habitButton = findViewById(R.id.allHabits);
        habitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodaysHabits.this, MainActivity.class);
                startActivity(intent);
            }


        });

        //Opens the todaysHabit page.
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TodaysHabits.this, "Already showing Today's Habits page", Toast.LENGTH_SHORT).show();
            }
        });

        //Add a new habit with the create activity.
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodaysHabits.this, Create.class);
                startActivity(intent);
                todayAdapter.notifyDataSetChanged();
                completedAdapter.notifyDataSetChanged();
            }
        });

        //Opens the DisplayUserProfile page.
        profileButton = findViewById(R.id.User);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodaysHabits.this, DisplayUserProfile.class);
                startActivity(intent);
            }
        });


        //goes to the search user page
        addUserButton = findViewById(R.id.addFriend);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodaysHabits.this, SearchPage.class);
                startActivity(intent);
            }
        });




    }
}
