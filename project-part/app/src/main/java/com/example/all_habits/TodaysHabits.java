package com.example.all_habits;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

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

    ImageView habitButton;

    ListView todayListView;
    ArrayList<Habit> todayArrayList;
    ArrayAdapter<Habit> todayAdapter;
    String[] daysOfTheWeek = {"Sun","Mon","Tues","Wed","Thurs","Fri","Sat"};

    //firestore attribute
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todays_habits);

        Calendar c = Calendar.getInstance();
        todayArrayList = new ArrayList<Habit>();
        int dayNum = c.get(Calendar.DAY_OF_WEEK) - 1;
        String day = daysOfTheWeek[dayNum];
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
        // get a reference to the listview and create an object for the city list
        todayListView = findViewById(R.id.todays_list_pending);
        todayArrayList = new ArrayList<>();

        todayAdapter = new HabitsList(this, todayArrayList);
        todayListView.setAdapter(todayAdapter); //converts data source to ListView


        // getting data from firebase to your local device (snapshot of database)
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
                }
                todayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
                //from the cloud
            }
        });

        habitButton = findViewById(R.id.allHabits);
        habitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodaysHabits.this, MainActivity.class);
                startActivity(intent);
            }


        });
    }
}
