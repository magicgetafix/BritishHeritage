package com.britishheritage.android.britishheritage.Global;

import com.google.android.gms.maps.model.LatLng;

public class Constants {

    public static final String LISTED_BUILDINGS_ID = "LB";
    public static final String BATTLEFIELDS_ID = "BF";
    public static final String SCHEDULED_MONUMENTS_ID = "SM";
    public static final String HILLFORTS_ID = "HF";
    public static final String PARKS_AND_GARDENS_ID = "PG";
    public static final String SHARED_PREFERENCES_FILE = "british_heritage_shared_preferences";
    public static final String SHARED_PREF_BOOLEAN = "shared_preferences_boolean";

    public final static String GEO_FIRE_DATABASE_REF = "geo_location";
    public final static String LANDMARK_REF = "landmarks";
    public final static String DATABASE_NAME = "landmarks";
    public final static LatLng DEFAULT_LATLNG = new LatLng(51.179, -1.828);

    //for geocode api
    public final static String GEOCODE_USERNAME = "britishheritage";
    public final static String GEOCODE_STYLE = "full";
    public final static String URL_REGEX = "(?:www|https?)[^\\s]+";

    //review database keys
    public final static String REVIEW_TEXT_KEY = "txt";
    public final static String REVIEW_USERNAME_KEY = "uN";
    public final static String REVIEW_SCORE_KEY = "pts";
    public final static String REVIEW_TIME_STAMP_KEY =  "tS";
    public final static String REVIEW_USER_ID_KEY = "uId";
    public final static String REVIEW_ID = "rId";
    public final static String REVIEW_TITLE = "rT";
}
