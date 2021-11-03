package com.example.all_habits;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Comments extends AppCompatActivity {

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.view.View;


public class Comments extends AppCompatActivity {
    // id
    // person who wrote it
    // who the person wrote it for
    // which habit the person wrote the comment on

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        Intent intent = getIntent();

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish(); // goes back to the all habits page
            }
        });


    }
}

    //Display User profile once clicked
    public void DisplayProfile(View view) {
        // Do something in response to user button
        Intent intent = new Intent(this, DisplayUserProfile.class);
        startActivity(intent);
    }
}
