package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Lists today's habits
 * @author Emma
 */

public class TodaysHabits extends AppCompatActivity {

    ImageView habitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todays_habits);


        habitButton = findViewById(R.id.allHabits);
        habitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodaysHabits.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
