package com.britishheritage.android.britishheritage.Model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Date;

public class Review {

    @PropertyName("txt")
    public String txt;
    @PropertyName("rT")
    public String rt;
    @PropertyName("uN")
    public String uN;
    @PropertyName("pts")
    public long pts;
    @PropertyName("tS")
    public long tS;
    @PropertyName("uId")
    public String uId;
    @PropertyName("rId")
    public String rId;
    @Exclude
    private boolean pH;


    //required empty constructor
    public Review(){

    }

    public Review(boolean isPlaceHolder){
        pH = isPlaceHolder;
    }

    /**
     *
     * @param txt
     * @param reviewTitle
     * @param username
     * @param userId
     * @param landmark
     */
    public Review(String txt, String reviewTitle, String username, String userId, Landmark landmark) {
        this.tS = new Date().getTime();
        this.rt = reviewTitle;
        this.txt = txt;
        this.uN = username;
        this.pts = 1;
        this.uId = userId;
        this.rId = landmark.getId() + this.uId;
    }

    @Exclude
    public String getText() {
        return txt;
    }

    @Exclude
    public void setText(String text) {
        this.txt = text;
    }

    @Exclude
    public String getUsername() {
        return uN;
    }

    @Exclude
    public void setUsername(String username) {
        this.uN = username;
    }

    @Exclude
    public long getUpvotes() {
        return pts;
    }

    @Exclude
    public void setUpvotes(int upvotes){
        this.pts = upvotes;
    }

    @Exclude
    public void addUpvote() {
        this.pts = pts + 1;
    }

    @Exclude
    public void downVote(){
        this.pts = pts - 1;
    }

    @Exclude
    public long getTimeStamp() {
        return tS;
    }

    @Exclude
    public void setTimeStamp(long timeStamp) {
        this.tS = timeStamp;
    }

    @Exclude
    public String getUserId() {
        return uId;
    }

    @Exclude
    public void setUserId(String userId) {
        this.uId = userId;
    }

    @Exclude
    public String getReviewId(){
        return rId;
    }

    @Exclude
    public void setReviewId(String reviewId){
        this.rId = reviewId;
    }

    @Exclude
    public void setReviewTitle(String reviewTitle){
        this.rt = reviewTitle;
    }

    @Exclude
    public String getReviewTitle(){
        return this.rt;
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
