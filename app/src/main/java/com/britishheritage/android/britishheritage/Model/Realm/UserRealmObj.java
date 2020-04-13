package com.britishheritage.android.britishheritage.Model.Realm;

import com.britishheritage.android.britishheritage.Model.User;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserRealmObj extends RealmObject {

    @PrimaryKey
    private String id;
    private String username;
    private int points;
    private int numOfReviews;
    private int numOfCheckIns;

    public UserRealmObj(){

    }

    public UserRealmObj(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.points = user.getTotalPoints();
        this.numOfCheckIns = user.getNumOfCheckIns();
        this.numOfReviews = user.getNumOfReviews();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getNumOfReviews() {
        return numOfReviews;
    }

    public void setNumOfReviews(int numOfReviews) {
        this.numOfReviews = numOfReviews;
    }

    public int getNumOfCheckIns() {
        return numOfCheckIns;
    }

    public void setNumOfCheckIns(int numOfCheckIns) {
        this.numOfCheckIns = numOfCheckIns;
    }
}
