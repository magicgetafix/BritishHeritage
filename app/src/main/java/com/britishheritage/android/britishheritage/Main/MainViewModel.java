package com.britishheritage.android.britishheritage.Main;

import android.app.Application;

import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Model.Realm.FavouriteLandmarkRealmObj;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class MainViewModel extends AndroidViewModel implements RealmChangeListener<RealmResults<FavouriteLandmarkRealmObj>> {

    private List<Landmark> favouriteLandmarkList = new ArrayList<>();
    private DatabaseInteractor databaseInteractor;
    private MutableLiveData<List<Landmark>> favouriteListLiveData = new MutableLiveData<>();
    private RealmResults<FavouriteLandmarkRealmObj> realmResults;
    private MutableLiveData<List<Landmark>> checkedInLandmarksLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        databaseInteractor = DatabaseInteractor.getInstance(getApplication());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getUserData(user);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null){
                    favouriteListLiveData.setValue(new ArrayList<Landmark>());
                    checkedInLandmarksLiveData.setValue(new ArrayList<Landmark>());
                }
                else{
                    getUserData(currentUser);
                }
            }
        });
    }

    private void getUserData(FirebaseUser user){
        if (user!=null) {
            realmResults = databaseInteractor.getFavourites();
            realmResults.addChangeListener(this);
            databaseInteractor.getCheckedInLandmarks(user).observeForever(landmarks -> {
                checkedInLandmarksLiveData.setValue(landmarks);
            });
        }
    }


    @Override
    public void onChange(RealmResults<FavouriteLandmarkRealmObj> favouriteLandmarkRealmObjs) {
        favouriteLandmarkList.clear();
        Iterator<FavouriteLandmarkRealmObj> favouriteObjIterator = favouriteLandmarkRealmObjs.iterator();
        while (favouriteObjIterator.hasNext()){
            FavouriteLandmarkRealmObj favouriteLandmarkRealmObj = favouriteObjIterator.next();
            Landmark favouriteLandmark = new Landmark(favouriteLandmarkRealmObj);
            favouriteLandmarkList.add(favouriteLandmark);
            favouriteListLiveData.setValue(favouriteLandmarkList);
        }

    }
    public LiveData<List<Landmark>> getCheckedInLandmarkLiveData(){
        return checkedInLandmarksLiveData;
    }

    public LiveData<List<Landmark>> getFavouriteListLiveData() {
        return favouriteListLiveData;
    }
}
