package com.example.all_habits;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OtherUsers extends AppCompatActivity {
    private ListView otherUsersList;
    private ArrayList userDataList;
    private ArrayAdapter<User> userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users);
        otherUsersList = findViewById(R.id.otherUsersList);
        userDataList = new ArrayList();

        //Test Values until User class array is created.
        User user = new User("TestGuy");
        User user2 = new User("TestGirl");
        userDataList.add(user);
        userDataList.add(user2);

        userListAdapter = new otherUsersList(this,userDataList);
        otherUsersList.setAdapter(userListAdapter);




    }
}