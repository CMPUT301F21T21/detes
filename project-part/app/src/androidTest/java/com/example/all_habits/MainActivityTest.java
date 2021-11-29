package com.example.all_habits;

import static android.app.PendingIntent.getActivity;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.robotium.solo.Solo;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {

    // object we use to interact with the phone
    private Solo solo;

    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);


    @Before
    public void setUp() throws Exception{
        // initalize solo
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkToday(){
        solo.enterText((EditText) solo.getView(R.id.Email), "dummyuser@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "dummyuser");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        View view = solo.getView("addButton");
        solo.clickOnView(view);
        solo.assertCurrentActivity("Wrong Activity", Create.class);
        solo.waitForActivity("Create");
        solo.enterText((EditText) solo.getView(R.id.habitName), "Eating");
        solo.enterText((EditText) solo.getView(R.id.habitReason), "To satisfy hunger");
        solo.enterText((EditText) solo.getView(R.id.habitTitle_TextView), "Eating");

    }

    @Test
    public void expandHabit(){
        solo.enterText((EditText) solo.getView(R.id.Email), "exuu@ualberta.ca");
        solo.enterText((EditText) solo.getView(R.id.password), "exuuexuu");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.waitForActivity("MainActivity");
        View expandArrow = solo.getView(R.id.expandArrow);
        solo.clickOnView(expandArrow);
    }

    @Test
    //Don't Touch Yet
    public void deleteHabit(){

    }

    @Test
    //Don't Touch Yet
    public void editHabit(){

    }

    @Test
    public void habitTitleLength(){

    }

    @Test
    //Don't Touch Yet
    public void reorderHabit(){

    }

    @Test
    public void createHabitEvent(){

    }

    @Test
    public void comment(){

    }

    @Test
    public void optionalPhoto(){

    }

    @Test
    public void optionalGallery(){

    }

    @Test
    public void viewHabitEvent(){

    }

    @Test
    public void deleteHabitEvent(){

    }

    @Test
    public void checkLocation(){

    }

    @Test
    public void changeLocation(){

    }

    @Test
    public void followUser(){

    }

    @Test
    public void acceptRequest(){

    }

    @Test
    public void declineRequest(){

    }


}