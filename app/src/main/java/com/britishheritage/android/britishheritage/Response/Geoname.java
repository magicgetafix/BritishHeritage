package com.britishheritage.android.britishheritage.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geoname {

    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("elevation")
    @Expose
    private Integer elevation;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("rank")
    @Expose
    private Integer rank;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("lat")
    @Expose
    private Double lat;
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

    public Integer getElevation() {
        return elevation;
    }

    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
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
