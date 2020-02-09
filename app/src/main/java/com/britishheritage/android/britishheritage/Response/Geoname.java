package com.britishheritage.android.britishheritage.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geoname {

    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("elevation")
    @Expose
    private Number elevation;
    @SerializedName("lng")
    @Expose
    private Number lng;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("rank")
    @Expose
    private Number rank;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("lat")
    @Expose
    private Number lat;
    @SerializedName("wikipediaUrl")
    @Expose
    private String wikipediaUrl;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("feature")
    @Expose
    private String feature;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getElevation() {
        return elevation.intValue();
    }

    public void setElevation(Number elevation) {
        this.elevation = elevation;
    }

    public double getLng() {
        return lng.doubleValue();
    }

    public void setLng(Number lng) {
        this.lng = lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getRank() {
        return rank.intValue();
    }

    public void setRank(Number rank) {
        this.rank = rank;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat.doubleValue();
    }

    public void setLat(Number lat) {
        this.lat = lat;
    }

    public String getWikipediaUrl() {
        return wikipediaUrl;
    }

    public void setWikipediaUrl(String wikipediaUrl) {
        this.wikipediaUrl = wikipediaUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

}
