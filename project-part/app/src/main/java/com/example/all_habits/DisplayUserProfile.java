package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayUserProfile extends AppCompatActivity {
    TextView mlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayuserprofile);

        mlogout   = findViewById(R.id.logoutText);

        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


    }

    public void onClick(View v) {

        // TODO:
        // This function closes Activity Two
        // Hint: use Context's finish() method
        finish();
    }



}
