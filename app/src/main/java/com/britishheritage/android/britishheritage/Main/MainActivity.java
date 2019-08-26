package com.britishheritage.android.britishheritage.Main;

import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.britishheritage.android.britishheritage.Home.HomeFragment;
import com.britishheritage.android.britishheritage.Maps.ArchaeologyMapFragment;
import com.britishheritage.android.britishheritage.Maps.ListedBuildingsMapFragment;
import com.britishheritage.android.britishheritage.Maps.MapViewModel;
import com.britishheritage.android.britishheritage.R;
import com.firebase.geofire.GeoFire;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;
import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView navigationView;
    private Fragment selectedFragment;
    private Fragment archMapFragment;
    private Fragment listedBuildingFragment;
    private Fragment homeFragment;
    private boolean databaseIsCreated;
    private ProgressBar progressBar;
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
        // construct a new instance of SimpleLocation
        SimpleLocation location = new SimpleLocation(this);

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }


        frameLayout = findViewById(R.id.main_frame_layout);
        navigationView = findViewById(R.id.main_navigation);
        progressBar = findViewById(R.id.main_progress_bar);
        //set up view model
        //mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        //database = FirebaseDatabase.getInstance();
        //if (database == null){
        //    System.out.print("Database is null");
        //}

        //DatabaseReference landmarkRef = database.getReference("landmarks");
        //if (landmarkRef == null){

          //  System.out.println("landmark ref is null");
        //}
        //System.out.println(landmarkRef.toString());

        //DatabaseReference geoFireRef = database.getReference("geo_location");
        //GeoFire geoFire = new GeoFire(geoFireRef);
        navigationView.setOnNavigationItemSelectedListener(navListener);

        /*landmarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                while (iterator.hasNext()){
                    DataSnapshot next = iterator.next();
                    DataSnapshot idDataShot = next.child("id");
                    String id = idDataShot.getValue(String.class);
                    DataSnapshot latitudeDatashot = next.child("latitude");
                    DataSnapshot longitudeDatashot = next.child("longitude");

                    Double latitude = latitudeDatashot.getValue(Double.class);
                    Double longitude = longitudeDatashot.getValue(Double.class);

                    if (latitude!=null && longitude!=null && id!=null){
                        System.out.print(id+" lat: "+latitude+" long: "+longitude);
                        GeoLocation location = new GeoLocation(latitude, longitude);
                        geoFire.setLocation(id, location, new GeoFire.CompletionListener() {

                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                System.out.println("Geofire complete for "+ key);
                            }
                        } );
                    }

                }

                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */




    }
}
