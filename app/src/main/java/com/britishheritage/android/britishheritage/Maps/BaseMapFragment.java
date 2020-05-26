package com.britishheritage.android.britishheritage.Maps;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Global.MyLocationProvider;
import com.britishheritage.android.britishheritage.Main.MainActivity;
import com.britishheritage.android.britishheritage.Maps.LocationQueries.LatLngQuery;
import com.britishheritage.android.britishheritage.Maps.MapAdapters.MapInfoWindow;
import com.britishheritage.android.britishheritage.Model.*;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import timber.log.Timber;

public class BaseMapFragment extends Fragment implements OnMapReadyCallback, LatLngQuery.LatLngResultListener, LocationListener {

    private MapViewModel mViewModel;
    private SupportMapFragment supportMapFragment;
    private GoogleMap gMap;
    private LatLng currentLatLng = Constants.DEFAULT_LATLNG;
    private static LatLng newLatLng;
    private static String newId = "";

    private static Bitmap hillIcon;
    private static Bitmap monumentIcon;
    private static Bitmap battleIcon;
    private static Bitmap buildingIcon;
    private static Bitmap parkIcon;

    private EditText searchText;
    private ImageView searchIcon;
    private LatLngQuery latLngQuery;
    private float pixelDensityScale = 1.0f;
    private int iconBmapWidthHeight = 20;
    private Marker currentLocationMarker;
    private BitmapDescriptor locationBitmapDescriptor;
    private boolean updatedLocationHasBeenSet = false;
    private ImageView mapLayerButton;
    private static Marker tempMarker;

