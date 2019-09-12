package com.britishheritage.android.britishheritage.Daos;

import com.britishheritage.android.britishheritage.Model.Landmark;

import java.util.List;

import androidx.room.Insert;

public interface LandmarkDao {

    @Insert
    void insertAll(List<Landmark> landmarks);

}
