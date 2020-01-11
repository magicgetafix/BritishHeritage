package com.britishheritage.android.britishheritage.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavouriteLandmarkRealmObj extends RealmObject {

    @PrimaryKey
    private String id = "";
    private Double latitude;
    private Double longitude;
    private String name;
    private String type;

    public FavouriteLandmarkRealmObj(){

    }

    public FavouriteLandmarkRealmObj(Landmark landmark){
        this.id = landmark.getId();
        this.latitude = landmark.getLatitude();
        this.longitude = landmark.getLongitude();
        this.name = landmark.getName();
        this.type = landmark.getType();
    }

    public String getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
