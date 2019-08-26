package com.britishheritage.android.britishheritage.Maps;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Interactors.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Model.GeoMarker;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.MapEntity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.*;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscription;
import timber.log.Timber;

import java.util.*;

public class MapViewModel extends AndroidViewModel {


    private HashMap<String, MapEntity> listedBuildingsHashMap = new HashMap<>();
    private HashMap<String, MapEntity> publicGardensHashMap = new HashMap<>();
    private HashMap<String, MapEntity> scheduledMonumentHashMap = new HashMap<>();
    private HashMap<String, MapEntity> battleFieldsHashMap = new HashMap<>();
    private HashMap<String, MapEntity> hillfortsHashMap = new HashMap<>();

    //private FirebaseDatabase database;
    //DatabaseReference geoFireRef;
    //DatabaseReference landmarkRef;
    //GeoFire geoFire;

    private MutableLiveData<String> listedBuildingsKey = new MutableLiveData<>();
    private MutableLiveData<String> publicGardensKey = new MutableLiveData<>();
    private MutableLiveData<String> scheduledMonumentsKey = new MutableLiveData<>();
    private MutableLiveData<String> battleFieldsKey = new MutableLiveData<>();
    private MutableLiveData<String> hillfortsKey = new MutableLiveData<>();
    private GeoMarker geoMarker;

    public MapViewModel(@NonNull Application application) {
        super(application);
        //database = FirebaseDatabase.getInstance();
        //geoFireRef = database.getReference(Constants.GEO_FIRE_DATABASE_REF);
        //landmarkRef = database.getReference(Constants.LANDMARK_REF);
        //geoFire = new GeoFire(geoFireRef);
    }

