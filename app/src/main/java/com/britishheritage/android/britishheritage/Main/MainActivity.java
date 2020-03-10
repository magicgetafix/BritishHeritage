package com.britishheritage.android.britishheritage.Main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.MyLocationProvider;
import com.britishheritage.android.britishheritage.Home.HomeFragment;
import com.britishheritage.android.britishheritage.LandmarkDetails.LandmarkActivity;
import com.britishheritage.android.britishheritage.Main.Dialogs.BottomDialogFragment;
import com.britishheritage.android.britishheritage.Maps.ArchMapFragment;
import com.britishheritage.android.britishheritage.Maps.ListedBuildingMapFragment;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.LandmarkList;
import com.britishheritage.android.britishheritage.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

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
    private Fragment previousFragment;
    private Fragment homeFragment;
    private BottomDialogFragment bottomDialogFragment;
    private DatabaseInteractor databaseInteractor;
    private ProgressBar progressBar;
    private LiveData<Integer> databaseSizeLiveData;
    //private FirebaseDatabase database;
    public static Landmark lastClickedLandmark = null;
    private int currentMenuId = -999999999;

    //To set up BottomNavigationView on select behaviour
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            if (menuItem.getItemId() == currentMenuId){
                return false;
            }
            switch (menuItem.getItemId()){

                case (R.id.arch_map): selectedFragment = getArchMapFragment();
                break;

                case (R.id.listed_buildings_map): selectedFragment = getListedBuildingFragment();
                break;

                case (R.id.home): selectedFragment = getHomeFragment();
            }

            if (selectedFragment!= null) {
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
            }
            currentMenuId = menuItem.getItemId();
            return true;
        }
    };


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
        databaseSizeLiveData.observe(this, this::populateDatabase);

    }


    private void populateDatabase(int databaseSize){
        Timber.d("Database size is: "+databaseSize);
        if (databaseSize == 0){

            Observable.just(0).doOnNext(o->{
                String jsonDatabaseString = loadJSONFromAsset();
                Gson gson = new Gson();
                LandmarkList landmarkList = gson.fromJson(jsonDatabaseString, LandmarkList.class);
                if (landmarkList!=null) {
                    databaseInteractor.addAllLandmarks(landmarkList.getLandmarks());
                    o++;
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(finished->{
                progressBar.setVisibility(View.INVISIBLE);
                navigationView.setOnNavigationItemSelectedListener(navListener);
                navigationView.setSelectedItemId(R.id.home);
            });

        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            navigationView.setOnNavigationItemSelectedListener(navListener);
            navigationView.setSelectedItemId(R.id.home);
        }

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("heritage_data.json");
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
            databaseInteractor.addFavourite(lastClickedLandmark);
            bottomDialogFragment.dismiss();
            showSnackbar(getString(R.string.added_to_favourites));
        }

    }

    @Override
    public void onViewDetailsClick() {

        if (lastClickedLandmark!=null){
            navigateWithLandmark(LandmarkActivity.class, lastClickedLandmark);
        }

    }

    public void showSnackbar(String message){
        Snackbar.make(findViewById(R.id.main_frame_layout), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
