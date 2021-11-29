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

/**
 * Determines what each listview item will look like, when displayed
 * on the all habits page (see storyboard sequence)
 *
 */

public class HabitsList extends ArrayAdapter<Habit> {

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
    private final String CHECKED_CONSTANT = "CHECKED";
    private final String UNCHECKED_CONSTANT = "UNCHECKED";

    /**
     * Constructor for the HabitsList
     *
     * @param context Activity context
     * @param habits  an ArrayList that contains Habit objects
     */
    public HabitsList(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }

    /**
     * When the comment button is clicked, we move to the Comments page
     */
    public void toCommentsPage(int habitNum) {
        Intent intent = new Intent(context, Comments.class);
        intent.putExtra("habitNum", habitNum);
        context.startActivity(intent);

    }

    /**
     * Based on the habit days, start date, and end date, all the days in which the habit will be done is returned
     * @param startDate start date of the habit
     * @param endDate end date of the habit
     * @param habitDays days of the week in which the habit will occur
     * @return an ArrayList of strings, where these strings are dates in the form of dd/MM/yyyy
     */
    public ArrayList<String> getAllDaysForHabit(String startDate, String endDate, ArrayList<String> habitDays) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE", Locale.US); //get the full name of the weekday
        ArrayList<String> allDaysForHabit = new ArrayList<>();

        try {
            Date startDateObject = dateFormat.parse(startDate); // change date to a Date object

            if (endDate != null) {
                Date endDateObject = dateFormat.parse(endDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(startDateObject); // set the date to the start date

                // gets the date for the start of the week
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                Date startOfWeek = cal.getTime();

                ArrayList<Date> daysOfFirstWeek = new ArrayList<>();

                // Gets us an ArrayList of all the dates of the first week
                for (int i = 0; i < 7; i++) {
                    daysOfFirstWeek.add(startOfWeek);
                    cal.add(Calendar.DAY_OF_MONTH, 1); // increase the day by 1
                    startOfWeek = cal.getTime();
                }

                // getting all the days for the habit by checking if it's one of the habit days
                for (int i = 0; i < daysOfFirstWeek.size(); i++) {
                    String weekDay = weekDayFormat.format(daysOfFirstWeek.get(i)); // weekday for that date

                    for (int j = 0; j < habitDays.size(); j++) {
                        // if the weekday is a habit day
                        if (weekDay.contains(habitDays.get(j))) {
                            cal.setTime(daysOfFirstWeek.get(i));

                            // checks if the date happens after but not including the start date and is not equal to the end date
                            if (cal.getTime().after(startDateObject) && cal.getTime().before(endDateObject)) {
                                allDaysForHabit.add(dateFormat.format(cal.getTime()));
                            }

                            // keep getting the habit days as long as it's before the end date
                            while (cal.getTime().before(endDateObject)) {
                                cal.add(Calendar.DAY_OF_MONTH, 7);
                                if (cal.getTime().before(endDateObject)) {
                                    allDaysForHabit.add(dateFormat.format(cal.getTime()));
                                }
                            }
                        }

                        // checks if the start date itself needs to be included as one of the dates where the habit will occur
                        if (weekDayFormat.format(startDateObject).contains(habitDays.get(j)) && !allDaysForHabit.contains(startDate)) {
                            allDaysForHabit.add(dateFormat.format(startDateObject));
                        }

                        // checks if the end date itself needs to be included as one of the dates where the habit will occur
                        if (weekDayFormat.format(endDateObject).contains(habitDays.get(j)) && !allDaysForHabit.contains(endDate)) {
                            allDaysForHabit.add(dateFormat.format(endDateObject));
                        }
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return allDaysForHabit;
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

        /*
        Takes user to the comments page when the button is clicked
         */
        commentsButton = view.findViewById(R.id.Comment);
        commentsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toCommentsPage(position + 1);
            }
        });

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

        CheckBox checkBox = view.findViewById(R.id.completed_habit_check);
        checkBox.setTag(UNCHECKED_CONSTANT);

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

        // if the user has checked the box for today already, leave it as checked
        if (completedDaysList.contains(dateFormat.format(todayDate)) && habit.getProgress() != 0){
            checkBox.setChecked(true);
            checkBox.setTag(CHECKED_CONSTANT);
        }
        else{
            checkBox.setChecked(false);
            checkBox.setTag(UNCHECKED_CONSTANT);
        }

        if (habit.getProgress() == 100){
            checkBox.setChecked(true);
            checkBox.setTag(CHECKED_CONSTANT);
        }

        if (checkBox.getTag().equals(CHECKED_CONSTANT)){
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }



        ArrayList<String> allDaysForHabit = new ArrayList<>();
        allDaysForHabit = getAllDaysForHabit(habit.getStartDate(), habit.getEndDate(), habitDays);

        // update the total days list
        habit.setTotalDaysList(allDaysForHabit);

        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // finds the specific habit
                Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", position + 1).limit(1);
                findHabit.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        habitId = document.getId();
                                    }
                                }

