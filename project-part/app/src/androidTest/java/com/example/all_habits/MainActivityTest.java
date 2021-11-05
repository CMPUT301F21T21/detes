package com.example.all_habits;

import static android.app.PendingIntent.getActivity;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {

    // object we use to interact with the phone
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        // initalize solo
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }


    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }



    /**
     * Checks if the listview items is working properly (can be clicked)
     * @author Elena
     */
    @Test
    public void checkListView(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.waitForActivity("MainActivity");

        solo.clickInList(1); // clicks on the first listview item

        // clicks on the CANCEL button
        Button cancelButton = (Button) solo.getView("cancelButton");
        solo.clickOnView(cancelButton);


        View expandArrow = solo.getView(R.id.expandArrow);
        solo.clickOnView(expandArrow);

    }

    /**
     * @author Emma
     */
    @Test
    public void checkHomeButton(){
        //checks that home button is clickable and works
        View homeButton = solo.getView(R.id.homeButton);
        solo.clickOnView(homeButton);
        solo.assertCurrentActivity(String.valueOf(this), TodaysHabits.class);
    }

    @Test
    public void checkAllHabitsButton(){
        //checks All habits button is clickable and works
        checkHomeButton();
        View allHabitsButton = solo.getView(R.id.allHabits);
        solo.clickOnView(allHabitsButton);
        solo.assertCurrentActivity(String.valueOf(this), MainActivity.class);
    }

    @Test
    public void checkComments(){
        //checks comment is empty message pops up when empty
        View commentsButton = solo.getView(R.id.Comment);
        solo.clickOnView(commentsButton);
        solo.assertCurrentActivity(String.valueOf(this), Comments.class);

    }



    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

    /**
     * Checks that all the buttons work and in the right activity
     * @author Diana
     */
    @Test
    public void checkCreateButton(){
        //checks create button is clickable and works
        View createButton = solo.getView(R.id.createButton);
        solo.clickOnView(createButton);
    }

    @Test
    public void checkDeleteButton(){
        //checks delete button is clickable and works
        View deleteButton = solo.getView(R.id.deleteComment);
        solo.clickOnView(deleteButton);
    }

    @Test
    public void checkCommentsButton(){
        //checks comment button is clickable and works
        View commentButton = solo.getView(R.id.commentButton);
        solo.clickOnView(commentButton);
    }
}
