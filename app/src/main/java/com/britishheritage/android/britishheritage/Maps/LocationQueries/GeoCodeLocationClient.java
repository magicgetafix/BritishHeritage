package com.britishheritage.android.britishheritage.Maps.LocationQueries;

import com.britishheritage.android.britishheritage.Keys.GooglePlayKeys;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class GeoCodeLocationClient {

    private static final String GOOGLE_MAP_BASE_URL = "https://maps.google.com/maps/api/geocode/json?address=";
    private static final String UK_COUNTRY_SUFFIX = "+UK&key=";
    private String googleMapServicesKey;
    private AsyncHttpClient client;

    /**Constructor sets up AsyncHttpClient
     *
     */
    public GeoCodeLocationClient(){

        this.client = new AsyncHttpClient();
        this.googleMapServicesKey = GooglePlayKeys.MAP_KEY;
    }

    /**
     * @param cityQuery takes the city name in the form of a String to construct
     *                  the full URL address.
     * @return returns a String which provides the full URL address for the query
     */
    private String getFullUrl(String cityQuery){

        return GOOGLE_MAP_BASE_URL+cityQuery+UK_COUNTRY_SUFFIX+this.googleMapServicesKey;

    }
    /**     *
     * @param cityQuery the city name which the query is related to
     * @param handler a JsonHttpResponseHandler which listens for the response
     *                from the Geocoding API
     */
    public void getData(final String cityQuery, JsonHttpResponseHandler handler){

        String url = getFullUrl(cityQuery);
        client.get(url, handler);

    }
}
