package com.britishheritage.android.britishheritage.Base;

import android.content.Intent;

import com.britishheritage.android.britishheritage.Model.Landmark;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String LANDMARK_PARCEL = "landmark_parcel";


    public void navigateWithLandmark(Class<? extends AppCompatActivity> destinationActivity , Landmark landmark){

        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra(LANDMARK_PARCEL, landmark);
        startActivity(intent);

    }

}
