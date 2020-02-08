package com.britishheritage.android.britishheritage.Response;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyWikipediaResponse {

    @SerializedName("geonames")
    @Expose
    private List<Geoname> geonames = new ArrayList<>();

    public List<Geoname> getGeonames() {
        return geonames;
    }

    public void setGeonames(List<Geoname> geonames) {
        this.geonames = geonames;
    }

}
