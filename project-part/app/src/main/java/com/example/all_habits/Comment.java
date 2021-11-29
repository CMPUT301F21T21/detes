package com.example.all_habits;

/**
 * Creates a Comment object
 */
public class Comment {

    //initialize
    String userId;
    String habitId;
    String commenterId;
    int commentNum;
    String commentString;

    /**

     * Constructor for the Comment class
     * @param userId
     * @param habitId
     * @param commentNum the number associated with the comment
     * @param commenterId
     * @param commentString contains the content of the comment
     */
     
    public Comment(String userId, String habitId, int commentNum, String commenterId, String commentString) {
        this.userId = userId;
        this.habitId = habitId;
        this.commenterId = commenterId;
        this.commentNum = commentNum;
        this.commentString = commentString;
    }

    /**
     * No argument constructor for Comment
     * Test values
     */
    public Comment(){
        this.commentString = "Test";
        commentNum = 1;
    }

    /**
     * Argument constructor for Comment
     * @param commentString the content of the comment
     * @param commentNum the number associated with the comment
     */
    public Comment(String commentString, int commentNum){
        this.commentString = commentString;
        this.commentNum = commentNum;

    }

    /**
     * Gets the user ID
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the habit ID
     * @return the habit ID
     */
    public String getHabitId() {
        return habitId;
    }

    /**
     * Sets the habit ID
     * @param habitId
     */
    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    /**
     * Gets the number associated with the comment
     * @return comment number
     */
    public int getCommentNum() {
        return commentNum;
    }

    /**
     * Sets the number associated with the comment
     * @param commentNum
     */
    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    /**
     * Gets the ID of the person who commented
     * @return ID of commenter
     */
    public String getCommenterId() {
        return commenterId;
    }

    /**
     * Sets the commenter ID
     * @param commenterId
     */
    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    /**
     * Gets the string of the comment
     * @return the comment as a string
     */
    public String getCommentString() {
        return commentString;
    }

    /**
     * Sets the string of the comment
     * @param commentString
     */
    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

}