                                todayDateString = dateFormat.format(todayDate);

                                if (checkBox.isChecked()) {

                                    // if today's date is when the habit should be happening, and it's not marked as as completed day yet
                                    if (habit.getTotalDaysList().contains(todayDateString) && !completedDaysList.contains(todayDateString)){
                                        completedDaysList.add(todayDateString);
                                        habit.setProgress(); // updates the progress
                                        checkBox.setTag(CHECKED_CONSTANT);


                                        if (habit.getProgress() == 100){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder
                                                    .setTitle("Habit Complete!")
                                                    .setMessage("This habit is now 100% complete! Do you want to continue to the habit events?")
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // If the user clicks yes
                                                            Intent intent = new Intent(context, HabitEvents.class);
                                                            intent.putExtra("habitNum", position + 1);
                                                            context.startActivity(intent);

                                                        }
                                                    })
                                                    .setNegativeButton("No", null)
                                                    .show();
                                        }
                                    }
                                }

                                else if (!checkBox.isChecked()) {
                                    if (habit.getTotalDaysList().contains(todayDateString) && completedDaysList.contains(todayDateString)){
                                        completedDaysList.remove(todayDateString);
                                        habit.setProgress(); // update the progress
                                    }
                                }

                                // make updates to firebase
                                if (habitId != null) {
                                    documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                                    documentRef.update("totalDaysList", habit.getTotalDaysList());
                                    documentRef.update("completedDaysList", habit.getCompletedDaysList());
                                    documentRef.update("progress", habit.getProgress());

                                }
                            }
                        });
            }
        });

        // if the user finishes the habit early
        habitCompleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, HabitEvents.class);
                intent.putExtra("habitNum", position + 1);

                if (habit.getProgress() != 100) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setTitle("Complete Habit Early")
                            .setMessage("This habit will be marked as 100% complete. Do you want to continue?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // If the user clicks yes
                                    habit.setProgress(100);
                                    checkBox.setChecked(true);
                                    Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", position + 1).limit(1);
                                    findHabit.get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            habitId = document.getId();
                                                        }
                                                    }

                                                    // make updates to firebase
                                                    if (habitId != null) {
                                                        documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                                                        documentRef.update("progress", habit.getProgress());
                                                        habit.setEndDate(todayDateString);
                                                        documentRef.update("endDate", habit.getEndDate());

                                                        ArrayList<String> totalDaysList = habit.getTotalDaysList();
                                                        for (int i = 0; i < totalDaysList.size(); i++){
                                                            try {
                                                                Date endDateObject = dateFormat.parse(habit.getEndDate());
                                                                if (dateFormat.parse(totalDaysList.get(i)).after(endDateObject)) {
                                                                    habit.removefromTotalDaysList(totalDaysList.get(i));
                                                                }
                                                            }
                                                            catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        documentRef.update("totalDaysList", habit.getTotalDaysList());

                                                        habit.addToCompletedDaysList(todayDateString);
                                                        documentRef.update("completedDaysList", habit.getCompletedDaysList());

                                                        context.startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                // if the user has clicked on completing the habit before (message doesn't need to pop up again)
                else{
                    context.startActivity(intent);
                }
            }
        });

        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setProgress(habit.getProgress());
        progressBar.setProgressDrawable(drawableProgress);

        return view;
    }
}
