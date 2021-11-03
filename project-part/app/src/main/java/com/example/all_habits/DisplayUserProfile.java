package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class DisplayUserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayuserprofile);
    }

    public void onClick(View v) {

        // TODO:
        // This function closes Activity Two
        // Hint: use Context's finish() method
        finish();
    }
}
