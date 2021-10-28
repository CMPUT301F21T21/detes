package com.example.all_habits;
//Our User tentative.
public class User {
    String name;

    public User() {
        this.name = "Test";
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
