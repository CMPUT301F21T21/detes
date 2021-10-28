package com.example.all_habits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

//List to display listView.
public class otherUsersList extends ArrayAdapter<User> {
    private ArrayList<User> userList;
    private Context context;

    public otherUsersList(Context context, ArrayList<User> userList){
        super(context,0,userList);
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.other_users_content,parent,false);
        }

        User user = userList.get(position);
        TextView userView = view.findViewById(R.id.otherUserName);

        userView.setText(user.getName());
        return view;
    }

}
