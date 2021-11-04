package com.example.all_habits;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Comments extends AppCompatActivity {
    // id
    // person who wrote it
    // who the person wrote it for
    // which habit the person wrote the comment on

    private Button backButton;
    private ImageView user;

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


        user = findViewById(R.id.User);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Comments.this, DisplayUserProfile.class);
                startActivity(intent);
            }
        });


    }



}
