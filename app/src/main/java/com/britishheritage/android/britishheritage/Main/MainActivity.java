package com.britishheritage.android.britishheritage.Main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.SparseLongArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Global.MyLocationProvider;
import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.Home.HomeFragment;
import com.britishheritage.android.britishheritage.LandmarkDetails.LandmarkActivity;
import com.britishheritage.android.britishheritage.Main.Dialogs.BottomDialogFragment;
import com.britishheritage.android.britishheritage.Maps.ArchMapFragment;
import com.britishheritage.android.britishheritage.Maps.BaseMapFragment;
import com.britishheritage.android.britishheritage.Maps.BluePlaqueMapFragment;
import com.britishheritage.android.britishheritage.Maps.ListedBuildingMapFragment;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.LandmarkList;
import com.britishheritage.android.britishheritage.R;
import com.britishheritage.android.britishheritage.SplashActivity;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements BottomDialogFragment.ItemClickListener {

    private FrameLayout frameLayout;
    private BottomNavigationView navigationView;
    private Fragment selectedFragment;
    private Fragment archMapFragment;
    private Fragment listedBuildingFragment;
    private Fragment bluePlaqueFragment;
    private Fragment previousFragment;
    private Fragment homeFragment;
    private BottomDialogFragment bottomDialogFragment;
    private DatabaseInteractor databaseInteractor;
    private ProgressBar progressBar;
    private LiveData<Integer> databaseSizeLiveData;
    private MainViewModel mainViewModel;
    //private FirebaseDatabase database;
    public static Landmark lastClickedLandmark = null;
    private int currentMenuId = -999999999;
    private View tempWhite;
    private ImageView logoTempView;

    //To set up BottomNavigationView on select behaviour
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            if (menuItem.getItemId() == currentMenuId){
                //resets back to current location on bottom tap
                if (previousFragment!=null && previousFragment instanceof BaseMapFragment){
                    ((BaseMapFragment) previousFragment).navBackToCurrentLatLng();
                }
                return true;
            }
            if (previousFragment!=null && previousFragment instanceof BaseMapFragment){
                ((BaseMapFragment) previousFragment).resetMap();
            }
            switch (menuItem.getItemId()){

                case (R.id.arch_map): selectedFragment = getArchMapFragment();
                break;

                case (R.id.listed_buildings_map): selectedFragment = getListedBuildingFragment();
                break;

                case (R.id.blue_plaques_map): selectedFragment = getBluePlaqueFragment();
                break;

                case (R.id.home): selectedFragment = getHomeFragment();
            }

            if (selectedFragment!= null) {
                executeFragmentTransaction(selectedFragment);
            }
            currentMenuId = menuItem.getItemId();
            return true;
        }
    };

    private void executeFragmentTransaction(Fragment selectedFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        if (selectedFragment.isHidden()){
            if (previousFragment!=null){
                fragmentTransaction.hide(previousFragment);
            }
            fragmentTransaction.show(selectedFragment);
        }
        else{
            if (previousFragment!=null){
                fragmentTransaction.hide(previousFragment);
            }
            fragmentTransaction.add(R.id.main_frame_layout, selectedFragment);
        }
        previousFragment = selectedFragment;
        fragmentTransaction.commit();
        navigationView.setOnNavigationItemSelectedListener(navListener);
    }


    private Fragment getArchMapFragment(){

        if (archMapFragment==null){
            archMapFragment = ArchMapFragment.newInstance();
        }
        return archMapFragment;
    }

    private Fragment getListedBuildingFragment(){

        if (listedBuildingFragment == null){
            listedBuildingFragment = new ListedBuildingMapFragment();
        }
        return listedBuildingFragment;
    }

    private Fragment getBluePlaqueFragment(){

        if (bluePlaqueFragment == null){
            bluePlaqueFragment = new BluePlaqueMapFragment();
        }
        return bluePlaqueFragment;
    }

    private Fragment getHomeFragment(){

        if (homeFragment == null){
            homeFragment = new HomeFragment();
        }

        return homeFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.main_frame_layout);
        navigationView = findViewById(R.id.main_navigation);
        progressBar = findViewById(R.id.main_progress_bar);

        Location location = MyLocationProvider.getLastLocation(this);
        databaseInteractor = DatabaseInteractor.getInstance(getApplicationContext());
        databaseSizeLiveData = databaseInteractor.getDatabaseSize();
        databaseSizeLiveData.observe(this, size -> {populateDatabase(size, "formatted_heritage_data_with_urls.json", true);});
        navigationView.setBackgroundColor(getResources().getColor(R.color.white));

        tempWhite = findViewById(R.id.white_temp_view);
        logoTempView = findViewById(R.id.logo_temp_view);

        Tools.animateToTransAlpha(tempWhite, 3000);
        Tools.animateToTransAlpha(logoTempView, 2500);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setElevation(0f);
        }

        if (!databaseInteractor.grade2BuildingsHaveBeenAdded()) {
            populateDatabase(0, "grade_2_buildings.json", false);
        }
        if (!databaseInteractor.bluePlaquesHaveBeenAdded()) {
            populateDatabase(0, "blue_plaques_formatted_with_wiki.json", false);
        }

        populateDatabase(0, "additional_landmarks.json", false);


    }

    private void populateDatabase(int databaseSize, String fileName, boolean startUp){
        Timber.d("Database size is: "+databaseSize);
        if (databaseSize == 0){

            Observable.just(0).doOnNext(o->{
                String jsonDatabaseString = loadJSONFromAsset(fileName);
                Gson gson = new Gson();
                LandmarkList landmarkList = gson.fromJson(jsonDatabaseString, LandmarkList.class);
                if (landmarkList!=null) {
                    databaseInteractor.addAllLandmarks(landmarkList.getLandmarks());
                    o++;
                }
            }).doOnError(error->{
                if (databaseInteractor!=null){
                    databaseInteractor.resetDatabaseSharedPrefs();
                }
                Timber.e(error);
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(finished->{
                if (startUp) {
                    progressBar.setVisibility(View.INVISIBLE);
                    navigationView.setOnNavigationItemSelectedListener(navListener);
                    navigationView.setSelectedItemId(R.id.home);
                }
            });

        }
        else{
            if (startUp) {
                progressBar.setVisibility(View.INVISIBLE);
                navigationView.setOnNavigationItemSelectedListener(navListener);
                navigationView.setSelectedItemId(R.id.home);
            }
        }

    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void showBottomSheet() {
        bottomDialogFragment =
                BottomDialogFragment.newInstance();
        bottomDialogFragment.show(getSupportFragmentManager(),
                BottomDialogFragment.TAG);
    }


    @Override
    public void onViewDirectionsClick() {

        Landmark lastClickedLandMark = lastClickedLandmark;

        if (lastClickedLandMark!=null) {

            Double latitude = lastClickedLandMark.getLatitude();
            Double longitude = lastClickedLandMark.getLongitude();
            String name = lastClickedLandMark.getName();

            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, name);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                try {
                    Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(unrestrictedIntent);
                } catch (ActivityNotFoundException innerEx) {
                    Toast.makeText(this, getResources().getString(R.string.please_install_maps), Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    @Override
    public void onAddToFavouritesClick() {

        if (lastClickedLandmark!=null) {
            if (!databaseInteractor.isFavourite(lastClickedLandmark.getId())) {
                databaseInteractor.addFavourite(lastClickedLandmark);
                bottomDialogFragment.dismiss();
                showSnackbar(getString(R.string.added_to_favourites));
            }
            else{
                databaseInteractor.removeFavourite(lastClickedLandmark.getId());
                bottomDialogFragment.dismiss();
                showSnackbar(getString(R.string.removed_from_favourites));
            }
        }

    }

    @Override
    public void onViewDetailsClick() {

        if (lastClickedLandmark!=null){
            navigateWithLandmarkForResult(LandmarkActivity.class, lastClickedLandmark);
        }

    }

    public void showSnackbar(String message){
        Snackbar.make(findViewById(R.id.main_frame_layout), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == LandmarkActivity.LANDMARK_EXITED){
            if (bottomDialogFragment!=null && bottomDialogFragment.isVisible()){
                bottomDialogFragment.dismissAllowingStateLoss();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (navigationView!=null) {
            if (navigationView.getSelectedItemId() != R.id.home) {
                navigationView.setSelectedItemId(R.id.home);
            }
            else{
                finishAffinity();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out){
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
            mainViewModel.deleteFavouritesLocally(currentUser);
            FirebaseAuth.getInstance().signOut();
            Intent splashIntent = new Intent(this, SplashActivity.class);
            startActivity(splashIntent);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    public void navigateWithLandmark(Class<? extends AppCompatActivity> destinationActivity, Landmark landmark, boolean requiresNav) {
        if (!requiresNav) {
            navigateWithLandmark(destinationActivity, landmark);
        }
        else{
            if (landmark!=null) {
                navigationView.setOnNavigationItemSelectedListener(null);
                if (landmark.getType().equalsIgnoreCase(Constants.SCHEDULED_MONUMENTS_ID) || landmark.getType().equalsIgnoreCase(getString(R.string.scheduled_monument))
                || landmark.getType().equalsIgnoreCase(getString(R.string.hillfort)) || landmark.getType().equalsIgnoreCase(Constants.HILLFORTS_ID) ||
                landmark.getType().equalsIgnoreCase(getString(R.string.battlefield)) || landmark.getType().equalsIgnoreCase(Constants.BATTLEFIELDS_ID)) {
                    navigationView.setSelectedItemId(R.id.arch_map);
                    currentMenuId = R.id.arch_map;
                    selectedFragment = getArchMapFragment();
                    ((BaseMapFragment) selectedFragment).setTargetLatLng(new LatLng(landmark.latitude, landmark.longitude), landmark.getId());
                    executeFragmentTransaction(selectedFragment);

                }

                else if (landmark.getType().equalsIgnoreCase(Constants.LISTED_BUILDINGS_ID) || landmark.getType().equalsIgnoreCase(getString(R.string.listedbuilding)) || landmark.getType().equalsIgnoreCase(getString(R.string.listed_building_string))
                        || landmark.getType().equalsIgnoreCase(getString(R.string.park)) || landmark.getType().equalsIgnoreCase(Constants.PARKS_AND_GARDENS_ID)) {
                    navigationView.setSelectedItemId(R.id.listed_buildings_map);
                    selectedFragment = getListedBuildingFragment();
                    currentMenuId = R.id.listed_buildings_map;
                    ((BaseMapFragment) selectedFragment).setTargetLatLng(new LatLng(landmark.latitude, landmark.longitude), landmark.getId());
                    executeFragmentTransaction(selectedFragment);
                }
                else if (landmark.getType().equalsIgnoreCase(Constants.BLUE_PLAQUES) || landmark.getType().equalsIgnoreCase(getString(R.string.blue_plaque))) {
                    navigationView.setSelectedItemId(R.id.blue_plaques_map);
                    selectedFragment = getBluePlaqueFragment();
                    currentMenuId = R.id.blue_plaques_map;
                    ((BaseMapFragment) selectedFragment).setTargetLatLng(new LatLng(landmark.latitude, landmark.longitude), landmark.getId());
                    executeFragmentTransaction(selectedFragment);
                }
            }
        }
    }
}
