package com.britishheritage.android.britishheritage.LandmarkDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Global.MyLocationProvider;
import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.LandmarkDetails.adapters.LandmarkReviewAdapter;
import com.britishheritage.android.britishheritage.LandmarkDetails.adapters.WikiLandmarkAdapter;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.Review;
import com.britishheritage.android.britishheritage.Model.WikiLandmark;
import com.britishheritage.android.britishheritage.R;
import com.britishheritage.android.britishheritage.WebActivity;
import com.firebase.geofire.core.GeoHash;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.Dash;
import com.google.android.libraries.maps.model.Gap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.LatLngBounds;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PatternItem;
import com.google.android.libraries.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LandmarkActivity extends BaseActivity implements WikiLandmarkAdapter.OnWikiLandmarkClickListener,
        LandmarkReviewAdapter.ReviewViewHolder.OnClickListener,
        OnMapReadyCallback,
        AddReviewDialogFragment.DialogListener,
        LocationListener {

    private FirebaseUser user;
    private Landmark mainLandmark;
    private SupportMapFragment landmarkMapFragment;
    private Button checkInButton;
    private TextView checkInTV;
    private ImageView checkInStarIV;
    private ImageView favouriteIcon;
    private TextView landmarkTitleTV;
    private TextView bluePlaqueTV;
    private TextView pointOfInterestTitleTV;
    private RecyclerView geoNamesRecyclerView;
    private TextView userReviewsTitleTV;
    private RecyclerView userReviewsRecyclerView;
    private LandmarkViewModel landmarkViewModel;
    private RecyclerView.LayoutManager wikiLayoutManager;
    private RecyclerView.LayoutManager reviewLayoutManager;
    private Button visitWebsiteButton;
    public static final String WIKI_URL_KEY = "british_heritage_wiki_url";
    private List<Review> reviewList = new ArrayList<>();
    private Toolbar toolbar;
    private DatabaseInteractor databaseInteractor;
    private Drawable isFavouriteDrawable;
    private Drawable notFavouriteDrawable;
    private Drawable starDrawable;
    //animated image views
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private View goldShimmerView;
    //user location
    private LatLng userLatLng;
    //initial LatLngBounds
    private LatLngBounds initialLatLngBounds;

    private GoogleMap gMap;
    public static int LANDMARK_EXITED = 452;

    public static int CHECK_IN_BONUS = 10;
    public static int WRITE_REVIEW_BONUS = 5;
    public static int BLUE_PLAQUE_CHECK_IN = 1;
    public static int SCHEDULED_MON_BONUS = 30;


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
        databaseInteractor = DatabaseInteractor.getInstance(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        checkInButton = findViewById(R.id.landmark_check_in_button);
        checkInTV = findViewById(R.id.landmark_check_in_button_text);
        checkInStarIV = findViewById(R.id.star_award_image_view);
        landmarkTitleTV = findViewById(R.id.landmark_title);
        pointOfInterestTitleTV = findViewById(R.id.landmark_point_of_interest);
        geoNamesRecyclerView = findViewById(R.id.landmark_geonames_recylerview);
        userReviewsTitleTV = findViewById(R.id.landmark_suggestions);
        toolbar = findViewById(R.id.landmark_activity_toolbar);
        favouriteIcon = findViewById(R.id.favourite_icon);
        bluePlaqueTV = findViewById(R.id.blue_plaque_title);
        userReviewsRecyclerView = findViewById(R.id.landmark_user_descriptions_recylerview);
        wikiLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        reviewLayoutManager  = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        isFavouriteDrawable = getDrawable(R.drawable.favourite_heart_full);
        notFavouriteDrawable = getDrawable(R.drawable.favourite_heart_empty);
        starDrawable = getDrawable(R.drawable.star_drawable);
        visitWebsiteButton = findViewById(R.id.landmark_visit_website_button);

        star1 = findViewById(R.id.star_award_image_view1);
        star2 = findViewById(R.id.star_award_image_view2);
        star3 = findViewById(R.id.star_award_image_view3);
        star4 = findViewById(R.id.star_award_image_view4);
        goldShimmerView = findViewById(R.id.gold_shimmer_view);

        setUpToolbar();

        landmarkViewModel = ViewModelProviders.of(this).get(LandmarkViewModel.class);
        LatLng latLng = new LatLng(mainLandmark.latitude, mainLandmark.longitude);
        landmarkViewModel.getWikiGeocodeData(latLng);
        landmarkViewModel.getWikiLandmarkLiveData().observe(this, this::processWikiLandmarkLiveData);
        landmarkViewModel.getReviewsLiveData(mainLandmark).observe(this, this::processReviewLiveData);

        landmarkMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.landmark_map);
        landmarkMapFragment.getMapAsync(this);

        setUpFavouriteButton();
        boolean isPreviouslyCheckedIn = databaseInteractor.isCheckedInLandmark(mainLandmark.getId());
        if (isPreviouslyCheckedIn){
            checkInStarIV.setVisibility(View.VISIBLE);
            checkInTV.setText(R.string.check_in_message);
            checkInTV.setTextColor(getResources().getColor(R.color.gold));
            checkInTV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else {
            checkInStarIV.setVisibility(View.INVISIBLE);
            setUpCheckInButton();
        }

        visitWebsiteButton.setOnClickListener(v->{
            String webUrl = mainLandmark.getWebUrl();
            if (webUrl!=null){
                Intent wikiWebViewIntent = new Intent(this, WebActivity.class);
                wikiWebViewIntent.putExtra(WIKI_URL_KEY, webUrl);
                startActivity(wikiWebViewIntent);
            }
        });
    }

    private void setUpCheckInButton() {

        checkInButton.setOnClickListener(v->{

            checkInStarIV.setVisibility(View.VISIBLE);
            RotateDrawable rotateDrawable = (RotateDrawable) checkInStarIV.getBackground();
            ObjectAnimator anim = ObjectAnimator.ofInt(rotateDrawable, "level", 0, 10000);
            anim.setDuration(750);
            anim.setRepeatCount(ValueAnimator.REVERSE);
            anim.start();

            if (gMap!=null) {
                if (userLatLng!=null && initialLatLngBounds!=null) {
                    boolean userIsWithinArea = initialLatLngBounds.contains(userLatLng);
                    if (!userIsWithinArea){
                        //for some phones LatLngBounds doesn't exist so we do the calculation using geohashing instead
                        //using 6 characters for a radius of 600 metres
                        GeoHash landmarkGeoHash = new GeoHash(mainLandmark.getLatitude(), mainLandmark.getLongitude());
                        GeoHash userGeoHash = new GeoHash(userLatLng.latitude, userLatLng.longitude);
                        String landmarkGeoHashStr = landmarkGeoHash.getGeoHashString();
                        String userGeoHashStr = userGeoHash.getGeoHashString();
                        if (landmarkGeoHashStr!=null & landmarkGeoHashStr.length() >= 6) {
                            landmarkGeoHashStr = landmarkGeoHashStr.substring(0, 6);
                        }
                        if (userGeoHashStr!=null && userGeoHashStr.length() >= 6) {
                            userGeoHashStr = userGeoHashStr.substring(0, 6);
                        }
                        if (landmarkGeoHashStr.equals(userGeoHashStr)){
                            userIsWithinArea = true;
                        }
                    }
                    if (userIsWithinArea) {
                        checkIn(anim);
                    } else {
                        String checkInFailure = getString(R.string.not_within_area);
                        showSnackbar(checkInFailure);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkInStarIV.setVisibility(View.INVISIBLE);
                            }
                        }, 2000);

                    }
                }
                else{
                    showSnackbar(getString(R.string.something_went_wrong));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkInStarIV.setVisibility(View.INVISIBLE);
                        }
                    }, 2000);
                }
            }
            else {
                showSnackbar(getString(R.string.something_went_wrong));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkInStarIV.setVisibility(View.INVISIBLE);
                    }
                }, 2000);
            }

        });
    }

    private void animateSuccessfulCheckIn(ObjectAnimator anim){

        if (mainLandmark.getType().equalsIgnoreCase(getString(R.string.blue_plaque)) || mainLandmark.getType().equalsIgnoreCase(Constants.BLUE_PLAQUES)){
            addPoints(BLUE_PLAQUE_CHECK_IN);
        }
        else if (mainLandmark.getType().equalsIgnoreCase(getString(R.string.scheduled_monument)) || mainLandmark.getType().equalsIgnoreCase(Constants.SCHEDULED_MONUMENTS_ID)){
            addPoints(SCHEDULED_MON_BONUS);
        }
        else {
            addPoints(CHECK_IN_BONUS);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                anim.end();
                star1.setVisibility(View.VISIBLE);
                star1.setAlpha(1f);
                star1.animate().alpha(0).setDuration(800);
                goldShimmerView.setVisibility(View.VISIBLE);
                goldShimmerView.animate().translationXBy(-1f * checkInTV.getWidth()).setDuration(1000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        star2.setVisibility(View.VISIBLE);
                        star2.setAlpha(1f);
                        star2.animate().alpha(0).setDuration(600);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                star3.setVisibility(View.VISIBLE);
                                star3.setAlpha(1f);
                                star3.animate().alpha(0).setDuration(400);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        star4.setVisibility(View.VISIBLE);
                                        star4.setAlpha(1f);
                                        star4.animate().scaleX(100f).scaleY(100f).setDuration(1000).setListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                        checkInTV.setText(R.string.check_in_message);
                                        checkInTV.setTextColor(getResources().getColor(R.color.gold));
                                        checkInTV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    }
                                }, 100);
                            }
                        }, 100);
                    }
                }, 100);

            }
        }, 1500);
    }

    private void checkIn(ObjectAnimator anim){

        if (mainLandmark!=null) {
            OnCompleteListener listener = new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        animateSuccessfulCheckIn(anim);
                        checkInButton.setOnClickListener(null);

                    }
                    else {
                        checkInStarIV.setVisibility(View.INVISIBLE);
                        showSnackbar(getString(R.string.something_went_wrong));
                    }
                }
            };
            databaseInteractor.checkInToLandmark(mainLandmark.getId(), listener);
        }
    }

    private void addPoints(int points){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int checkIn = 0;
                int review = 0;
                if (points == CHECK_IN_BONUS || points == BLUE_PLAQUE_CHECK_IN || points == SCHEDULED_MON_BONUS){
                    checkIn = 1;
                }
                else if (points == WRITE_REVIEW_BONUS){
                    review = 1;
                }
                databaseInteractor.addPoints(user, points, review, checkIn).observe(LandmarkActivity.this, status->{
                    if (status == DatabaseInteractor.Status.SUCCESS){
                        if (points == BLUE_PLAQUE_CHECK_IN){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showSnackbar(getString(R.string.point_added, points));
                                }
                            }, 2000);

                        }
                        else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showSnackbar(getString(R.string.points_added, points));
                                }
                            }, 2000);

                        }
                    }
                });
            }
        }, 100);

    }

    private void setUpFavouriteButton() {

        favouriteIcon.setOnClickListener(v->{
            if (databaseInteractor.isFavourite(mainLandmark.getId())){
                databaseInteractor.removeFavourite(mainLandmark.getId());
                favouriteIcon.setImageDrawable(notFavouriteDrawable);
            }
            else{
                databaseInteractor.addFavourite(mainLandmark);
                favouriteIcon.setImageDrawable(isFavouriteDrawable);
            }
        });
    }

    private void setUpToolbar(){
        String name = "";
        if (!mainLandmark.getType().equalsIgnoreCase(getString(R.string.blue_plaque)) && !mainLandmark.getType().equalsIgnoreCase(Constants.BLUE_PLAQUES)) {
            name = Tools.formatTitle(mainLandmark.getName());
            if (name.contains("\u0027")) {
                name = Tools.convertToTitleCase(name);
            }
            landmarkTitleTV.setText(name);
        }
        else{
            String[] nameArray = mainLandmark.getName().split("!!");
            if (nameArray.length > 1)
            name = Tools.convertToTitleCase(nameArray[0]);
            String description = nameArray[1];
            bluePlaqueTV.setVisibility(View.VISIBLE);
            bluePlaqueTV.setText(name);
            landmarkTitleTV.setText(description);
        }
        if (name!=null) {

            setSupportActionBar(toolbar);
            if (getSupportActionBar()!=null) {
                final Drawable upArrow = getResources().getDrawable(R.drawable.baseline_arrow_back_white_24);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                getSupportActionBar().setTitle(name);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        boolean isFavourite = databaseInteractor.isFavourite(mainLandmark.getId());
        if (isFavourite){
            favouriteIcon.setImageDrawable(isFavouriteDrawable);
        }
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
        geoNamesRecyclerView.setVisibility(View.VISIBLE);
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

        gMap = googleMap;
        MyLocationProvider.addLocationListener(this, this);
        Location lastLocation = MyLocationProvider.getLastLocation(this);
        if (lastLocation != null){
            userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        boolean locationPermissions = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            locationPermissions = MyLocationProvider.hasLocationPermissions(this);
        }
        if (locationPermissions) {
            gMap.setMyLocationEnabled(true);
        }
        LatLng locationLatLng = new LatLng(mainLandmark.getLatitude(), mainLandmark.getLongitude());
        gMap.setMinZoomPreference(13);
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 17));
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setAllGesturesEnabled(false);
        gMap.setPadding(10, 10, 14 ,30);
        gMap.addMarker(new MarkerOptions().position(locationLatLng).alpha(new Float(0.5)));

        if (mainLandmark.getType().equalsIgnoreCase(getString(R.string.blue_plaque)) || mainLandmark.getType().equalsIgnoreCase(Constants.BLUE_PLAQUES)){
            gMap.getUiSettings().setZoomControlsEnabled(false);
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 18));
        }
        else{
            gMap.getUiSettings().setZoomControlsEnabled(true);
        }

        initialLatLngBounds = gMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng neLatLng = initialLatLngBounds.northeast;
        LatLng swLatLng = initialLatLngBounds.southwest;
        LatLng seLatLng = new LatLng(swLatLng.latitude, neLatLng.longitude);
        LatLng nwLatLng = new LatLng(neLatLng.latitude, swLatLng.longitude);

        try {
            List<LatLng> latLngList = new ArrayList<>();
            latLngList.add(nwLatLng);
            latLngList.add(neLatLng);
            latLngList.add(seLatLng);
            latLngList.add(swLatLng);
            latLngList.add(nwLatLng);

            List<PatternItem> patternItemList = new ArrayList<>();
            patternItemList.add(new Dash(20));
            patternItemList.add(new Gap(30));
            gMap.addPolyline(new PolylineOptions().pattern(patternItemList).width(3).addAll(latLngList).color(Color.argb(255, 255, 255, 255)));
        }
        catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public void onUserInteraction() {

        try {
            super.onUserInteraction();
            if (gMap!=null && gMap.getCameraPosition()!=null) {
                float zoomLevel = gMap.getCameraPosition().zoom;
                if (zoomLevel <= 14.0) {
                    gMap.getUiSettings().setZoomControlsEnabled(false);
                } else {
                    if (!mainLandmark.getType().equalsIgnoreCase(getString(R.string.blue_plaque)) && !mainLandmark.getType().equalsIgnoreCase(Constants.BLUE_PLAQUES)) {
                        gMap.getUiSettings().setZoomControlsEnabled(true);
                    }
                }
            }
        }
        catch (Exception e){
            Timber.e(e);
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
        addPoints(WRITE_REVIEW_BONUS);

    }

    @Override
    public void onBackPressed() {
        setResult(LANDMARK_EXITED);
        MyLocationProvider.removeLocationListeners(this);
        super.onBackPressed();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (gMap!=null){
            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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
