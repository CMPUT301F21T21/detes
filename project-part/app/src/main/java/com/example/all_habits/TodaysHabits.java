package com.example.all_habits;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Lists today's habits
 */

public class TodaysHabits extends AppCompatActivity {

    //initialize
    ImageView habitButton;
    ImageView addButton;
    ImageView homeButton;
    ImageView profileButton;


    ListView todayListView;
    ArrayList<Habit> todayArrayList;
    ArrayAdapter<Habit> todayAdapter;

    ListView completedListView;
    ArrayList<Habit> completedTodayArrayList;
    ArrayAdapter<Habit> completedTodayAdapter;
    String[] daysOfTheWeek = {"Sun","Mon","Tues","Wed","Thurs","Fri","Sat"};

    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;


    public ArrayList<Habit> getTodayArrayList() {
        return todayArrayList;
    }

    //gets today's date and creates an array list of the habits for today
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

        todayAdapter = new TodaysHabitsList(this, todayArrayList);
        todayListView.setAdapter(todayAdapter); //converts data source to ListView

        completedListView = findViewById( R.id.todays_list_completed );
        completedTodayArrayList = new ArrayList<>();

        completedTodayAdapter = new TodaysHabitsList(  this, completedTodayArrayList );
        completedListView.setAdapter( completedTodayAdapter );



        // getting data from firebase to your local device (snapshot of database)
        //adds habit of today to the array list pending
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                todayArrayList.clear();
                ArrayList<String> habitDays;
                String dateString;
                for(QueryDocumentSnapshot habits: queryDocumentSnapshots)
                {
                    Habit habit= habits.toObject(Habit.class);
                    habitDays = habit.getHabitDays();
                    dateString = habit.getStartDate();

                    //Formats the string into the form 01/01/1990
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDateString = simpleFormat.format(c.getTime());
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
                                if(date1.before(date2) || (date1.compareTo(date2) == 0))
                                todayArrayList.add(habit);


                            }
                            Log.d("Tag", date1.toString());
                        }
                    }

                    //gets date
                    String todayWeekDay;
                    SimpleDateFormat dayFormat = new SimpleDateFormat( "EEEE", Locale.US ); //get the full name of the weekday
                    Calendar calendar = Calendar.getInstance();
                    todayWeekDay = dayFormat.format( calendar.getTime() );

                    //uses code from HabitsList
                    //moves habit from pending to complete and back based on if checkboxes are checked or not
                    if (habit.getCompletedDaysList().contains( todayWeekDay )) {
                        completedTodayArrayList.add( habit );
                        todayArrayList.remove( habit );

                    }
                }
                // Notifying the adapter to render any new data fetched
                //from the cloud
                todayAdapter.notifyDataSetChanged();
                completedTodayAdapter.notifyDataSetChanged();
            }
        });

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


    }
}

