package com.britishheritage.android.britishheritage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.Keys.GooglePlayKeys;
import com.britishheritage.android.britishheritage.Main.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import timber.log.Timber;

public class LoginActivity extends BaseActivity {

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

        setContentView(R.layout.activity_login);
        usernameTV = findViewById(R.id.splash_username);
        emailTV = findViewById(R.id.splash_email);
        passwordTV = findViewById(R.id.splash_password);
        googleSignInBtn = findViewById(R.id.splash_sign_in_button);
        loginButton = findViewById(R.id.login_button);
        usernameErrorTV = findViewById(R.id.splash_username_error);
        emailErrorTV = findViewById(R.id.splash_email_error);
        passwordErrorTV = findViewById(R.id.splash_password_error);
        progressBar = findViewById(R.id.splash_progressbar);

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
                signIn();
                passwordTV.addTextChangedListener(textWatcher);
                emailTV.addTextChangedListener(textWatcher);
            }
        });

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    googleSignIn();
            }
        });
    }

    private void signIn(){
        progressBar.setVisibility(View.VISIBLE);

        if (checkValidity(true)){
            firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser!=null) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        else{
                            showSnackbar(getString(R.string.login_failure));
                            progressBar.setVisibility(View.INVISIBLE);
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
                            // Sign in success, start new activity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        } else {
                            showSnackbar(getString(R.string.login_failure));
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }


    private boolean checkValidity(boolean checkAll){

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

        return validEmail && validPassword;

    }

}
