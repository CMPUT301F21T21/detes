package com.example.all_habits;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Comments extends AppCompatActivity {
    // id
    // person who wrote it
    // who the person wrote it for
    // which habit the person wrote the comment on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdelete);
        Intent intent = getIntent();

    }
}
