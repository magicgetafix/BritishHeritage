package com.britishheritage.android.britishheritage.Model;

import com.britishheritage.android.britishheritage.Model.Realm.WikiLandmarkRealmObj;

public class WikiLandmark {

    private String url = "";
    private String summary = "";
    private String title = "";
    private double lat = 0.0;
    private double lng = 0.0;

    public WikiLandmark(WikiLandmarkRealmObj wikiLandmarkRealmObj){
        this.url = wikiLandmarkRealmObj.getUrl();
        this.summary = wikiLandmarkRealmObj.getSummary();
        this.title = wikiLandmarkRealmObj.getTitle();
        this.lat = wikiLandmarkRealmObj.getLat();
        this.lng = wikiLandmarkRealmObj.getLng();
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
