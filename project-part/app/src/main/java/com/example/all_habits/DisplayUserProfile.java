package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Profile page that is opened on the MainActivity with the button on the top right.
 */

public class DisplayUserProfile extends AppCompatActivity {
    //initialize
    Button followersButton;
    Button followingButton;
    Button logOutButton;

    ImageView backButton;

    TextView greetingTextView;
    TextView emailTextView;
    TextView uidTextView;
    private TextView usernameTextView;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayuserprofile);


        //initialize
        backButton = findViewById(R.id.displayBackButton);

        logOutButton = findViewById(R.id.logoutText);
        followersButton = findViewById(R.id.followerButton);
        followingButton = findViewById(R.id.followingButton);

        greetingTextView = findViewById(R.id.Greeting);
        usernameTextView = findViewById(R.id.Username);

        emailTextView = findViewById(R.id.Email);
        uidTextView = findViewById(R.id.UserID);
        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(uid);

        //shows your profile
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usernameTextView.setText(snapshot.getValue().toString());
                emailTextView.setText(user.getEmail());
                uidTextView.setText(uid);

                String userName = snapshot.getValue().toString();
                greetingTextView.setText("Hello, " + userName + "!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //returns you back to previous page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //logout of app
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        //follow another user
        followersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayUserProfile.this, Followers.class);
                startActivity(intent);
            }
        });

        //following another user - access to their profile
        followingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayUserProfile.this, Following.class);
                startActivity(intent);
            }
        });

    }
}

