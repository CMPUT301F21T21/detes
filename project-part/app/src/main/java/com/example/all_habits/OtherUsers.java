package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class OtherUsers extends AppCompatActivity {
    private ListView otherUsersList;
    private ArrayList userNames;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users);
        otherUsersList = findViewById(R.id.otherUsersList);



    }
}