package com.britishheritage.android.britishheritage.Database;

import com.britishheritage.android.britishheritage.Daos.LandmarkDao;
import com.britishheritage.android.britishheritage.Model.Landmark;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = Landmark.class, version = 1)
public abstract class LandmarkDatabase extends RoomDatabase {
    public abstract LandmarkDao landmarkDao();
}
