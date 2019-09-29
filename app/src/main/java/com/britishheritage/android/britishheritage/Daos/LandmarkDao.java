package com.britishheritage.android.britishheritage.Daos;

import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.Landmark;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LandmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Landmark> landmarks);

    @Query("SELECT COUNT(*) FROM LANDMARK_TABLE")
    LiveData<Integer> getNumberOfEntries();

    @Query("SELECT * FROM LANDMARK_TABLE WHERE latitude < :maxLat AND latitude > :minLat AND longitude < :maxLong AND longitude > :minLong")
    LiveData<List<Landmark>> getNearestLandmarks(double maxLat, double minLat, double maxLong, double minLong);

}
