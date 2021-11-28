package com.example.all_habits;

import java.util.ArrayList;

public class User {
    private String uid;
    private String name;
    private String email;
    private String password;
    private ArrayList<String> requestsList = new ArrayList<String>();
    private ArrayList<String> followers = new ArrayList<String>();

    public User() {
    }

    public User(String uid, String name, String email, String password, ArrayList requestsList, ArrayList followers) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.requestsList = requestsList;
        this.followers = followers;
    }

    public User(String uid, String name, String email, String password) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getRequestsList() {
        return requestsList;
    }

    public void setRequestsList(ArrayList<String> requestsList) {
        this.requestsList = requestsList;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }
}