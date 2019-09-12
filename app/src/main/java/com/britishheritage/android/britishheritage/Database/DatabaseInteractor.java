package com.britishheritage.android.britishheritage.Database;

import android.content.Context;

import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.Landmark;

import java.util.List;

import androidx.room.Room;

public class DatabaseInteractor {

    private static DatabaseInteractor instance;

    private Context context;
    private LandmarkDatabase db;

    public static DatabaseInteractor getInstance(Context context){
        if (instance == null){
            instance = new DatabaseInteractor(context);
        }
        return instance;
    }

    public DatabaseInteractor(Context context){
        this.context = context;
        db = Room.databaseBuilder(context, LandmarkDatabase.class, Constants.DATABASE_NAME).build();
    }

    public void addAllLandmarks(List<Landmark> landmarks){
        db.landmarkDao().insertAll(landmarks);
    }
}
