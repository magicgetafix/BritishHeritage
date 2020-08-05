package com.britishheritage.android.britishheritage.Base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.britishheritage.android.britishheritage.LandmarkDetails.LandmarkActivity;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity {

    public static final String LANDMARK_PARCEL = "landmark_parcel";
    public static final int LANDMARK_ENTERED = 12;

    public void navigateWithLandmark(Class<? extends AppCompatActivity> destinationActivity , Landmark landmark){

        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra(LANDMARK_PARCEL, landmark);
        startActivity(intent);

    }

    public void navigateWithLandmarkForResult(Class<? extends AppCompatActivity> destinationActivity , Landmark landmark){

        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra(LANDMARK_PARCEL, landmark);
        startActivityForResult(intent, LANDMARK_ENTERED);


    }

    public void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showDialog(String title, String message, DialogInterface.OnClickListener positiveClickListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        String yes = getString(R.string.ok);
        builder.setPositiveButton(yes, positiveClickListener);
        AlertDialog alert = builder.create();
        alert.show();

    }


}
