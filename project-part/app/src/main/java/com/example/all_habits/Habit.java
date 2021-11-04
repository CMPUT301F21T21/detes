package com.example.all_habits;

import org.w3c.dom.Comment;

import java.util.Date;

public class Habit {

    private String habitName;
    private String reason;
    private String habitDays;
    private String startDate;

    Habit(String habitName, String reason, String habitDays, String startDate){
        this.habitName= habitName;
        this.reason = reason;
        this.habitDays = habitDays;
        this.startDate = startDate;

    }

    public Habit() {

    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getHabitDays() {
        return habitDays;
    }

    public void setHabitDays(String habitDays) {
        this.habitDays = habitDays;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
