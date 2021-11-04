package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView habitsListView;
    ImageView user;
    ImageView addButton;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        user = findViewById(R.id.User);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayUserProfile.class);
                startActivity(intent);
            }
        });

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateHabit.class);
                startActivity(intent);
            }
        });

    }

}