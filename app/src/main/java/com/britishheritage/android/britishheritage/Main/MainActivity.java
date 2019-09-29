package com.britishheritage.android.britishheritage.Main;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.MyLocationProvider;
import com.britishheritage.android.britishheritage.Home.HomeFragment;
import com.britishheritage.android.britishheritage.Maps.ArchaeologyMapFragment;
import com.britishheritage.android.britishheritage.Maps.ListedBuildingsMapFragment;
import com.britishheritage.android.britishheritage.Maps.MapViewModel;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.LandmarkList;
import com.britishheritage.android.britishheritage.R;
import com.firebase.geofire.GeoFire;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import im.delight.android.location.SimpleLocation;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView navigationView;
    private Fragment selectedFragment;
    private Fragment archMapFragment;
    private Fragment listedBuildingFragment;
    private Fragment homeFragment;
    private DatabaseInteractor databaseInteractor;
    private ProgressBar progressBar;
    private LiveData<Integer> databaseSizeLiveData;
    //private FirebaseDatabase database;
    //private MapViewModel mapViewModel;


    //To set up BottomNavigationView on select behaviour
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){

                case (R.id.arch_map): selectedFragment = getArchMapFragment();
                break;

                case (R.id.listed_buildings_map): selectedFragment = getListedBuildingFragment();
                break;

                case (R.id.home): selectedFragment = getHomeFragment();
            }

            if (selectedFragment!= null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, selectedFragment).commit();
            }
            return true;
        }
    };


    private Fragment getArchMapFragment(){

        if (archMapFragment==null){
            archMapFragment = new ArchaeologyMapFragment();
        }
        return archMapFragment;
    }

    private Fragment getListedBuildingFragment(){


        if (listedBuildingFragment == null){
            listedBuildingFragment = new ListedBuildingsMapFragment();
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
            String jsonDatabaseString = loadJSONFromAsset();
            Gson gson = new Gson();
            LandmarkList landmarkList = gson.fromJson(jsonDatabaseString, LandmarkList.class);
            if (landmarkList!=null) {
                databaseInteractor.addAllLandmarks(landmarkList.getLandmarks());
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    navigationView.setOnNavigationItemSelectedListener(navListener);
                }
            }, 5000);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            navigationView.setOnNavigationItemSelectedListener(navListener);
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


}
