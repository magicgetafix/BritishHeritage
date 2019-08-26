package com.britishheritage.android.britishheritage.Interactors;

import androidx.annotation.NonNull;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.GeoMarker;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.firebase.geofire.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.*;
import io.reactivex.Flowable;
import org.reactivestreams.Subscriber;

public class DatabaseInteractor {

  private FirebaseDatabase database;
  private DatabaseReference geoFireRef;
  private DatabaseReference landmarkRef;
  private GeoFire geoFire;
  private Flowable<GeoMarker> geoMarkerFlowable;
  private Flowable<GeoMarker> geoMarkerFlowableStream;
  private Flowable<Landmark> landmarkFlowable;

  public DatabaseInteractor(){
    database = FirebaseDatabase.getInstance();
    geoFireRef = database.getReference(Constants.GEO_FIRE_DATABASE_REF);
    landmarkRef = database.getReference(Constants.LANDMARK_REF);
    geoFire = new GeoFire(geoFireRef);
  }

  public Flowable<GeoMarker> getGeoMarkerStream(LatLng centerLatLng, Double radius){

    geoMarkerFlowableStream = new Flowable<GeoMarker>() {
      @Override
      protected void subscribeActual(Subscriber<? super GeoMarker> s) {

      }
    };

    GeoLocation centerLocation = new GeoLocation(centerLatLng.latitude, centerLatLng.longitude);
    if (geoFire!=null){
      GeoQuery geoQuery = geoFire.queryAtLocation(centerLocation, radius);
      geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {

          GeoMarker geoMarker = new GeoMarker(key, location.latitude, location.longitude);
          geoMarkerFlowable = Flowable.just(geoMarker);
          geoMarkerFlowableStream = geoMarkerFlowableStream.mergeWith(geoMarkerFlowable);
        }

        @Override
        public void onKeyExited(String key) {

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {
        }
      });
    }

    return geoMarkerFlowableStream;

  }

  public Flowable<Landmark> getLandmark(String id){

    DatabaseReference child = landmarkRef.child(id);
    child.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Landmark landmark = dataSnapshot.getValue(Landmark.class);
        if (landmark!=null){
          landmarkFlowable = Flowable.just(landmark);
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    return landmarkFlowable;
  }
}
