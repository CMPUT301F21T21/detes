package com.example.all_habits;

import java.util.ArrayList;

/**
 * Displays User information to them
 */

public class User {

    //initialize
    private String uid;
    private String name;
    private String email;
    private String password;
    private ArrayList<String> requestsList = new ArrayList<String>();
    private ArrayList<String> followers = new ArrayList<String>();
    private ArrayList<String> following = new ArrayList<String>();

    public User() {
    }

    /**
     * Constructor for creating the habit
     *
     * @param uid unique code of the user
     * @param name name of user
     * @param email email of user
     * @param password password of user
     * @param requestsList list of requests from potential followers
     * @param followers lists followers of user
     * @param following lists those user follows
     */
    public User(String uid, String name, String email, String password, ArrayList<String> requestsList, ArrayList<String> followers, ArrayList<String> following) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.requestsList = requestsList;
        this.followers = followers;
        this.following = following;
    }

    public User(String uid, String name, String email, String password) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * gets unique code of user
     * @return
     */
    public String getUid() {
        return uid;
    }

    /**
     * sets unique code of user
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * gets name of user
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * sets name of user
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets email of user
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets email of user
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * gets password of user
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * sets password of user
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * gets the requested followers list
     * @return
     */
    public ArrayList<String> getRequestsList() {
        return requestsList;
    }

    /**
     * sets the requested followers list
     * @param requestsList
     */
    public void setRequestsList(ArrayList<String> requestsList) {
        this.requestsList = requestsList;
    }

    /**
     * gets followers list
     * @return
     */
    public ArrayList<String> getFollowers() {
        return followers;
    }

    /**
     * sets followers list
     * @param followers
     */
    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    /**
     * gets following list
     * @return
     */
    public ArrayList<String> getFollowing() {
        return following;
    }

    /**
     * sets following list
     * @param following
     */
    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }
}