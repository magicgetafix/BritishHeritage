package com.britishheritage.android.britishheritage.Model;

public class Landmark {

  private String id;
  private Double latitude;
  private Double longitude;
  private String name;
  private String type;

  public Landmark(String id, Double latitude, Double longitude, String name, String type){
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.name = name;
    this.type = type;
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
