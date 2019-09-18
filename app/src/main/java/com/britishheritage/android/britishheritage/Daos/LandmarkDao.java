package com.britishheritage.android.britishheritage.Daos;

import com.britishheritage.android.britishheritage.Model.Landmark;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface LandmarkDao {

    @Insert
    void insert(List<Landmark> landmarks);

}
