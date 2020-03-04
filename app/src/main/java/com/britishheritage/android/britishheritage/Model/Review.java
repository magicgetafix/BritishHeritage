package com.britishheritage.android.britishheritage.Model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import java.util.Date;

public class Review {

    private String txt;
    private String uN;
    private int pts;
    private long tS;
    private String uId;
    private String rId;
    @Exclude
    private boolean pH;


    //required empty constructor
    public Review(){

    }

    public Review(boolean isPlaceHolder){
        pH = isPlaceHolder;
    }

    public Review(String txt, FirebaseUser currentUser, Landmark landmark) {
        this.tS = new Date().getTime();
        this.txt = txt;
        this.uN = currentUser.getDisplayName();
        this.pts = 1;
        this.uId = currentUser.getUid();
        this.rId = landmark.getId() + this.uId;
    }

    public String getText() {
        return txt;
    }

    public void setText(String text) {
        this.txt = text;
    }

    public String getUsername() {
        return uN;
    }

    public void setUsername(String username) {
        this.uN = username;
    }

    public int getUpvotes() {
        return pts;
    }

    public void addUpvote() {
        this.pts = pts + 1;
    }

    public void downVote(){
        this.pts = pts - 1;
    }

    public long getTimeStamp() {
        return tS;
    }

    public void setTimeStamp(int timeStamp) {
        this.tS = timeStamp;
    }

    public String getUserId() {
        return uId;
    }

    public void setUserId(String userId) {
        this.uId = userId;
    }

    public String getReviewId(){
        return rId;
    }

    public void setReviewId(String reviewId){
        this.rId = reviewId;
    }

    @Exclude
    public boolean isPlaceholder(){
        return pH;
    }

    @Exclude
    public void setAsPlaceholder(boolean isPlaceholder){
        this.pH = isPlaceholder;
    }

}
