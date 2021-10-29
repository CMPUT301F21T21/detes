package com.example.all_habits;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HabitsList extends ArrayAdapter<Habit> {
    private ArrayList<Habit> habits;
    private Context context;

    public HabitsList(Context context, ArrayList<Habit> habits){
        super(context,0, habits);
        this.habits = habits;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.habits_list, parent,false);
        }

        /*
        Button commentsButton = view.findViewById(R.id.commentsButton);
        commentsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Testing", "MESSAGE");
                Intent intent = new Intent(this, Comments.class);
                startActivity(intent);
            }
        });

         */


        Habit habit = habits.get(position);
        TextView cityName = view.findViewById(R.id.habits_title_textview);
        cityName.setText(habit.getTitle());

        return view;
    }
}
