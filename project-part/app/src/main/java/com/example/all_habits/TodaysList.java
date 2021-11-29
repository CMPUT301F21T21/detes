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

public class TodaysList extends ArrayAdapter<Habit> {

    private ArrayList<Habit> habits;
    private Context context;
    private View view;
    private Boolean isTodaysList;

    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private DocumentReference documentRef;
    private String habitId; // to keep track of which habit was selected

    /**
     * Constructor for the TodaysList
     *
     * @param context Activity context
     * @param habits  an ArrayList that contains Habit objects
     */
    public TodaysList(Context context, ArrayList<Habit> habits, Boolean isTodaysList) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
        this.isTodaysList = isTodaysList;
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

        TextView habitTitleText;
        Date todayDate;
        SimpleDateFormat dateFormat;
        Calendar cal;


        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.todays_list, parent, false);
        }

        Habit habit = habits.get(position); // gets the selected habit


        habitTitleText = view.findViewById(R.id.habitTitle_TextView);
        habitTitleText.setText(habit.getHabitName());
;
        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        ArrayList<String> habitDays = habit.getHabitDays(); // when this habit will be done
        ArrayList<String> completedDaysList = habit.getCompletedDaysList(); // which days of the week has the habit been completed on

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


