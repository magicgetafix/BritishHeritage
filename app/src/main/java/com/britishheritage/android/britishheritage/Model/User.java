package com.britishheritage.android.britishheritage.Model;


import com.britishheritage.android.britishheritage.Model.Realm.UserRealmObj;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class User {

    @PropertyName("id")
    public String id = "";
    @PropertyName("uN")
    public String username = "";
    @PropertyName("pts")
    public int totalPoints = 0;
    @PropertyName("rNo")
    public int numOfReviews;
    @PropertyName("cNo")
    public int numOfCheckIns;

    //required by Firebase
    public User(){

    }

    public User(String id, String username, int totalPoints, int numOfReviews, int numOfCheckIns) {
        this.id = id;
        this.username = username;
        this.totalPoints = totalPoints;
        this.numOfReviews = numOfReviews;
        this.numOfCheckIns = numOfCheckIns;
    }

    public User(UserRealmObj userRealmObj){
        this.id = userRealmObj.getId();
        this.username = userRealmObj.getUsername();
        this.totalPoints = userRealmObj.getPoints();
        this.numOfCheckIns = userRealmObj.getNumOfCheckIns();
        this.numOfReviews = userRealmObj.getNumOfReviews();
    }

    @Exclude
    public String getId() {
        return id;
    }
    @Exclude
    public void setId(String id) {
        this.id = id;
    }
    @Exclude
    public String getUsername() {
        return username;
    }
    @Exclude
    public void setUsername(String username) {
        this.username = username;
    }
    @Exclude
    public int getTotalPoints() {
        return totalPoints;
    }
    @Exclude
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
    @Exclude
    public int getNumOfReviews() { return numOfReviews; }
    @Exclude
    public void setNumOfReviews(int numOfReviews) { this.numOfReviews = numOfReviews; }
    @Exclude
    public int getNumOfCheckIns() { return numOfCheckIns; }
    @Exclude
    public void setNumOfCheckIns(int numOfCheckIns) { this.numOfCheckIns = numOfCheckIns; }
}
