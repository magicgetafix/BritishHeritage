package com.britishheritage.android.britishheritage.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LandmarkList {

    @Expose
    @SerializedName("landmarks")
    private List<Landmark> landmarks;

    public List<Landmark> getLandmarks() {
        return landmarks;
    }
}
