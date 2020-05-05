package com.britishheritage.android.britishheritage.Maps;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Model.Realm.FavouriteLandmarkRealmObj;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.google.android.gms.maps.model.LatLng;


import java.util.*;

public class MapViewModel extends AndroidViewModel {


    private Set<Landmark> listedBuildingsSet = new TreeSet<>();
    private Set<Landmark> publicGardensSet = new TreeSet<>();
    private Set<Landmark> scheduledMonumentsSet = new TreeSet<>();
    private Set<Landmark> battleFieldSet = new TreeSet<>();
    private Set<Landmark> hillfortsSet = new TreeSet<>();
    private Set<Landmark> bluePlaqueSet = new TreeSet<>();

    private HashMap<String, Landmark> landmarksHashMap = new HashMap<>();
    private Landmark lastClickedLandMark = null;

    private MutableLiveData<Landmark> scheduledMonumentLiveData = new MutableLiveData<>();
    private MutableLiveData<Landmark> listedBuildingLiveData = new MutableLiveData<>();
    private MutableLiveData<Landmark> publicGardenLiveData = new MutableLiveData<>();
    private MutableLiveData<Landmark> battleFieldLiveData = new MutableLiveData<>();
    private MutableLiveData<Landmark> hillfortLiveData = new MutableLiveData<>();
    private MutableLiveData<Landmark> bluePlaqueLiveData = new MutableLiveData<>();

    private List<FavouriteLandmarkRealmObj> favouritesList;
    private MutableLiveData<List<FavouriteLandmarkRealmObj>> favouritesListLiveData = new MutableLiveData<>();


    private DatabaseInteractor databaseInteractor;

    public MapViewModel(@NonNull Application application) {
        super(application);
        databaseInteractor = DatabaseInteractor.getInstance(getApplication().getApplicationContext());
        favouritesList = new ArrayList<>();


    }

    public void getLandmarks(LatLng neLatLng, LatLng swLatLng){

        databaseInteractor.getLandmarksInBox(neLatLng, swLatLng).observeForever(list->processLandmarks(list));

    }

    public void processLandmarks(List<Landmark> landmarkList){

        for (Landmark landmark: landmarkList){
           String type = landmark.getType();

           if (type.equals(Constants.SCHEDULED_MONUMENTS_ID)){
               if (!scheduledMonumentsSet.contains(landmark)) {
                   scheduledMonumentLiveData.setValue(landmark);
                   scheduledMonumentsSet.add(landmark);
                   landmarksHashMap.put(landmark.getId(), landmark);
               }
           }
           else if (type.equals(Constants.LISTED_BUILDINGS_ID)){
               if (!listedBuildingsSet.contains(landmark)) {
                   listedBuildingLiveData.setValue(landmark);
                   listedBuildingsSet.add(landmark);
                   landmarksHashMap.put(landmark.getId(), landmark);
               }
           }
           else if (type.equals(Constants.PARKS_AND_GARDENS_ID)){
               if (!publicGardensSet.contains(landmark)) {
                   publicGardenLiveData.setValue(landmark);
                   publicGardensSet.add(landmark);
                   landmarksHashMap.put(landmark.getId(), landmark);
               }
           }
           else if (type.equals(Constants.HILLFORTS_ID)){
               if (!hillfortsSet.contains(landmark)) {
                   hillfortLiveData.setValue(landmark);
                   hillfortsSet.add(landmark);
                   landmarksHashMap.put(landmark.getId(), landmark);
               }
           }
           else if (type.equals(Constants.BATTLEFIELDS_ID)){
               if (!battleFieldSet.contains(landmark)) {
                   battleFieldLiveData.setValue(landmark);
                   battleFieldSet.add(landmark);
                   landmarksHashMap.put(landmark.getId(), landmark);
               }
           }

           else if (type.equals(Constants.BLUE_PLAQUES)){
               if (!bluePlaqueSet.contains(landmark)) {
                   bluePlaqueLiveData.setValue(landmark);
                   bluePlaqueSet.add(landmark);
                   landmarksHashMap.put(landmark.getId(), landmark);
               }
           }
        }

    }


    public MutableLiveData<Landmark> getScheduledMonumentLiveData() {
        return scheduledMonumentLiveData;
    }

    public MutableLiveData<Landmark> getListedBuildingLiveData() {
        return listedBuildingLiveData;
    }

    public MutableLiveData<Landmark> getPublicGardenLiveData() {
        return publicGardenLiveData;
    }

    public MutableLiveData<Landmark> getBattleFieldLiveData() {
        return battleFieldLiveData;
    }

    public MutableLiveData<Landmark> getHillfortLiveData() {
        return hillfortLiveData;
    }

    public Set<Landmark> getListedBuildingsSet() {
        return listedBuildingsSet;
    }

    public Set<Landmark> getPublicGardensSet() {
        return publicGardensSet;
    }

    public Set<Landmark> getScheduledMonumentsSet() {
        return scheduledMonumentsSet;
    }

    public Set<Landmark> getBattleFieldSet() {
        return battleFieldSet;
    }

    public Set<Landmark> getHillfortsSet() {
        return hillfortsSet;
    }

    public HashMap<String, Landmark> getLandmarksHashMap() {
        return landmarksHashMap;
    }

    public LiveData<Landmark> getBluePlaqueLiveData() {
        return bluePlaqueLiveData;
    }

    public void setBluePlaqueLiveData(MutableLiveData<Landmark> bluePlaqueLiveData) {
        this.bluePlaqueLiveData = bluePlaqueLiveData;
    }
}
