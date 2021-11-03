package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.view.View;


public class Comments extends AppCompatActivity {
    // id
    // person who wrote it
    // who the person wrote it for
    // which habit the person wrote the comment on


    //Display User profile once clicked
    public void DisplayProfile(View view) {
        // Do something in response to user button
        Intent intent = new Intent(this, DisplayUserProfile.class);
        startActivity(intent);
    }
}