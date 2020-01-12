package com.britishheritage.android.britishheritage.Database;

import android.content.Context;
import android.os.AsyncTask;

import com.britishheritage.android.britishheritage.Daos.LandmarkDao;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.FavouriteLandmarkRealmObj;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import io.reactivex.Flowable;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DatabaseInteractor {

    private static DatabaseInteractor instance;
    private Context context;
    private LandmarkDatabase db;
    private Realm realm;

    public static DatabaseInteractor getInstance(Context context){
        if (instance == null){
            instance = new DatabaseInteractor(context);
        }
        return instance;
    }

    public DatabaseInteractor(Context context){
        this.context = context;
        db = Room.databaseBuilder(context, LandmarkDatabase.class, Constants.DATABASE_NAME).build();
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public void addAllLandmarks(List<Landmark> landmarks){
        new insertLandmarkAsyncTask(db.landmarkDao()).execute(landmarks);
    }

    private static class insertLandmarkAsyncTask extends AsyncTask<List<Landmark>, Void, Void>{

        private LandmarkDao landmarkDao;

        insertLandmarkAsyncTask(LandmarkDao landmarkDao){
            this.landmarkDao = landmarkDao;
        }

        @Override
        protected Void doInBackground(List<Landmark>... lists) {
            landmarkDao.insert(lists[0]);
            return null;
        }
    }

    public LiveData<Integer> getDatabaseSize(){
        return db.landmarkDao().getNumberOfEntries();
    }

    /**A method which returns a LiveData<List>of Landmarks
     *
     * @param topRightLatLng the top right or north east coordinates of the map
     * @param bottomLeftLatLng the bottom left or south west coordinates of the map
     * @return
     */
    public LiveData<List<Landmark>> getLandmarksInBox(LatLng topRightLatLng, LatLng bottomLeftLatLng){

        double maxLat = topRightLatLng.latitude;
        double minLat = bottomLeftLatLng.latitude;
        double maxLong = topRightLatLng.longitude;
        double minLong = bottomLeftLatLng.longitude;
        return db.landmarkDao().getNearestLandmarks(maxLat, minLat, maxLong, minLong);
    }

    public void addFavourite(Landmark landmark){

        FavouriteLandmarkRealmObj favourite = new FavouriteLandmarkRealmObj(landmark);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(favourite);
        realm.commitTransaction();

    }

    public RealmResults<FavouriteLandmarkRealmObj> getFavourites(){
        return realm.where(FavouriteLandmarkRealmObj.class)
                .sort("latitude", Sort.DESCENDING)
                .findAllAsync();
    }
}
