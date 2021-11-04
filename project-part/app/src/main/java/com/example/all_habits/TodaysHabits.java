package com.example.all_habits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TodaysHabits extends AppCompatActivity {

    ImageView habitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todays_habits);
        Intent intent = getIntent();

        habitButton = findViewById(R.id.allHabits);
        habitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodaysHabits.this, Comments.class);
                startActivity(intent);
            }
        });
    }
}

