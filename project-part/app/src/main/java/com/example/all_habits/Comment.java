package com.example.all_habits;


/**
 * Creates a Comment for the CommentsList
 * @Version 1.0
 */
public class Comment {
    String userId;
    String habitId;
    String commenterId;
    int commentNum;
    String commentString;

    /**
     * Constructor for creating the habit
     *
     * @param userId
     * @param habitId
     * @param commentNum
     * @param commenterId
     * @param commentString
     */

    public Comment(String userId, String habitId, int commentNum, String commenterId, String commentString) {
        this.userId = userId;
        this.habitId = habitId;
        this.commenterId = commenterId;
        this.commentNum = commentNum;
        this.commentString = commentString;
    }

    public Comment(){
        this.commentString = "Test";
        commentNum = 1;
    }

    public Comment(String commentString, int commentNum){
        this.commentString = commentString;
        this.commentNum = commentNum;

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHabitId() {
        return habitId;
    }

    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

}
