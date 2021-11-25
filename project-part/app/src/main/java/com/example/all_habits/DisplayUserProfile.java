package com.example.all_habits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
    TextView mlogout, emailTextView, uidTextView;
    private TextView userName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayuserprofile);

        mlogout   = findViewById(R.id.logoutText);
        userName  = findViewById(R.id.Username);
        emailTextView = findViewById(R.id.Email);
        uidTextView = findViewById(R.id.User_ID);
        String uid = user.getUid();

        emailTextView.setText(user.getEmail());
        uidTextView.setText(uid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(uid);

        //userName.setText(ref.get().getResult().getValue().toString());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


        // TODO:
        // This function closes UserProfile
        finish();
    }


}
