package com.britishheritage.android.britishheritage.LandmarkDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.os.Bundle;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Main.FavouritesAdapter;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.WikiLandmark;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class LandmarkActivity extends AppCompatActivity implements WikiLandmarkAdapter.OnWikiLandmarkClickListener {

    private Landmark mainLandmark;
    private Fragment landmarkMapFragment;
    private FloatingActionButton checkInButton;
    private TextView checkInTV;
    private TextView landmarkTitleTV;
    private RecyclerView geoNamesRecyclerView;
    private TextView userSuggestionsTitleTV;
    private RecyclerView userDescriptionsRecyclerView;
    private LandmarkViewModel landmarkViewModel;
    private RecyclerView.LayoutManager layoutManager;


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
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }



        landmarkViewModel = ViewModelProviders.of(this).get(LandmarkViewModel.class);
        LatLng latLng = new LatLng(mainLandmark.latitude, mainLandmark.longitude);
        landmarkViewModel.getWikiGeocodeData(latLng);
        landmarkViewModel.getWikiLandmarkLiveData().observe(this, this::processWikiLandmarkLiveData);
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
    }
}
