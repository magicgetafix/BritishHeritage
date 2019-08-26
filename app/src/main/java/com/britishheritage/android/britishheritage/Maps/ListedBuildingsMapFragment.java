package com.britishheritage.android.britishheritage.Maps;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProviders;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Global.MyLocationProvider;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.MapEntity;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import im.delight.android.location.SimpleLocation;
import timber.log.Timber;

import java.util.*;

public class ListedBuildingsMapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mViewModel;
    private SupportMapFragment supportMapFragment;
    private GoogleMap gMap;
    private LatLng currentLatLng;
    private static LatLng newLatLng;

    private static Bitmap buildingIcon;
    private static Bitmap parkIcon;

    private HashMap<String, MapEntity> archaeologyHashMap = new HashMap<>();


    public static ListedBuildingsMapFragment newInstance(LatLng targetLatLng) {
        newLatLng = targetLatLng;
        return new ListedBuildingsMapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listed_buildings_map_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);



        supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_listed_buildings);
        supportMapFragment.getMapAsync(this);
        // construct a new instance of SimpleLocation
        //todo SimpleLocation not working as advertised


        currentLatLng = MyLocationProvider.getLastLocationLatLng(getActivity());
        //setting up icon size
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_museum_medium);
        buildingIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 30, 30, false);
        bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_park);
        parkIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 30, 30, false);

    }


    @Override
    public void onResume() {
        super.onResume();
        SimpleLocation location = new SimpleLocation(getActivity());
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(getActivity());
        }
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Timber.d("Center latlng: "+currentLatLng.toString());

    }

    //in km
    private double getMapVisibleRadius(GoogleMap googleMap) {
        VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();

        float[] distanceWidth = new float[1];
        float[] distanceHeight = new float[1];

        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        Location.distanceBetween(
                (farLeft.latitude + nearLeft.latitude) / 2,
                farLeft.longitude,
                (farRight.latitude + nearRight.latitude) / 2,
                farRight.longitude,
                distanceWidth
        );

        Location.distanceBetween(
                farRight.latitude,
                (farRight.longitude + farLeft.longitude) / 2,
                nearRight.latitude,
                (nearRight.longitude + nearLeft.longitude) / 2,
                distanceHeight
        );

        double radiusInMeters = Math.sqrt(Math.pow(distanceWidth[0], 2) + Math.pow(distanceHeight[0], 2)) / 2;
        Timber.d("radius in metres: "+radiusInMeters);
        return radiusInMeters/1000;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setMaxZoomPreference(15);
        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        gMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        //moves map camera to current location
        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        setDragMapBehaviour(gMap);
        processMapHashMap(mViewModel.getListedBuildingsHashMap());
        processMapHashMap(mViewModel.getPublicGardensHashMap());

        double radius = getMapVisibleRadius(gMap);
        //mViewModel.getData(currentLatLng, radius);

        //mViewModel.getListedBuildingsKey().observe(this, o->processLiveData(o, Constants.LISTED_BUILDINGS_ID));
        //mViewModel.getPublicGardensKey().observe(this, o->processLiveData(o, Constants.PARKS_AND_GARDENS_ID));


        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String snippetData = marker.getSnippet();
                String[] snippetCSV = snippetData.split(",");
                double latitude = 0.0;
                double longitude = 0.0;
                String name = "";
                if (snippetCSV.length == 4){
                    name = snippetCSV[0];
                    latitude = Double.parseDouble(snippetCSV[1]);
                    longitude = Double.parseDouble(snippetCSV[2]);
                }
                else{
                    Timber.e("Snippet wrong format");
                }

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, name);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try
                {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getContext(), getResources().getString(R.string.please_install_maps), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    public void processMapHashMap(HashMap<String, MapEntity> hashMap){

        Iterator<Map.Entry<String, MapEntity>> mapIterator = hashMap.entrySet().iterator();
        while (mapIterator.hasNext()){
            Map.Entry<String, MapEntity> nextEntry = mapIterator.next();
            if (archaeologyHashMap.get(nextEntry.getKey()) == null){
                archaeologyHashMap.put(nextEntry.getKey(), nextEntry.getValue());
                setUpMarker(nextEntry.getValue());
            }
        }

    }

    public void setDragMapBehaviour(final GoogleMap gMap){

        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                currentLatLng = gMap.getCameraPosition().target;
                double radius = getMapVisibleRadius(gMap);
          //      mViewModel.getData(currentLatLng, radius);
            }
        });
    }


    private void processLiveData(String key, String type){

        if (key!=null) {
            MapEntity mapEntity = archaeologyHashMap.get(key);
            if (mapEntity == null) {
                if (type.equals(Constants.LISTED_BUILDINGS_ID)){
                    MapEntity lbMapEntity = mViewModel.getListedBuildingsHashMap().get(key);
                    archaeologyHashMap.put(key, lbMapEntity);
                    setUpMarker(lbMapEntity);
                }

                else if (type.equals(Constants.PARKS_AND_GARDENS_ID)){
                    MapEntity pgMapEntity = mViewModel.getPublicGardensHashMap().get(key);
                    archaeologyHashMap.put(key, pgMapEntity);
                    setUpMarker(pgMapEntity);
                }
            }
        }
    }

    private void setUpMarker(MapEntity mapEntity){


        Double entityLat = mapEntity.getGeoMarker().getLatitude();
        Double entityLong = mapEntity.getGeoMarker().getLongitude();
        LatLng entLatLng = new LatLng(entityLat, entityLong);
        Landmark landmark = mapEntity.getLandmark();

        if (landmark.getId().equals(Constants.LISTED_BUILDINGS_ID)){

            String name = landmark.getName();
            String latitude = entityLat.toString();
            String longitude = entityLong.toString();
            String type = getString(R.string.listed_building_string);

            String csvData = name+","+latitude+","+longitude+","+type;

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(buildingIcon);
            gMap.addMarker(new MarkerOptions().position(entLatLng)
                    .icon(bitmapDescriptor)
                    .snippet(csvData).draggable(false));
        }

        else if (landmark.getId().equals(Constants.PARKS_AND_GARDENS_ID)){

            String name = landmark.getName();
            String latitude = entityLat.toString();
            String longitude = entityLong.toString();
            String type = getString(R.string.park);

            String csvData = name+","+latitude+","+longitude+","+type;

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(parkIcon);
            gMap.addMarker(new MarkerOptions().position(entLatLng)
                    .icon(bitmapDescriptor)
                    .snippet(csvData).draggable(false));
        }

    }

    /**@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listCounter = 0;
        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_archaeology);
        supportMapFragment.getMapAsync(this);
        currentLatLng = MyLocationProvider.getLastLocationLatLng(getActivity());
        if (newLatLng!=null){
            currentLatLng = newLatLng;
        }
        newLatLng = null;

        //setting up icon size
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_museum_medium);
        buildingIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 30, 30, false);
        bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_park);
        parkIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 30, 30, false);

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setMaxZoomPreference(15);
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        gMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //moves map camera to current location
        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        //subscribes to changes in ViewModel
        mViewModel.getDBLiveData().observe(this, dataList-> processLiveData(dataList));

        LatLngBounds boundsOfMap = gMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng southWest = boundsOfMap.southwest;
        LatLng northEast = boundsOfMap.northeast;

        //requests ViewModel to find locations from database
        getMarkers(southWest, northEast);

        setDragMapBehaviour(gMap);

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String snippetData = marker.getSnippet();
                String[] snippetCSV = snippetData.split(",");
                double latitude = 0.0;
                double longitude = 0.0;
                String name = "";
                if (snippetCSV.length == 4){
                    name = snippetCSV[0];
                    latitude = Double.parseDouble(snippetCSV[1]);
                    longitude = Double.parseDouble(snippetCSV[2]);
                }
                else{
                    Timber.e("Snippet wrong format");
                }

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, name);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try
                {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getContext(), getResources().getString(R.string.please_install_maps), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    public void setDragMapBehaviour(final GoogleMap gMap){

        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLngBounds boundsOfMap = gMap.getProjection().getVisibleRegion().latLngBounds;
                LatLng southWest = boundsOfMap.southwest;
                LatLng northEast = boundsOfMap.northeast;

                mViewModel.getData(southWest, northEast);
            }
        });
    }

    public void getMarkers(LatLng swLatLng, LatLng neLatLng){

        mViewModel.getData(swLatLng, neLatLng);
    }

    private void processLiveData(List<DBEntity> liveDataList){

        listOfBuildingsAndGardens.addAll(liveDataList);

        for (int i = listCounter; i<listOfBuildingsAndGardens.size(); i++){

            listCounter = i;
            setUpMarker(listOfBuildingsAndGardens.get(i));
        }
    }

    private void setUpMarker(DBEntity dbEntity){


        Double entityLat = dbEntity.getLatitude();
        Double entityLong = dbEntity.getLongitude();
        LatLng entLatLng = new LatLng(entityLat, entityLong);

        if (dbEntity.getId().equals(Constants.LISTED_BUILDINGS_ID)){

            String name = dbEntity.getName();
            String latitude = dbEntity.getLatitude().toString();
            String longitude = dbEntity.getLongitude().toString();
            String type = getString(R.string.listedbuilding);

            String csvData = name+","+latitude+","+longitude+","+type;

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(buildingIcon);
            gMap.addMarker(new MarkerOptions().position(entLatLng)
                    .icon(bitmapDescriptor)
                    .snippet(csvData).draggable(false));
        }

        else if (dbEntity.getId().equals(Constants.PARKS_AND_GARDENS_ID)){

            String name = dbEntity.getName();
            String latitude = dbEntity.getLatitude().toString();
            String longitude = dbEntity.getLongitude().toString();
            String type = getString(R.string.park);

            String csvData = name+","+latitude+","+longitude+","+type;

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(parkIcon);
            gMap.addMarker(new MarkerOptions().position(entLatLng)
                    .icon(bitmapDescriptor)
                    .snippet(csvData).draggable(false));
        }

    }**/
}
