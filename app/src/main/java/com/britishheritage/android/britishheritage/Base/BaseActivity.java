package com.britishheritage.android.britishheritage.Base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.britishheritage.android.britishheritage.Model.Landmark;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity {

    public static final String LANDMARK_PARCEL = "landmark_parcel";

    public void navigateWithLandmark(Class<? extends AppCompatActivity> destinationActivity , Landmark landmark){

        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra(LANDMARK_PARCEL, landmark);
        startActivity(intent);

    }

    public void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


}
