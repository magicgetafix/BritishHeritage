package com.britishheritage.android.britishheritage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.Keys.GooglePlayKeys;
import com.britishheritage.android.britishheritage.Main.MainActivity;
import com.britishheritage.android.britishheritage.Model.LandmarkList;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplashActivity extends BaseActivity {

  private DatabaseInteractor databaseInteractor;
  private LiveData<Integer> databaseSizeLiveData;
  private EditText usernameTV;
  private EditText emailTV;
  private EditText passwordTV;
  private Button googleSignInBtn;
  private Button loginButton;
  private ConstraintLayout splashViewGroup;

  private TextView usernameErrorTV;
  private TextView emailErrorTV;
  private TextView passwordErrorTV;
  private ProgressBar progressBar;

  private GoogleSignInClient googleSignInClient;
  private static final int SIGN_IN_CODE = 583;

  private TextWatcher textWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      checkValidity(false);
    }
  };

  private TextWatcher userNameTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      checkUsernameValidity();
    }
  };

  private FirebaseAuth firebaseAuth;

  private String username = "";
  private String password = "";
  private String emailAddress = "";
  private List<View> taggedViews;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    setContentView(R.layout.activity_splash);
    usernameTV = findViewById(R.id.splash_username);
    emailTV = findViewById(R.id.splash_email);
    passwordTV = findViewById(R.id.splash_password);
    googleSignInBtn = findViewById(R.id.splash_sign_in_button);
    loginButton = findViewById(R.id.login_button);
    usernameErrorTV = findViewById(R.id.splash_username_error);
    emailErrorTV = findViewById(R.id.splash_email_error);
    passwordErrorTV = findViewById(R.id.splash_password_error);
    progressBar = findViewById(R.id.splash_progressbar);
    splashViewGroup = findViewById(R.id.splash_viewgroup);

    taggedViews = Tools.getViewsByTag(splashViewGroup, "splash_component");
    for (View view: taggedViews){
      Tools.animateToOpaqueAlpha(view, 1000);
    }

    if (currentUser != null){

      for (View view: taggedViews){
        if (view.getId() != R.id.splash_transparency) {
          //set all views to invisible except transparency view which
          //will fade in over splash
          view.setVisibility(View.INVISIBLE);
        }
      }
      //start main activity after a delay
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
          startActivity(mainIntent);
        }
      }, 1000);

    }

    databaseInteractor = DatabaseInteractor.getInstance(getApplicationContext());
    databaseSizeLiveData = databaseInteractor.getDatabaseSize();
    databaseSizeLiveData.observe(this, this::populateDatabase);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GooglePlayKeys.CLIENT_ID)
            .requestEmail()
            .build();
    googleSignInClient = GoogleSignIn.getClient(this, gso);
    setUpButtons();
  }

  public void setUpButtons(){

    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signUp();
        passwordTV.addTextChangedListener(textWatcher);
        emailTV.addTextChangedListener(textWatcher);
      }
    });

    googleSignInBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (checkUsernameValidity()){
          googleSignIn();
        }
      }
    });

    usernameTV.addTextChangedListener(userNameTextWatcher);
  }

  private void signUp(){
    progressBar.setVisibility(View.VISIBLE);

    if (checkValidity(true)){
      firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()){
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser!=null) {
              saveUsername(currentUser);
            }

          }
          else{
            showSnackbar(getString(R.string.login_failure));
            progressBar.setVisibility(View.INVISIBLE);
          }
        }
      });
    }

    progressBar.setVisibility(View.INVISIBLE);

  }

  private void saveUsername(FirebaseUser currentUser){
    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
    currentUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()){
          Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
          startActivity(mainIntent);
          progressBar.setVisibility(View.INVISIBLE);
        }
        else{
          showSnackbar(getString(R.string.login_failure));
          progressBar.setVisibility(View.INVISIBLE);
        }

      }
    });
  }

  private void googleSignIn(){
    Intent signInIntent = googleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, SIGN_IN_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == SIGN_IN_CODE) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = task.getResult(ApiException.class);
        firebaseAuthWithGoogle(account);
      } catch (ApiException e) {
        Timber.e(e);
      }
    }
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

  AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
  firebaseAuth.signInWithCredential(credential)
          .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                FirebaseUser user = firebaseAuth.getCurrentUser();
                saveUsername(user);
              } else {
                showSnackbar(getString(R.string.login_failure));
                progressBar.setVisibility(View.INVISIBLE);
              }
            }
          });
  }


  private boolean checkUsernameValidity(){

    username = usernameTV.getText().toString().trim();
    boolean validUsername = username.length() >= 8 && username.length() <= 25;

    if (!validUsername){
      usernameErrorTV.setVisibility(View.VISIBLE);
      return false;
    }
    else {
      usernameErrorTV.setVisibility(View.INVISIBLE);
      return true;
    }
  }

  private boolean checkValidity(boolean checkAll){

    username = usernameTV.getText().toString().trim();
    password = passwordTV.getText().toString().trim();
    emailAddress = emailTV.getText().toString().trim();

    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(emailAddress);

    boolean validEmail = matcher.matches();
    if (!validEmail){
        emailErrorTV.setVisibility(View.VISIBLE);
        if (!checkAll){
          return false;
        }
    }
    else{
        emailErrorTV.setVisibility(View.INVISIBLE);
    }

    boolean validUsername = username.length() >= 8 && username.length() <= 25;

    if (!validUsername){
      usernameErrorTV.setVisibility(View.VISIBLE);
      if (!checkAll){
        return false;
      }
    }
    else{
      usernameErrorTV.setVisibility(View.INVISIBLE);
    }

    boolean validPassword = password.length() >= 8;

    if (!validPassword) {
      passwordErrorTV.setVisibility(View.VISIBLE);
      if (!checkAll){
        return false;
      }
    }
    else{
      passwordErrorTV.setVisibility(View.INVISIBLE);
    }

    return validEmail && validPassword && validUsername;

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
