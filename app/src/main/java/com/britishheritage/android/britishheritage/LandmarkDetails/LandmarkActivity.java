package com.britishheritage.android.britishheritage.LandmarkDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.LandmarkDetails.adapters.WikiLandmarkAdapter;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.WikiLandmark;
import com.britishheritage.android.britishheritage.R;
import com.britishheritage.android.britishheritage.WebActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class LandmarkActivity extends AppCompatActivity implements WikiLandmarkAdapter.OnWikiLandmarkClickListener, OnMapReadyCallback {

    private Landmark mainLandmark;
    private SupportMapFragment landmarkMapFragment;
    private FloatingActionButton checkInButton;
    private TextView checkInTV;
    private TextView landmarkTitleTV;
    private RecyclerView geoNamesRecyclerView;
    private TextView userSuggestionsTitleTV;
    private RecyclerView userDescriptionsRecyclerView;
    private LandmarkViewModel landmarkViewModel;
    private RecyclerView.LayoutManager layoutManager;
    public static final String WIKI_URL_KEY = "british_heritage_wiki_url";

    private GoogleMap gMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mainLandmark = bundle.getParcelable(BaseActivity.LANDMARK_PARCEL);
        }
        if (mainLandmark == null){
            Timber.d("Landmark failed to be unwrapped");
            onDestroy();
        }
        checkInButton = findViewById(R.id.landmark_check_in_button);
        checkInTV = findViewById(R.id.landmark_check_in_button_text);
        landmarkTitleTV = findViewById(R.id.landmark_title);
        geoNamesRecyclerView = findViewById(R.id.landmark_geonames_recylerview);
        userSuggestionsTitleTV = findViewById(R.id.landmark_suggestions);
        userDescriptionsRecyclerView = findViewById(R.id.landmark_user_descriptions_recylerview);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

        String name = mainLandmark.getName();
        if (name!=null) {
            landmarkTitleTV.setText(name);
            if (getSupportActionBar()!=null) {
                getSupportActionBar().setTitle(name);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        landmarkViewModel = ViewModelProviders.of(this).get(LandmarkViewModel.class);
        LatLng latLng = new LatLng(mainLandmark.latitude, mainLandmark.longitude);
        landmarkViewModel.getWikiGeocodeData(latLng);
        landmarkViewModel.getWikiLandmarkLiveData().observe(this, this::processWikiLandmarkLiveData);

        landmarkMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.landmark_map);
        landmarkMapFragment.getMapAsync(this);
    }



    private void processWikiLandmarkLiveData(List<WikiLandmark> wikiLandmarkList){

        WikiLandmarkAdapter wikiLandmarkAdapter = new WikiLandmarkAdapter(wikiLandmarkList, this, this);
        geoNamesRecyclerView.setLayoutManager(layoutManager);
        layoutManager.offsetChildrenHorizontal(40);
        geoNamesRecyclerView.setAdapter(wikiLandmarkAdapter);
    }


    @Override
    public void onItemClick(String url) {
        //todo set up on click
        Intent wikiWebViewIntent = new Intent(this, WebActivity.class);
        wikiWebViewIntent.putExtra(WIKI_URL_KEY, url);
        startActivity(wikiWebViewIntent);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng locationLatLng = new LatLng(mainLandmark.getLatitude(), mainLandmark.getLongitude());
        gMap = googleMap;
        gMap.setMinZoomPreference(13);
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 17));
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setAllGesturesEnabled(false);
        gMap.setPadding(10, 10, 14 ,30);
        gMap.addMarker(new MarkerOptions().position(locationLatLng).alpha(new Float(0.5)));

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        float zoomLevel = gMap.getCameraPosition().zoom;
        if (zoomLevel <= 14.0){
            gMap.getUiSettings().setZoomControlsEnabled(false);
        }
        else{
            gMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }
}
