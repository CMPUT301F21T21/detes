package com.example.all_habits;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Date;

/**
 * Creates a Habit
 * @author Elena
 * @Version 1.0
 */
public class Habit {

    private String habitName;
    private String reason;
    private ArrayList<String> habitDays;
    private String startDate;
    private int habitNum;
    private Boolean Private;

    /**
     * Constructor for creating the habit
     *
     * @param habitName name of the habit
     * @param reason reason for the habit
     * @param habitDays days in which the habit occurs
     * @param startDate the start date of the habit
     * @param habitNum the number of the habit
     * @param Private whether the habit is public or private
     */
    Habit(String habitName, String reason, ArrayList<String> habitDays, String startDate,
          int habitNum, Boolean Private){

        this.habitName= habitName;
        this.reason = reason;
        this.habitDays = habitDays;
        this.startDate = startDate;
        this.habitNum = habitNum;
        this.Private = Private;

    }

    /**
     * Constructor for creating the habits (with no arguments)
     */
    public Habit() {

    }

    /**
    Gets the name of the habit
     @return the name of the habit
     */
    public String getHabitName() {
        return habitName;
    }

    /**
     Sets the name of the habit

     @param habitName name of the habit
     */
    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    /**
     Gets the reason for the habit

     @return the reason for the habit
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the habit
     * @param reason the reason for the habit
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     *  Gets the habit days
     * @return the days of the week in which the habit will occur
     */
    public ArrayList<String> getHabitDays() {
        return habitDays;
    }

    /**
     * Set the habit days
     * @param habitDays the days of the week in which the habit will occur
     */
    public void setHabitDays(ArrayList<String> habitDays) {
        this.habitDays = habitDays;

    }

    /**
     * Gets the start date of the habit
     * @return the start date of the habit
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Set the start date of the habit
     * @param startDate the start date of the habit
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the habit number
     * @return the number associated with the habit (eg. habit 1)
     */
    public int getHabitNum() {
        return habitNum;
    }

    /**
     * Sets the habit number
     * @param habitNum the numebr associated with the habit
     */
    public void setHabitNum(int habitNum) {
        this.habitNum = habitNum;
    }

    /**
     * Returns if the habit is marked as private or public
     * @return true if the habit is marked as private. False otherwise.
     */
    public Boolean getPrivate() {
        return Private;
    }

    /**
     * Sets the habit as private or public
     * @param Private
     */
    public void setPrivate(Boolean Private) {
        Private = Private;
    }
}
