package com.britishheritage.android.britishheritage.Main;

import android.app.Application;

import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Model.FavouriteLandmarkRealmObj;
import com.britishheritage.android.britishheritage.Model.Landmark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class MainViewModel extends AndroidViewModel implements RealmChangeListener<RealmResults<FavouriteLandmarkRealmObj>> {

    private List<Landmark> favouriteLandmarkList = new ArrayList<>();
    private DatabaseInteractor databaseInteractor;
    private MutableLiveData<List<Landmark>> favouriteListLiveData = new MutableLiveData<>();
    private RealmResults<FavouriteLandmarkRealmObj> realmResults;

    public MainViewModel(@NonNull Application application) {
        super(application);
        databaseInteractor = DatabaseInteractor.getInstance(getApplication());
        realmResults = databaseInteractor.getFavourites();
        realmResults.addChangeListener(this);
    }


    @Override
    public void onChange(RealmResults<FavouriteLandmarkRealmObj> favouriteLandmarkRealmObjs) {
        Iterator<FavouriteLandmarkRealmObj> favouriteObjIterator = favouriteLandmarkRealmObjs.iterator();
        while (favouriteObjIterator.hasNext()){
            FavouriteLandmarkRealmObj favouriteLandmarkRealmObj = favouriteObjIterator.next();
            Landmark favouriteLandmark = new Landmark(favouriteLandmarkRealmObj);
            favouriteLandmarkList.add(favouriteLandmark);
            favouriteListLiveData.setValue(favouriteLandmarkList);
        }

    }

    public LiveData<List<Landmark>> getFavouriteListLiveData() {
        return favouriteListLiveData;
    }
}
