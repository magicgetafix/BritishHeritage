package com.britishheritage.android.britishheritage.Maps;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
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
import com.britishheritage.android.britishheritage.Maps.LocationQueries.LatLngQuery;
import com.britishheritage.android.britishheritage.Maps.MapAdapters.MapInfoWindow;
import com.britishheritage.android.britishheritage.Model.*;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import im.delight.android.location.SimpleLocation;
import timber.log.Timber;

import java.util.*;

public class ArchaeologyMapFragment extends Fragment implements OnMapReadyCallback, LatLngQuery.LatLngResultListener {

    private MapViewModel mViewModel;
    private SupportMapFragment supportMapFragment;
    private GoogleMap gMap;
    private LatLng currentLatLng;
    private static LatLng newLatLng;

    private static Bitmap hillIcon;
    private static Bitmap monumentIcon;
    private static Bitmap battleIcon;

    private EditText searchText;
    private ImageView searchIcon;
    private LatLngQuery latLngQuery;

    public static ArchaeologyMapFragment newInstance(LatLng targetLatLng) {
        newLatLng = targetLatLng;
        return new ArchaeologyMapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.archaeology_map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        searchIcon = view.findViewById(R.id.arch_search_button);
        searchText = view.findViewById(R.id.arch_searchbar);

        supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_archaeology);
        supportMapFragment.getMapAsync(this);
        Location location = MyLocationProvider.getLastLocation(getActivity());
        if (newLatLng==null) {
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else{
            currentLatLng = newLatLng;
        }

        //setting up icon size
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_hill_silhouette_small);
        hillIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 20, 20, false);
        bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_battlefield);
        battleIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 20, 20, false);
        bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_menhir);
        monumentIcon = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 20, 20, false);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = searchText.getText().toString();
                searchString = searchString.trim();
                if (!searchString.isEmpty()) {
                    searchForLatLng(searchString);
                }
                else{
                    Toast.makeText(getContext(), "Please type a location name into the search bar", Toast.LENGTH_LONG).show();
                }
            }
        });
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
        gMap.setMinZoomPreference(11);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        //moves map camera to current location
        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        gMap.setInfoWindowAdapter(new MapInfoWindow(getContext()));

        for (Landmark landmark: mViewModel.getBattleFieldSet()){
            setUpMarker(landmark);
        }
        for (Landmark landmark: mViewModel.getHillfortsSet()){
            setUpMarker(landmark);
        }
        for (Landmark landmark: mViewModel.getScheduledMonumentsSet()){
            setUpMarker(landmark);
        }

        setDragMapBehaviour(gMap);

        LatLng[] latLngs = getMapCorners(gMap);
        mViewModel.getScheduledMonumentLiveData().observe(this, this::setUpMarker);
        mViewModel.getHillfortLiveData().observe(this, this::setUpMarker);
        mViewModel.getBattleFieldLiveData().observe(this, this::setUpMarker);

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

                String snippetData = marker.getSnippet();
                String[] snippetCSV = snippetData.split("//");
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

                currentLatLng = gMap.getCameraPosition().target;
                LatLng[] latLngs = getMapCorners(gMap);
                mViewModel.getLandmarks(latLngs[0], latLngs[1]);
            }
        });
    }

    private void setUpMarker(Landmark landmark){

        if (landmark == null){
            Timber.d("Landmark is null in ArchaeologyMapFragment");
            return;
        }

        Double entityLat = landmark.getLatitude();
        Double entityLong = landmark.getLongitude();
        LatLng entLatLng = new LatLng(entityLat, entityLong);

        if (landmark.getType().equals(Constants.SCHEDULED_MONUMENTS_ID)){

            String name = landmark.getName();
            String latitude = entityLat.toString();
            String longitude = entityLong.toString();
            String type = getString(R.string.scheduled_monument);

            String csvData = name+"//"+latitude+"//"+longitude+"//"+type;

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(monumentIcon);
            gMap.addMarker(new MarkerOptions().position(entLatLng)
                    .icon(bitmapDescriptor)
                    .snippet(csvData).draggable(false));
        }

        else if (landmark.getType().equals(Constants.HILLFORTS_ID)){

            String name = landmark.getName();
            String latitude = entityLat.toString();
            String longitude = entityLong.toString();
            String type = getString(R.string.hilfort);

            String csvData = name+"//"+latitude+"//"+longitude+"//"+type;

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(hillIcon);
            gMap.addMarker(new MarkerOptions().position(entLatLng)
                    .icon(bitmapDescriptor)
                    .snippet(csvData).draggable(false));
        }

        else if (landmark.getType().equals(Constants.BATTLEFIELDS_ID)){

            String name = landmark.getName();
            String latitude = entityLat.toString();
            String longitude = entityLong.toString();
            String type = getString(R.string.battlefield);

            String csvData = name+"//"+latitude+"//"+longitude+"//"+type;

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(battleIcon);
            gMap.addMarker(new MarkerOptions().position(entLatLng)
                    .icon(bitmapDescriptor)
                    .snippet(csvData).draggable(false));
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
}
