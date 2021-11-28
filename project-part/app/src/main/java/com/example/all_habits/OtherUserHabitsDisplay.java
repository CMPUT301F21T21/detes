package com.example.all_habits;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OtherUserHabitsDisplay extends AppCompatActivity {

    ListView habitsListView;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitArrayList;

    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private String otherUserID = "RxddmtjCMUZsg84B6Z5OJwgR9f82";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_habits_display);

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference otherUserCollectionReference = db.collection(otherUserID.toString());

        habitsListView = findViewById(R.id.habits_list);
        habitArrayList = new ArrayList<>();

        habitAdapter = new OtherUserHabitsList(this, habitArrayList);
        habitsListView.setAdapter(habitAdapter); //converts data source to ListView

        otherUserCollectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Habit habit = document.toObject(Habit.class);
                                habitArrayList.add(habit);
                            }
                        }
                        habitAdapter.notifyDataSetChanged();
                    }
                });


    }
}
