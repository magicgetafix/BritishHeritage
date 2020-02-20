package com.britishheritage.android.britishheritage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Main.MainActivity;
import com.britishheritage.android.britishheritage.Model.LandmarkList;
import com.britishheritage.android.britishheritage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

  private DatabaseInteractor databaseInteractor;
  private LiveData<Integer> databaseSizeLiveData;
  private EditText usernameTV;
  private EditText emailTV;
  private EditText passwordTV;
  private Button googleSignInBtn;
  private Button loginButton;

  private TextView usernameErrorTV;
  private TextView emailErrorTV;
  private TextView passwordErrorTV;

  private FirebaseAuth firebaseAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    usernameTV = findViewById(R.id.splash_username);
    emailTV = findViewById(R.id.splash_email);
    passwordTV = findViewById(R.id.splash_password);
    googleSignInBtn = findViewById(R.id.splash_sign_in_button);
    loginButton = findViewById(R.id.login_button);
    usernameErrorTV = findViewById(R.id.splash_username_error);
    emailErrorTV = findViewById(R.id.splash_email_error);
    passwordErrorTV = findViewById(R.id.splash_password_error);

    firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    if (currentUser != null){
      Intent mainIntent = new Intent(this, MainActivity.class);
      startActivity(mainIntent);
    }
    databaseInteractor = DatabaseInteractor.getInstance(getApplicationContext());
    databaseSizeLiveData = databaseInteractor.getDatabaseSize();
    databaseSizeLiveData.observe(this, this::populateDatabase);

  }

  private void populateDatabase(int databaseSize){
    Timber.d("Database size is: "+databaseSize);
    if (databaseSize == 0){

      Observable.just(0).doOnNext(o->{
        String jsonDatabaseString = loadJSONFromAsset();
        LandmarkList landmarkList = null;
        if (jsonDatabaseString != null) {
          Gson gson = new Gson();
          landmarkList = gson.fromJson(jsonDatabaseString, LandmarkList.class);
        }
        if (landmarkList!=null) {
          databaseInteractor.addAllLandmarks(landmarkList.getLandmarks());
        }
      }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

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
