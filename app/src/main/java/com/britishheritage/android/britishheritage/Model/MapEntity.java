package com.britishheritage.android.britishheritage.Model;

public class MapEntity {

  private GeoMarker geoMarker;
  private Landmark landmark;

  public MapEntity(GeoMarker geoMarker, Landmark landmark){

    this.geoMarker = geoMarker;
    this.landmark = landmark;
  }

  public GeoMarker getGeoMarker() {
    return geoMarker;
  }

  public Landmark getLandmark() {
    return landmark;
  }
}
