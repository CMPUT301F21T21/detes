package com.example.all_habits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OtherUserHabitsList extends ArrayAdapter<Habit> {

    private ArrayList<Habit> habits;
    private Context context;
    private View view;

    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private DocumentReference documentRef;
    private String habitId; // to keep track of which habit was selected
    private String todayDateString;

    // values will not be changed
    private final String EXPAND_CONSTANT = "EXPAND";
    private final String COLLAPSE_CONSTANT = "COLLAPSE";

    /**
     * Constructor for the HabitsList
     *
     * @param context Activity context
     * @param habits  an ArrayList that contains Habit objects
     */
    public OtherUserHabitsList(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }

    /**
     * Handles what happens in each listview item (what is displayed,
     * the actions of button clicks, etc)
     *
     * @param position    which listview item was clicked on
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
        Button habitCompleteButton;

        TextView habitTitleText;
        TextView habitReasonText;
        TextView habitStartDateText;
        TextView habitDaysText;
        TextView habitEndDateText;

        Date todayDate;
        SimpleDateFormat dateFormat;
        Calendar cal;

        // access the habits_list.xml to work with the buttons
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.habits_list, parent, false);
        }


        Habit habit = habits.get(position); // gets the selected habit

        habitCompleteButton = view.findViewById(R.id.habitCompleteButton);

        // looking for the "hidden" textviews and image buttons
        habitTitleText = view.findViewById(R.id.habitTitle_TextView);
        habitReasonText = view.findViewById(R.id.habitReason_TextView);
        habitStartDateText = view.findViewById(R.id.habitStartDate_TextView);
        habitDaysText = view.findViewById(R.id.habitDays_TextView);
        habitEndDateText = view.findViewById(R.id.habitEndDate_TextView);

        expandArrowButton = view.findViewById(R.id.expandArrow);
        expandArrowButton.setTag(EXPAND_CONSTANT); // starts out as being able to "expand"

        expandArrowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // if the user clicks on an expanding arrow
                if (EXPAND_CONSTANT.equals(expandArrowButton.getTag())) {

                    // changes arrow to collapse arrow
                    expandArrowButton.setImageResource(R.drawable.ic_collapse_arrow);

                    // when expanded, the parts that were once hidden are now visible
                    habitReasonText.setVisibility(View.VISIBLE);
                    habitStartDateText.setVisibility(View.VISIBLE);
                    habitDaysText.setVisibility(View.VISIBLE);
                    habitEndDateText.setVisibility(View.VISIBLE);

                    // indicates that the arrow is currently the collapse arrow
                    expandArrowButton.setTag(COLLAPSE_CONSTANT);

                }

                // the user clicks on the collapse arrow
                else if (COLLAPSE_CONSTANT.equals(expandArrowButton.getTag())) {

                    expandArrowButton.setImageResource(R.drawable.ic_expand_arrow);

                    habitReasonText.setVisibility(View.GONE);
                    habitStartDateText.setVisibility(View.GONE);
                    habitDaysText.setVisibility(View.GONE);
                    habitEndDateText.setVisibility(View.GONE);

                    expandArrowButton.setTag(EXPAND_CONSTANT);
                }

            }
        });

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        // what should be displayed when the listview item is NOT expanded in the beginning

        ArrayList<String> habitDays = habit.getHabitDays(); // when this habit will be done
        ArrayList<String> completedDaysList = habit.getCompletedDaysList(); // which days of the week has the habit been completed on
        String habitDayString = "";

        habitTitleText.setText(habit.getHabitName());
        habitReasonText.setText("Reason: " + habit.getReason());

        //Creates a string with all the habit days selected, seperated by a ','.
        if (habitDays != null){
            for(int i = 0; i < habitDays.size();i++){
                habitDayString += habitDays.get(i);
                if(i != habitDays.size() - 1) {
                    habitDayString = habitDayString + ", ";
                }
            }}
        habitDaysText.setText("Days: " + habitDayString);

        habitStartDateText.setText("Start Date: " + habit.getStartDate());
        habitEndDateText.setText("End Date: " + habit.getEndDate());



        // displays the current progress percentage of the habit
        TextView completedPercent_TextView = view.findViewById(R.id.completed_percent);
        completedPercent_TextView.setText(Integer.toString(habit.getProgress()) + '%');

        ProgressBar progressBar = view.findViewById(R.id.completionProgressBar);
        Resources resources = context.getResources();
        Drawable drawableProgress = resources.getDrawable(R.drawable.completion_progress_bar);

        // displays the progress in the progress bar
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setProgress(habit.getProgress());
        progressBar.setProgressDrawable(drawableProgress);

        SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE", Locale.US); //get the full name of the weekday
        cal = Calendar.getInstance();
        todayDate = cal.getTime(); // gets today's date

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setProgress(habit.getProgress());
        progressBar.setProgressDrawable(drawableProgress);

        return view;
    }
}

