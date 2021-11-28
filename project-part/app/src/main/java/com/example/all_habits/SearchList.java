package com.example.all_habits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchList extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;
    private View view;
    private TextView searchedName;

    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    public SearchList(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        view = convertView;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.search_list,parent,false);
        }

        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference userRef = db.collection("Users");
        searchedName = view.findViewById(R.id.searchedNameList);
        searchedName.setText(users.get(position).getName().toString() + "\n" + users.get(position).getUid());


        return view;
    }
}
