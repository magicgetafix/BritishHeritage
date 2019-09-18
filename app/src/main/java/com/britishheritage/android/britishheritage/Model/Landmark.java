package com.britishheritage.android.britishheritage.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Landmark {

  @PrimaryKey
  @NonNull
  public String id = "";
  @ColumnInfo(name = "latitude")
  public Double latitude;
  @ColumnInfo(name = "longitude")
  public Double longitude;
  @ColumnInfo(name = "name")
  public String name;
  @ColumnInfo(name = "type")
  public String type;

  //Room requires empty constructor
  public Landmark(){

  }

  @Ignore
  public Landmark(@NonNull String id, Double latitude, Double longitude, String name, String type){
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

  public void setId(String id) {
    this.id = id;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }
}
