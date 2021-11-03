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
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Determines what each listview item will look like, when displayed
 * on the all habits page (see storyboard sequence)
 *
 * @author Elena
 * @version 1.0
 *
 */

public class HabitsList extends ArrayAdapter<Habit> {

    private ArrayList<Habit> habits;
    private Context context;
    private View view;

    // values will not be changed
    private final String EXPAND_CONSTANT = "EXPAND";
    private final String COLLAPSE_CONSTANT = "COLLAPSE";

    /**
     * Constructor for the HabitsList
     *
     * @param context Activity context
     * @param habits an ArrayList that contains Habit objects
     */
    public HabitsList(Context context, ArrayList<Habit> habits){
        super(context,0, habits);
        this.habits = habits;
        this.context = context;
    }

    /**
     * When the comment button is clicked, we move to the Comments page
     */
    public void toCommentsPage(){
        Intent intent = new Intent(context, Comments.class);
        context.startActivity(intent);

    }

    /**
     * Handles what happens in each listview item (what is displayed,
     * the actions of button clicks, etc)
     *
     * @param position which listview item was clicked on
     * @param convertView
     * @param parent
     * @return View
     * @see View
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        View objects (ImageViews, TextViews) are added in this method so that we can handle the buttons
        in EACH listview item separately
         */
        view = convertView;
        ImageView expandArrowButton;
        ImageView commentsButton;

        TextView habitTitleText;
        TextView habitReasonText;
        TextView habitStartDateText;
        TextView habitDaysText;

        // access the habits_list.xml to work with the buttons
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.habits_list, parent, false);
        }

        /*
        Takes user to the comments page when the button is clicked
         */
        commentsButton = view.findViewById(R.id.Comment);
        commentsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toCommentsPage();
            }
        });

        // looking for the "hidden" textviews
        habitTitleText = view.findViewById(R.id.habitTitle_TextView);
        habitReasonText = view.findViewById(R.id.habitReason_TextView);
        habitStartDateText = view.findViewById(R.id.habitStartDate_TextView);
        habitDaysText = view.findViewById(R.id.habitDays_TextView);

        expandArrowButton = view.findViewById(R.id.expandArrow);
        expandArrowButton.setTag(EXPAND_CONSTANT); // starts out as being able to "expand"

        expandArrowButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // if the user clicks on an expanding arrow
                    if (EXPAND_CONSTANT.equals(expandArrowButton.getTag())){

                        // changes arrow to collapse arrow
                        expandArrowButton.setImageResource(R.drawable.ic_collapse_arrow);

                        // when expanded, the parts that were once hidden are now visible
                        habitReasonText.setVisibility(View.VISIBLE);
                        habitStartDateText.setVisibility(View.VISIBLE);
                        habitDaysText.setVisibility(View.VISIBLE);

                        // indicates that the arrow is currently the collapse arrow
                        expandArrowButton.setTag(COLLAPSE_CONSTANT);

                    }

                    // the user clicks on the collapse arrow
                    else if (COLLAPSE_CONSTANT.equals(expandArrowButton.getTag())){

                        expandArrowButton.setImageResource(R.drawable.ic_expand_arrow);

                        habitReasonText.setVisibility(View.GONE);
                        habitStartDateText.setVisibility(View.GONE);
                        habitDaysText.setVisibility(View.GONE);

                        expandArrowButton.setTag(EXPAND_CONSTANT);
                    }

                }
            });

        // what should be displayed when the listview item is NOT expanded in the beginning
        Habit habit = habits.get(position);

        // habitTitleText.setText(habit.getTitle());
        // habitReasonText.setText(habit.getReason());
        // habitDaysText.setText(habit.getDays());
        // habitStartDateText.setText(habit.getStartDate());
        return view;
    }
}
