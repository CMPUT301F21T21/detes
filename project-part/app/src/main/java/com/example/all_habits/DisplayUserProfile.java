package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Profile page that is opened on the MainActivity with the button on the top right.
 */
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
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
                System.exit(0);
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
