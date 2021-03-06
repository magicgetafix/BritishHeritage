package com.britishheritage.android.britishheritage.Model.Realm;

import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.Model.Landmark;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavouriteLandmarkRealmObj extends RealmObject {

    @PrimaryKey
    private String id = "";
    private Double latitude;
    private Double longitude;
    private String name;
    private String type;
    private String webUrl;

    public FavouriteLandmarkRealmObj(){

    }

    public FavouriteLandmarkRealmObj(Landmark landmark){
        this.id = landmark.getId();
        this.latitude = landmark.getLatitude();
        this.longitude = landmark.getLongitude();
        this.name = Tools.formatTitle(landmark.getName());
        this.type = landmark.getType();
        this.webUrl = landmark.getWebUrl();
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

    public String getWebUrl(){
        return webUrl;
    }
}
