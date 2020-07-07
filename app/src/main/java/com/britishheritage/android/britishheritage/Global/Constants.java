package com.britishheritage.android.britishheritage.Global;


import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.LatLngBounds;

public class Constants {

    public static final String LISTED_BUILDINGS_ID = "LB";
    public static final String BATTLEFIELDS_ID = "BF";
    public static final String SCHEDULED_MONUMENTS_ID = "SM";
    public static final String HILLFORTS_ID = "HF";
    public static final String PARKS_AND_GARDENS_ID = "PG";
    public static final String BLUE_PLAQUES = "BP";
    public static final String SHARED_PREFERENCES_FILE = "british_heritage_shared_preferences";
    public static final String SHARED_PREF_BOOLEAN = "shared_preferences_boolean";

    public final static String GEO_FIRE_DATABASE_REF = "geo_location";
    public final static String LANDMARK_REF = "landmarks";
    public final static String DATABASE_NAME = "landmarks";

    public final static LatLng DEFAULT_LATLNG = new LatLng(51.179, -1.828);
    public final static LatLng STONEHENGE_LATLNG = new LatLng(51.179, -1.828);
    public final static LatLng AVEBURY_LATLNG = new LatLng(51.4295, -1.853);
    public final static LatLng CHESTER_LATLNG = new LatLng(53.189999, -2.890000);
    public final static LatLng ILKLEY_MOOR = new LatLng(53.914, -1.827);
    public final static LatLng EDINBURGH_LATLNG = new LatLng(55.953251, -3.188);
    public final static LatLng CASTLERIGG_LATLNG = new LatLng(54.602837, -3.098384);
    public final static LatLng MERRIVALE_LATLNG = new LatLng(50.558, -4.050);
    public final static LatLng HARLECH = new LatLng(52.86, -4.10916);
    public final static LatLng HEBDEN_BRIDGE = new LatLng(53.741, -2.013);
    public final static LatLng STIRLING = new LatLng(56.116, -3.936);
    public final static LatLng BREMENIUM = new LatLng(55.281, -2.264);
    public final static LatLng LOCH_NESS = new LatLng(57.324, -4.442);
    public final static LatLng LLANSTEFFAN = new LatLng(51.765637, -4.390566);
    public final static LatLng MITCHELLS_FOLD = new LatLng(52.5787, -3.0261);
    public final static LatLng LANYON_QUOIT = new LatLng(50.1475, -5.599167);
    public final static LatLng GARIANNONUM = new LatLng(52.582, 1.652);
    public final static LatLng LINCOLN = new LatLng(53.234444, -0.538611);
    public final static LatLng EXETER = new LatLng(50.7236, -3.52751);
    public final static LatLng WROXETER = new LatLng(52.67, -2.648);
    public final static LatLng ORKNEY = new LatLng(59.001482, -3.229723);
    public final static LatLng SCILLY_ISLES = new LatLng(49.91829, -6.28113);
    public final static LatLng CONWY = new LatLng(53.28, -3.825556);
    public final static LatLng ST_ALBANS = new LatLng(51.755, -0.336);

    public final static LatLngBounds UK_BOUNDING_BOX = new LatLngBounds(new LatLng(49.9, -8.62), new LatLng(60.84, 1.77));



    public final static LatLng[] DEFAULT_LOCATION_ARRAY = {STONEHENGE_LATLNG, AVEBURY_LATLNG,
            CHESTER_LATLNG, ILKLEY_MOOR, EDINBURGH_LATLNG, CASTLERIGG_LATLNG, MERRIVALE_LATLNG, HARLECH, HEBDEN_BRIDGE,
            STIRLING, BREMENIUM, LOCH_NESS, LLANSTEFFAN, MITCHELLS_FOLD, LANYON_QUOIT,
            GARIANNONUM, LINCOLN, EXETER, WROXETER, ORKNEY, SCILLY_ISLES, CONWY, ST_ALBANS};




    //for geocode api
    public final static String GEOCODE_USERNAME = "britishheritage";
    public final static String GEOCODE_STYLE = "full";
    public final static String URL_REGEX = "(?:www|https?)[^\\s]+";
    public final static int BIG_DEC_SCALE = 1;

    //review database keys
    public final static String REVIEW_TEXT_KEY = "txt";
    public final static String REVIEW_USERNAME_KEY = "uN";
    public final static String REVIEW_SCORE_KEY = "pts";
    public final static String REVIEW_TIME_STAMP_KEY =  "tS";
    public final static String REVIEW_USER_ID_KEY = "uId";
    public final static String REVIEW_ID = "rId";
    public final static String REVIEW_TITLE = "rT";

    //checked in landmark keys
    public final static String CHECKED_IN = "visits";
    public final static String FAVOURITES = "fav";
    public static final String REVIEWS = "reviews";
    public static final String USERS = "users";

    //rankings
    public static String[] RANKING_ARRAY = {"Itinerant",
            "Rambler",
            "Wanderer",
            "Traveller",
            "Trekker",
            "Adventurer",
            "Wayfarer",
            "Surveyor",
            "Pioneer",
            "Explorer",
            "Discoverer",
            "Heritage Expert"
    };

    public static String ENG_START_OF_URL = "https://h";
    public static String SCOT_START_OF_URL = "http://p";
    public static String WALES_START_OF_URL = "http://c";

}
