package com.example.all_habits;

import org.w3c.dom.Comment;

import java.util.Date;

public class Habit {

    private String title;
    private String reason;
    private String[] days; // eg. [M,W,F]
    private Date startDate;
    private String location;
    private Comment[] comments;

    Habit(String title){
        this.title = title;

    }

    public void setTitle(String inputTitle){
        this.title = inputTitle;
    }

    public String getTitle(){
        return this.title;
    }


}
