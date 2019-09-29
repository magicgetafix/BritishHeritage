package com.britishheritage.android.britishheritage.Model;

public class MapEntity {

  private GeoMarker geoMarker;
  private Landmark landmark;

  public MapEntity(Landmark landmark){

    this.landmark = landmark;
  }

  public GeoMarker getGeoMarker() {
    return geoMarker;
  }

  public Landmark getLandmark() {
    return landmark;
  }
}
