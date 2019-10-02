package com.britishheritage.android.britishheritage.Maps.LocationQueries;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import timber.log.Timber;

public class LatLngQuery {

    private GeoCodeLocationClient geoCodeLocationClient;
    private LatLngResultListener listener;

    public LatLngQuery(){
        this.geoCodeLocationClient = new GeoCodeLocationClient();
    }

    public static LatLngQuery getInstance(){
        return new LatLngQuery();
    }

    public void searchForCity(String cityQuery, LatLngResultListener latLngResultListener){

        GeoCodeLocationClient client = this.geoCodeLocationClient;
        this.listener = latLngResultListener;
        client.getData(cityQuery, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response != null) {
                        JSONArray responseArray = response.getJSONArray("results");
                        if (responseArray!=null){
                            JSONObject locationObject = (JSONObject) responseArray.get(0);
                            final JsonGeometryObject locationInfo = JsonGeometryObject.fromJson(locationObject);
                            if (locationInfo!=null){

                                JSONObject locationMap = locationInfo.location;
                                if(locationMap==null){

                                    Timber.d("JSONError: Can't retrieve Json location data");
                                    listener.onError();
                                    return;
                                }
                                double lat = 0.0;
                                double lng = 0.0;
                                if (locationMap.has("lat")){
                                    try {
                                        lat = locationMap.getDouble("lat");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (locationMap.has("lng")){

                                    try {
                                        lng = locationMap.getDouble("lng");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                if (lat!=0.0){
                                    LatLng locationLatLng = new LatLng(lat, lng);
                                    listener.foundLatLng(locationLatLng);
                                }

                            }
                        }
                    }
                } catch (JSONException e) {
                   listener.onError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onError();
            }
        });

    }

    public interface LatLngResultListener{

        void foundLatLng(LatLng latLng);
        void onError();

    }

}
