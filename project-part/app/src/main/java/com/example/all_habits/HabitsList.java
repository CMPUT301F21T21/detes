package com.example.all_habits;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HabitsList extends ArrayAdapter<Habit> {
    private ArrayList<Habit> habits;
    private Context context;
    View view;


    public HabitsList(Context context, ArrayList<Habit> habits){
        super(context,0, habits);
        this.habits = habits;
        this.context = context;
    }

    public void toComments(){
        Intent intent = new Intent(context, Comments.class);
        context.startActivity(intent);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        view = convertView;
        ImageView expandArrowButton;
        ImageView commentsButton;

        TextView habitReasonText;
        TextView habitStartDateText;
        TextView habitDaysText;

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.habits_list, parent, false);
        }

            commentsButton = view.findViewById(R.id.Comment);
            commentsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    toComments();
                }
            });

            habitReasonText = view.findViewById(R.id.habitReason_TextView);
            habitStartDateText = view.findViewById(R.id.habitStartDate_TextView);
            habitDaysText = view.findViewById(R.id.habitDays_TextView);


            expandArrowButton = view.findViewById(R.id.expandArrow);
            expandArrowButton.setTag("expand");
            expandArrowButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if ("expand".equals(expandArrowButton.getTag())){
                        Log.d("here", "1");
                        expandArrowButton.setImageResource(R.drawable.ic_collapse_arrow);
                        habitReasonText.setVisibility(View.VISIBLE);
                        habitStartDateText.setVisibility(View.VISIBLE);
                        habitDaysText.setVisibility(View.VISIBLE);
                        expandArrowButton.setTag("collapse");}

                    else if ("collapse".equals(expandArrowButton.getTag())){ // the user clicks on the collapse arrow
                        Log.d("here", "2");
                        expandArrowButton.setImageResource(R.drawable.ic_expand_arrow);
                    habitReasonText.setVisibility(View.GONE);
                    habitStartDateText.setVisibility(View.GONE);
                    habitDaysText.setVisibility(View.GONE);
                    expandArrowButton.setTag("expand");
                    }

                }
            });



        Habit habit = habits.get(position);
        TextView cityName = view.findViewById(R.id.habitTitle_TextView);
        cityName.setText(habit.getTitle());

        return view;
    }
}
