package com.britishheritage.android.britishheritage.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Review {

    private String txt;
    private String uN;
    private int pnts;
    private long tS;
    private String uId;
    private String rId;
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
        this.pnts = 1;
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
        return pnts;
    }

    public void addUpvote() {
        this.pnts = pnts + 1;
    }

    public void downVote(){
        this.pnts = pnts - 1;
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

    public boolean isPlaceholder(){
        return pH;
    }

    public void setAsPlaceholder(boolean isPlaceholder){
        this.pH = isPlaceholder;
    }

}