    /*public void getData(LatLng centerlatLng, Double radius) {

        GeoLocation centerLocation = new GeoLocation(centerlatLng.latitude, centerlatLng.longitude);

        GeoQuery geoQuery = geoFire.queryAtLocation(centerLocation, radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                geoMarker = new GeoMarker(key, location.latitude, location.longitude);
                DatabaseReference child = landmarkRef.child(key);
                child.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Landmark landmark = dataSnapshot.getValue(Landmark.class);
                        if (landmark!=null){
                            MapEntity mapEntity = new MapEntity(geoMarker, landmark);
                            addMapEntityToHashMap(mapEntity);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

    public void addMapEntityToHashMap(MapEntity mapEntity){

        String type = mapEntity.getLandmark().getType();
        GeoMarker geoMarker = mapEntity.getGeoMarker();
        if (type.equals(Constants.LISTED_BUILDINGS_ID)){
            if (listedBuildingsHashMap.get(geoMarker.landmarkId) == null) {
                listedBuildingsHashMap.put(geoMarker.landmarkId, mapEntity);
                listedBuildingsKey.setValue(geoMarker.landmarkId);
            }
        }
        else if (type.equals(Constants.SCHEDULED_MONUMENTS_ID)){
            if (scheduledMonumentHashMap.get(geoMarker.landmarkId) == null) {
                scheduledMonumentHashMap.put(geoMarker.landmarkId, mapEntity);
                scheduledMonumentsKey.setValue(geoMarker.landmarkId);
            }
        }
        else if (type.equals(Constants.HILLFORTS_ID)){
            if (hillfortsHashMap.get(geoMarker.landmarkId) == null) {
                hillfortsHashMap.put(geoMarker.landmarkId, mapEntity);
                hillfortsKey.setValue(geoMarker.landmarkId);
            }
        }
        else if (type.equals(Constants.BATTLEFIELDS_ID)){
            if (battleFieldsHashMap.get(geoMarker.landmarkId) == null) {
                battleFieldsHashMap.put(geoMarker.landmarkId, mapEntity);
                battleFieldsKey.setValue(geoMarker.landmarkId);
            }
        }
        else if (type.equals(Constants.PARKS_AND_GARDENS_ID)){
            if (publicGardensHashMap.get(geoMarker.landmarkId) == null) {
                publicGardensHashMap.put(geoMarker.landmarkId, mapEntity);
                publicGardensKey.setValue(geoMarker.landmarkId);
            }
        }
    }



    /**public void getData(LatLng centerlatLng, Double radius){

        FlowableSubscriber<GeoMarker> geoMarkerFlowableSubscriber = new FlowableSubscriber<GeoMarker>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(GeoMarker geoMarker) {

                FlowableSubscriber<Landmark> landmarkFlowableSubscriber = new FlowableSubscriber<Landmark>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Landmark landmark) {
                        MapEntity mapEntity = new MapEntity(geoMarker, landmark);
                        String type = mapEntity.getLandmark().getType();
                        if (type.equals(Constants.LISTED_BUILDINGS_ID)){
                            if (listedBuildingsHashMap.get(geoMarker.landmarkId) == null) {
                                listedBuildingsHashMap.put(geoMarker.landmarkId, mapEntity);
                                listedBuildingsKey.setValue(geoMarker.landmarkId);
                            }
                        }
                        else if (type.equals(Constants.SCHEDULED_MONUMENTS_ID)){
                            if (scheduledMonumentHashMap.get(geoMarker.landmarkId) == null) {
                                scheduledMonumentHashMap.put(geoMarker.landmarkId, mapEntity);
                                scheduledMonumentsKey.setValue(geoMarker.landmarkId);
                            }
                        }
                        else if (type.equals(Constants.HILLFORTS_ID)){
                            if (hillfortsHashMap.get(geoMarker.landmarkId) == null) {
                                hillfortsHashMap.put(geoMarker.landmarkId, mapEntity);
                                hillfortsKey.setValue(geoMarker.landmarkId);
                            }
                        }
                        else if (type.equals(Constants.BATTLEFIELDS_ID)){
                            if (battleFieldsHashMap.get(geoMarker.landmarkId) == null) {
                                battleFieldsHashMap.put(geoMarker.landmarkId, mapEntity);
                                battleFieldsKey.setValue(geoMarker.landmarkId);
                            }
                        }
                        else if (type.equals(Constants.PARKS_AND_GARDENS_ID)){
                            if (publicGardensHashMap.get(geoMarker.landmarkId) == null) {
                                publicGardensHashMap.put(geoMarker.landmarkId, mapEntity);
                                publicGardensKey.setValue(geoMarker.landmarkId);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };
                Flowable<Landmark> landmarkFlowable = dbInteractor.getLandmark(geoMarker.landmarkId);
                landmarkFlowable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(landmarkFlowableSubscriber);
            }

            @Override
            public void onError(Throwable t) {

                Timber.e("Error "+t.toString());
            }

            @Override
            public void onComplete() {

            }
        };


        Flowable<GeoMarker> geoMarkerStream = dbInteractor.getGeoMarkerStream(centerlatLng, radius);
        geoMarkerStream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(geoMarkerFlowableSubscriber);

    }**/

    public MutableLiveData<String> getBattleFieldsKey() {
        return battleFieldsKey;
    }

    public MutableLiveData<String> getHillfortsKey() {
        return hillfortsKey;
    }

    public MutableLiveData<String> getListedBuildingsKey() {
        return listedBuildingsKey;
    }

    public MutableLiveData<String> getPublicGardensKey() {
        return publicGardensKey;
    }

    public MutableLiveData<String> getScheduledMonumentsKey() {
        return scheduledMonumentsKey;
    }

    public HashMap<String, MapEntity> getListedBuildingsHashMap() {
        return listedBuildingsHashMap;
    }

    public HashMap<String, MapEntity> getPublicGardensHashMap() {
        return publicGardensHashMap;
    }

    public HashMap<String, MapEntity> getScheduledMonumentHashMap() {
        return scheduledMonumentHashMap;
    }

    public HashMap<String, MapEntity> getBattleFieldsHashMap() {
        return battleFieldsHashMap;
    }

    public HashMap<String, MapEntity> getHillfortsHashMap() {
        return hillfortsHashMap;
    }
}
