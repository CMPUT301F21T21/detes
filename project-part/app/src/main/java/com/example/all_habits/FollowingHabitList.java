package com.example.all_habits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Allows you to see a list of habits for all those you follow
 */

public class FollowingHabitList extends ArrayAdapter<Habit> {

    //initialize
    private ArrayList<Habit> habits;
    private Context context;
    private View view;
    private TextView habitName;

    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    public FollowingHabitList(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }

    //shows you a list of all public habits of all followers
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        view = convertView;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.following_habits_list,parent,false);
        }

        habitName = view.findViewById(R.id.followingHabitName);
        habitName.setText(habits.get(position).getHabitName().toString());


        return view;
    }
}
