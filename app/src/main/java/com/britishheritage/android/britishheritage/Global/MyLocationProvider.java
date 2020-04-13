package com.britishheritage.android.britishheritage.Global;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Main.MainActivity;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.maps.model.LatLng;

public class MyLocationProvider {

    private static Activity myActivity;
    private static Context appContext;
    private static LocationManager locationManager;
    private static Location location;


    public static Location getLastLocation(Activity activity) throws SecurityException {

        myActivity = activity;
        if (appContext == null) {
            appContext = activity.getApplicationContext();
        }
        if (locationManager == null) {
            locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        }
        boolean havePermissions = checkPermissions();
        if (havePermissions){

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        }

        if (location!=null){
            return location;
        }

        else{
            BaseActivity baseActivity = (BaseActivity) myActivity;
            //Shows a message to ask the user to modify their location permissions
            if (baseActivity!= null){
                baseActivity.showSnackbar(appContext.getString(R.string.unable_to_find_location));
            }
            return null;
        }

    }

    public static void addLocationListener(LocationListener listener, Activity activity) throws SecurityException {

        if (locationManager == null) {
            locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        }
        boolean havePermissions = checkPermissions();
        if (havePermissions){
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, listener);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 10, listener);
            }
        }
        else{
            BaseActivity baseActivity = (BaseActivity) activity;
            //Shows a message to ask the user to modify their location permissions
            if (baseActivity!= null){
                baseActivity.showSnackbar(appContext.getString(R.string.unable_to_find_location));
            }
            return;
        }
    }

    public static void removeLocationListeners(LocationListener listener){
        if (locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }

    public static LatLng getLastLocationLatLng(Activity activity){

        Location location = getLastLocation(activity);
        if (location!=null) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            return latLng;
        }
        else return Constants.DEFAULT_LATLNG;

    }

    @TargetApi(23)
    private static boolean checkPermissions(){

        if (appContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && appContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            myActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 43);

            if (appContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && appContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return checkPermissions(false);
            }

        }
        return true;
    }

    @TargetApi(23)
    private static boolean checkPermissions(Boolean bool){
        boolean havePermissions = bool;
        if (appContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && appContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            myActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 43);

            if (appContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && appContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return havePermissions;
            }
            else{
                return true;
            }

        }
        return true;
    }



}
