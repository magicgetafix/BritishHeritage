package com.britishheritage.android.britishheritage.Database;

import com.britishheritage.android.britishheritage.Daos.LandmarkDao;
import com.britishheritage.android.britishheritage.Model.Landmark;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = Landmark.class, version = 2, exportSchema = false)
public abstract class LandmarkDatabase extends RoomDatabase {
    public abstract LandmarkDao landmarkDao();
}
