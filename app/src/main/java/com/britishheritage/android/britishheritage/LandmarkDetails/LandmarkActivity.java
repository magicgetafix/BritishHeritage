package com.britishheritage.android.britishheritage.LandmarkDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LandmarkActivity extends AppCompatActivity {

    private Landmark mainLandmark;
    private Fragment landmarkMapFragment;
    private FloatingActionButton checkInButton;
    private TextView checkInTV;
    private TextView landmarkTitleTV;
    private RecyclerView geoNamesRecyclerView;
    private TextView userSuggestionsTitleTV;
    private RecyclerView userDescriptionsRecyclerView;


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
    }





}
