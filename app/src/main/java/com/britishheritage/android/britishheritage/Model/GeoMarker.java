package com.britishheritage.android.britishheritage.Model;

public class GeoMarker {

  public String landmarkId;
  public Double latitude;
  public Double longitude;

  public GeoMarker(String landmarkId, Double latitude, Double longitude){

    this.landmarkId = landmarkId;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public String getLandmarkId() {
    return landmarkId;
  }
}