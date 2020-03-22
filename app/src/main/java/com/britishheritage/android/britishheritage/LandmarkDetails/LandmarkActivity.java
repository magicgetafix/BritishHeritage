package com.britishheritage.android.britishheritage.LandmarkDetails;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.LandmarkDetails.adapters.LandmarkReviewAdapter;
import com.britishheritage.android.britishheritage.LandmarkDetails.adapters.WikiLandmarkAdapter;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.Review;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LandmarkActivity extends BaseActivity implements WikiLandmarkAdapter.OnWikiLandmarkClickListener, LandmarkReviewAdapter.ReviewViewHolder.OnClickListener, OnMapReadyCallback, AddReviewDialogFragment.DialogListener {

    private FirebaseUser user;
    private Landmark mainLandmark;
    private SupportMapFragment landmarkMapFragment;
    private FloatingActionButton checkInButton;
    private TextView checkInTV;
    private TextView landmarkTitleTV;
    private TextView pointOfInterestTitleTV;
    private RecyclerView geoNamesRecyclerView;
    private TextView userReviewsTitleTV;
    private RecyclerView userReviewsRecyclerView;
    private LandmarkViewModel landmarkViewModel;
    private RecyclerView.LayoutManager wikiLayoutManager;
    private RecyclerView.LayoutManager reviewLayoutManager;
    public static final String WIKI_URL_KEY = "british_heritage_wiki_url";
    private List<Review> reviewList = new ArrayList<>();

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
        user = FirebaseAuth.getInstance().getCurrentUser();
        checkInButton = findViewById(R.id.landmark_check_in_button);
        checkInTV = findViewById(R.id.landmark_check_in_button_text);
        landmarkTitleTV = findViewById(R.id.landmark_title);
        pointOfInterestTitleTV = findViewById(R.id.landmark_point_of_interest);
        geoNamesRecyclerView = findViewById(R.id.landmark_geonames_recylerview);
        userReviewsTitleTV = findViewById(R.id.landmark_suggestions);
        userReviewsRecyclerView = findViewById(R.id.landmark_user_descriptions_recylerview);
        wikiLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        reviewLayoutManager  = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

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
        landmarkViewModel.getReviewsLiveData(mainLandmark).observe(this, this::processReviewLiveData);

        landmarkMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.landmark_map);
        landmarkMapFragment.getMapAsync(this);
    }


    private void processReviewLiveData(List<Review> reviewList){

        userReviewsTitleTV.setVisibility(View.VISIBLE);
        this.reviewList = reviewList;
        userReviewsTitleTV.setVisibility(View.VISIBLE);
        reviewLayoutManager.offsetChildrenHorizontal(40);
        userReviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        LandmarkReviewAdapter reviewAdapter = new LandmarkReviewAdapter(this.reviewList, this, this, this);
        userReviewsRecyclerView.setAdapter(reviewAdapter);
    }

    private void processWikiLandmarkLiveData(List<WikiLandmark> wikiLandmarkList){

        pointOfInterestTitleTV.setVisibility(View.VISIBLE);
        WikiLandmarkAdapter wikiLandmarkAdapter = new WikiLandmarkAdapter(wikiLandmarkList, this, this);
        geoNamesRecyclerView.setLayoutManager(wikiLayoutManager);
        wikiLayoutManager.offsetChildrenHorizontal(40);
        geoNamesRecyclerView.setAdapter(wikiLandmarkAdapter);
    }


    @Override
    public void onItemClick(String url) {
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

    @Override
    public void addReview() {
        if (mainLandmark!=null && user!=null){
            AddReviewDialogFragment reviewDialogFragment = AddReviewDialogFragment.newInstance(mainLandmark.getName(), mainLandmark, user.getUid(), user.getDisplayName());
            FragmentManager fragmentManager = getSupportFragmentManager();
            reviewDialogFragment.show(fragmentManager, "add_review_dialog");
            reviewDialogFragment.setListener(this);

        }
    }

    @Override
    public void upvoted(Review review) {
        landmarkViewModel.upvoteReview(review, mainLandmark);
    }

    @Override
    public void downvoted(Review review) {
        landmarkViewModel.downvoteReview(review, mainLandmark);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainLandmark!=null){
            landmarkViewModel.getReviews(mainLandmark);
        }
    }

    @Override
    public void onDismiss(Review review) {
        for (int i = 0; i < this.reviewList.size(); i++){
            Review newReview = this.reviewList.get(i);
            if (newReview.isPlaceholder()){
                reviewList.remove(i);
                reviewList.add(i, review);
            }
        }
        if (userReviewsRecyclerView.getAdapter() != null) {
            userReviewsRecyclerView.getAdapter().notifyDataSetChanged();
        }

    }
}