    public void setTargetLatLng(LatLng latLng, String id){
        newId = id;
        newLatLng = latLng;
        if (gMap!=null){
            gMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));

        }
    }

    public void navBackToCurrentLatLng(){
        if (gMap!=null && currentLatLng!=null){
            gMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng), 500, null);
        }
    }

    public void resetMap(){
        if (newLatLng != null) {
            if (gMap != null && currentLatLng != null) {
                gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            }
            if (tempMarker!=null){
                tempMarker.hideInfoWindow();
            }
        }
        newLatLng = null;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.archaeology_map_fragment, container, false);
    }

    private void switchMapLayer(){
        try {
            if (gMap != null) {
                int mapType = gMap.getMapType();
                if (mapType == GoogleMap.MAP_TYPE_NORMAL) {
                    locationBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_white_36);
                    if (currentLocationMarker!=null) {
                        currentLocationMarker.remove();
                        currentLocationMarker = gMap.addMarker(new MarkerOptions().icon(locationBitmapDescriptor).position(currentLatLng));
                    }
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    gMap.getUiSettings().setCompassEnabled(false);
                } else {
                    locationBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_black_36);
                    if (currentLocationMarker!=null) {
                        currentLocationMarker.remove();
                        currentLocationMarker = gMap.addMarker(new MarkerOptions().icon(locationBitmapDescriptor).position(currentLatLng));
                    }
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    gMap.getUiSettings().setCompassEnabled(false);
                }
            }
        }
        catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        searchIcon = view.findViewById(R.id.arch_search_button);
        searchText = view.findViewById(R.id.arch_searchbar);
        mapLayerButton = view.findViewById(R.id.switch_map_button);

        mapLayerButton.setOnClickListener(v->{
            switchMapLayer();
        });

        // Get the screen's density scale
        pixelDensityScale = getResources().getDisplayMetrics().density;
        iconBmapWidthHeight = (int)(pixelDensityScale * 20 + 0.5f);

        supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_archaeology);
        supportMapFragment.getMapAsync(this);
        Location location = MyLocationProvider.getLastLocation(getActivity());

            if (location != null) {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            else{
                currentLatLng = Constants.DEFAULT_LATLNG;
            }



        //setting up icon size
        BitmapDrawable bitmapDrawable = null;
        try {
            bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_hill_silhouette_small);
            hillIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), iconBmapWidthHeight, iconBmapWidthHeight, false);
        }
        catch(OutOfMemoryError outOfMemoryError){
            Timber.e(outOfMemoryError);
        }
        try {
            bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_battlefield);
            battleIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), iconBmapWidthHeight, iconBmapWidthHeight, false);
        }
        catch(OutOfMemoryError outOfMemoryError){
            Timber.e(outOfMemoryError);
        }
        try {
            bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_menhir);
            monumentIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), iconBmapWidthHeight, iconBmapWidthHeight, false);
        }
        catch(OutOfMemoryError outOfMemoryError){
            Timber.e(outOfMemoryError);
        }
        try {
            bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_museum_medium);
            buildingIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), iconBmapWidthHeight, iconBmapWidthHeight, false);
        }
        catch(OutOfMemoryError outOfMemoryError){
            Timber.e(outOfMemoryError);
        }
        try {
            bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_park);
            parkIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), iconBmapWidthHeight, iconBmapWidthHeight, false);
        }
        catch(OutOfMemoryError outOfMemoryError){
            Timber.e(outOfMemoryError);
        }

        locationBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_black_36);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = searchText.getText().toString();
                searchString = searchString.trim();
                if (!searchString.isEmpty()) {
                    searchForLatLng(searchString);
                    updatedLocationHasBeenSet = true;
                }
                else{
                    Toast.makeText(getContext(), "Please type a location name into the search bar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updatedLocationHasBeenSet = false;
    }

    public void searchForLatLng(String cityQuery){
        if (latLngQuery == null){
            latLngQuery = LatLngQuery.getInstance();
        }
        latLngQuery.searchForCity(cityQuery, this);
    }

    /**
     *
     * @param googleMap
     * @return an array of two LatLng, the first value is the NorthEast corner of the map,
     * the second value is the SouthWest corner of the map
     */
    private LatLng[] getMapCorners(GoogleMap googleMap) {

        LatLng[] latLngs = new LatLng[2];
        latLngs[0] = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        latLngs[1] = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        return latLngs;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.getUiSettings().setCompassEnabled(false);
        gMap.setMinZoomPreference(11);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        //moves map camera to current location
        if (newLatLng == null && currentLatLng!=null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        }
        else{
            gMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        }
        //adds marker
        if (currentLocationMarker == null){
            currentLocationMarker = gMap.addMarker(new MarkerOptions().position(currentLatLng).icon(locationBitmapDescriptor));
        }
        else{
            currentLocationMarker.setPosition(currentLatLng);
        }
        gMap.setInfoWindowAdapter(new MapInfoWindow(getContext()));

        MyLocationProvider.removeLocationListeners(this);
        MyLocationProvider.addLocationListener(this, getActivity());

        setDragMapBehaviour(gMap);

        LatLng[] latLngs = getMapCorners(gMap);
        mViewModel.getScheduledMonumentLiveData().observe(this, this::setUpMarker);
        mViewModel.getHillfortLiveData().observe(this, this::setUpMarker);
        mViewModel.getBattleFieldLiveData().observe(this, this::setUpMarker);
        mViewModel.getPublicGardenLiveData().observe(this, this::setUpMarker);
        mViewModel.getListedBuildingLiveData().observe(this, this::setUpMarker);
        mViewModel.getBluePlaqueLiveData().observe(this, this::setUpMarker);

        mViewModel.getLandmarks(latLngs[0], latLngs[1]);


        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                newLatLng = marker.getPosition();
                gMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng), 700, null);
                return true;
            }
        });

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (marker.getSnippet()!=null) {
                    String snippetData = marker.getSnippet();
                    String[] snippetCSV = snippetData.split("@@");
                    double latitude = 0.0;
                    double longitude = 0.0;
                    String name = "";
                    String id = "";
                    String type = "";
                    String webUrl = "";
                    if (snippetCSV.length == 6) {
                        id = snippetCSV[0];
                        name = snippetCSV[1];
                        latitude = Double.parseDouble(snippetCSV[2]);
                        longitude = Double.parseDouble(snippetCSV[3]);
                        type = snippetCSV[4];
                        webUrl = snippetCSV[5];

                        Landmark landmark = new Landmark(id, latitude, longitude, name, type, webUrl);
                        MainActivity.lastClickedLandmark = landmark;
                        showBottomSheet();

                    }
                    else{
                        Timber.e("Snippet wrong format");
                    }
                }
                else{
                    Timber.e("Snippet wrong format");
                }
            }
        });

    }


    public void setDragMapBehaviour(final GoogleMap gMap){

        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng[] latLngs = getMapCorners(gMap);
                mViewModel.getLandmarks(latLngs[0], latLngs[1]);
            }
        });
    }

    private void setUpMarker(Landmark landmark){

        if (landmark == null){
            Timber.d("Landmark is null in BaseMapFragment");
            return;
        }

        Double entityLat = landmark.getLatitude();
        Double entityLong = landmark.getLongitude();
        LatLng entLatLng = new LatLng(entityLat, entityLong);
        String name = landmark.getName();
        String latitude = entityLat.toString();
        String longitude = entityLong.toString();
        String type = getString(R.string.scheduled_monument);
        String id = landmark.getId();
        String csvData = id+"@@"+name+"@@"+latitude+"@@"+longitude+"@@";
        String webUrl = landmark.getWebUrl();
        if (webUrl == null){
            webUrl = "";
        }

        if (landmark.getType().equals(Constants.SCHEDULED_MONUMENTS_ID)
            && this instanceof ArchMapFragment){

            type = getString(R.string.scheduled_monument);
            csvData +=type+"@@"+webUrl;
            if (monumentIcon!=null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(monumentIcon);
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .icon(bitmapDescriptor)
                        .snippet(csvData).draggable(false));
            }
            else{
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .snippet(csvData).draggable(false));
            }
        }

        else if (landmark.getType().equals(Constants.HILLFORTS_ID)
        && this instanceof ArchMapFragment){

            type = getString(R.string.hillfort);
            csvData +=type+"@@"+webUrl;
            if (hillIcon!=null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(hillIcon);
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .icon(bitmapDescriptor)
                        .snippet(csvData).draggable(false));
            }
            else{
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .snippet(csvData).draggable(false));
            }
        }

        else if (landmark.getType().equals(Constants.BATTLEFIELDS_ID)
        && this instanceof ArchMapFragment){

            type = getString(R.string.battlefield);
            csvData +=type+"@@"+webUrl;
            if (battleIcon!=null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(battleIcon);
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .icon(bitmapDescriptor)
                        .snippet(csvData).draggable(false));
            }
            else{
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                            .snippet(csvData).draggable(false));
            }
        }

        else if (landmark.getType().equals(Constants.LISTED_BUILDINGS_ID)
        && this instanceof ListedBuildingMapFragment){

            type = getString(R.string.listedbuilding);
            csvData +=type+"@@"+webUrl;
            if (buildingIcon!=null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(buildingIcon);
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .icon(bitmapDescriptor)
                        .snippet(csvData).draggable(false));
            }
            else{
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .snippet(csvData).draggable(false));
            }
        }

        else if (landmark.getType().equals(Constants.PARKS_AND_GARDENS_ID)
        && this instanceof ListedBuildingMapFragment){

            type = getString(R.string.park);
            csvData +=type+"@@"+webUrl;
            if (parkIcon!=null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(parkIcon);
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .icon(bitmapDescriptor)
                        .snippet(csvData).draggable(false));
            }
            else{
                tempMarker = gMap.addMarker(new MarkerOptions().position(entLatLng)
                        .snippet(csvData).draggable(false));
            }
        }

        else if (landmark.getType().equals(Constants.BLUE_PLAQUES) && this instanceof BluePlaqueMapFragment){

            type = getString(R.string.blue_plaque);
            csvData +=type+"@@"+webUrl;
            tempMarker = gMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(entLatLng)
                        .snippet(csvData).draggable(false));

        }
        try {

            if (tempMarker != null && newId!= null && !newId.isEmpty()) {

                if (tempMarker.getSnippet()!=null) {
                    if (tempMarker.getSnippet().startsWith(newId)){
                        tempMarker.showInfoWindow();
                    }
                }
            }
        }
        catch (Exception e){
            Timber.e(e);
        }

    }

    @Override
    public void foundLatLng(LatLng latLng) {
        newLatLng = latLng;
        gMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng), 700, null);
    }

    @Override
    public void onError() {
        Toast.makeText(getContext(), "Couldn't find any location which matched your search term", Toast.LENGTH_LONG).show();

    }

    public void showBottomSheet(){

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity!=null) {
            mainActivity.showBottomSheet();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location!=null && gMap != null && newLatLng == null) {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            if (currentLocationMarker == null){
                currentLocationMarker = gMap.addMarker(new MarkerOptions().position(latLng).icon(locationBitmapDescriptor));
            }
            else{
                currentLocationMarker.setPosition(latLng);
            }

            if (!updatedLocationHasBeenSet && gMap!=null){

                int currentApproxLat = (int) (currentLatLng.latitude * 10);
                int newApproxLat = (int) (location.getLatitude() * 10);
                int currentApproxLng = (int) (currentLatLng.longitude * 10);
                int newApproxLng = (int) (location.getLongitude() * 10);
                if ((currentApproxLat != newApproxLat) || (currentApproxLng != newApproxLng)) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (newLatLng == null) {
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    }
                    updatedLocationHasBeenSet = true;
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
