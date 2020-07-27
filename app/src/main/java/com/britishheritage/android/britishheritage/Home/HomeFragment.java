package com.britishheritage.android.britishheritage.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.LandmarkDetails.LandmarkActivity;
import com.britishheritage.android.britishheritage.Main.Adapters.UsersAdapter;
import com.britishheritage.android.britishheritage.Main.Adapters.LandmarksAdapter;
import com.britishheritage.android.britishheritage.Main.MainActivity;
import com.britishheritage.android.britishheritage.Main.MainViewModel;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.User;
import com.britishheritage.android.britishheritage.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements LandmarksAdapter.Listener {


  private MainViewModel mainViewModel;
  private RecyclerView favouriteRecyclerView;
  private LandmarksAdapter favouritesAdapter;
  private LinearLayoutManager favouritesLayoutManager;
  private RecyclerView checkedInLandmarksRecyclerView;
  private LandmarksAdapter checkedInLandmarksAdapter;
  private LinearLayoutManager checkedInLandmarksManager;
  private LinearLayoutManager topScoresLayoutManager;
  private UsersAdapter topScoresAdapter;
  private ImageView userPhotoIV;
  private FirebaseUser currentUser;
  private DatabaseInteractor databaseInteractor;
  private TextView highestScorersTitleTv;
  private RecyclerView highestScorersRecyclerView;
  private ImageView deleteFavouritesIV;
  private ImageView deleteCheckedInPropertiesIV;
  private ImageView backgroundPhotoIV;
  private SearchLandmarkView searchLandmarkView;
  private ImageView openButton;
  private View shimView;

  //user profile
  private TextView userNameTV;
  private TextView userScoreTV;
  private TextView userRankTV;

  private TextView placesVisitedTV;
  private TextView favouritesTV;

  public HomeFragment() {
    // Required empty public constructor
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    databaseInteractor = new DatabaseInteractor(getContext());

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    currentUser = FirebaseAuth.getInstance().getCurrentUser();
    favouriteRecyclerView = view.findViewById(R.id.favourites_recycler_view);
    checkedInLandmarksRecyclerView = view.findViewById(R.id.home_checked_in_landmarks_recycler_view);
    userPhotoIV = view.findViewById(R.id.home_fragment_photo);
    backgroundPhotoIV = view.findViewById(R.id.photo_background);
    highestScorersTitleTv = view.findViewById(R.id.home_top_scores_text);
    highestScorersRecyclerView = view.findViewById(R.id.top_names_recycler_view);
    userNameTV = view.findViewById(R.id.home_username_textview);
    userScoreTV = view.findViewById(R.id.home_user_points_textview);
    userRankTV = view.findViewById(R.id.home_user_rank_textview);
    deleteFavouritesIV = view.findViewById(R.id.delete_favourites);
    favouritesTV = view.findViewById(R.id.home_your_favourites_text);
    placesVisitedTV = view.findViewById(R.id.home_places_you_have_visited_text);
    deleteCheckedInPropertiesIV = view.findViewById(R.id.delete_checked_in_places);
    mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel .class);
    favouritesLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
    checkedInLandmarksManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    topScoresLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    searchLandmarkView = view.findViewById(R.id.home_search_pane);
    openButton = view.findViewById(R.id.search_landmarks_button);
    shimView = view.findViewById(R.id.shim_view);
    openButton.setOnClickListener(v->{
      if (searchLandmarkView!=null){
        searchLandmarkView.openPane();
      }
    });
    if (searchLandmarkView!=null){
        searchLandmarkView.setShim(shimView);
        searchLandmarkView.setOnLandmarkClickListener(this);
    }

    shimView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (searchLandmarkView!=null){
                searchLandmarkView.closePane();
            }
        }
    });




    //set up with default values
    onCheckedInLandmarksUpdated(new ArrayList<Landmark>());
    //set up with default values
    onFavouritesUpdated(new ArrayList<Landmark>());
    mainViewModel.getFavouriteListLiveData().observe(getViewLifecycleOwner(), this::onFavouritesUpdated);
    mainViewModel.getCheckedInLandmarkLiveData().observe(getViewLifecycleOwner(), this::onCheckedInLandmarksUpdated);
    mainViewModel.getTopScoringUserLiveData().observe(getViewLifecycleOwner(), this::onTopScoringUsersUpdated);
    userPhotoIV.setOnClickListener(v->{
      onChooseImageClick();
    });

    if (currentUser.getPhotoUrl() != null){
      Glide.with(this)
              .load(currentUser.getPhotoUrl())
              .into(userPhotoIV);
    }
    else{
      Drawable addPhotoDrawable = getResources().getDrawable(R.drawable.add_photo_white_icon);
      userPhotoIV.setImageDrawable(addPhotoDrawable);
    }
    setUpUserInfo();

    deleteFavouritesIV.setOnClickListener(v->{
      String confirm = getString(R.string.confirm);
      String deleteFavsStr = getString(R.string.delete_favs);
      DialogInterface.OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          if (currentUser!=null) {
            mainViewModel.deleteFavouritesCompletely(currentUser);
            dialog.dismiss();
          }
        }
      };
      DialogInterface.OnClickListener negativeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      };
      showDialog(confirm, deleteFavsStr, positiveClickListener, negativeClickListener);
    });

    deleteCheckedInPropertiesIV.setOnClickListener(v->{
      String confirm = getString(R.string.confirm);
      String deletePlacesStr = getString(R.string.delete_checked_in);
      DialogInterface.OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          mainViewModel.deleteCheckedInProperties();
          dialog.dismiss();
          setUpUserInfo();
        }
      };
      DialogInterface.OnClickListener negativeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      };
      showDialog(confirm, deletePlacesStr, positiveClickListener, negativeClickListener);
    });

  }

  private void setUpUserInfo(){

      if (currentUser!=null) {
        userNameTV.setText(currentUser.getDisplayName());
        int score = databaseInteractor.getCurrentPointsTotal(currentUser);
        score = Math.abs(score);
        userScoreTV.setText(getString(R.string.points, score));
        String ranking = Tools.getRanking(score);
        String rankingStr = getString(R.string.rank, ranking);
        userRankTV.setText(rankingStr);
      }

  }

  private void onFavouritesUpdated(List<Landmark> landmarks) {

    if (!landmarks.isEmpty()){
      String favsText = getString(R.string.your_favourite_places)+" ("+landmarks.size()+") :";
      favouritesTV.setText(favsText);
    }
    if (landmarks.isEmpty()){
      Landmark tempLandmark = new Landmark();
      String tempFavText = getString(R.string.temp_favourite_text);
      tempLandmark.setName(tempFavText);
      tempLandmark.setId("0");
      tempLandmark.setType(Constants.LISTED_BUILDINGS_ID);
      landmarks.add(tempLandmark);
    }
    favouritesAdapter = new LandmarksAdapter(landmarks, getContext(), this);
    favouriteRecyclerView.setLayoutManager(favouritesLayoutManager);
    favouriteRecyclerView.setAdapter(favouritesAdapter);

  }

  private void onCheckedInLandmarksUpdated(List<Landmark> landmarks){

    if (!landmarks.isEmpty()){
      String placesVistedText = getString(R.string.places_you_have_explored)+" ("+landmarks.size()+") :";
      placesVisitedTV.setText(placesVistedText);
    }
    if (landmarks.isEmpty()){
      Landmark tempLandmark = new Landmark();
      String tempFavText = getString(R.string.temp_checked_in_landmarks);
      tempLandmark.setName(tempFavText);
      tempLandmark.setId("0");
      tempLandmark.setType(Constants.SCHEDULED_MONUMENTS_ID);
      landmarks.add(tempLandmark);
      setUpUserInfo();
    }
    try {
      Tools.sortLandmarksAtoZ(landmarks);
    }
    catch (Exception e){
      Timber.e(e);
    }
    checkedInLandmarksAdapter = new LandmarksAdapter(landmarks, getContext(), this);
    checkedInLandmarksRecyclerView.setLayoutManager(checkedInLandmarksManager);
    checkedInLandmarksRecyclerView.setAdapter(checkedInLandmarksAdapter);

  }

  private void onTopScoringUsersUpdated(List<User> userList){

    if (userList!=null && !userList.isEmpty()){
      highestScorersTitleTv.setVisibility(View.VISIBLE);
      topScoresAdapter = new UsersAdapter(getContext(), userList, getViewLifecycleOwner());
      topScoresLayoutManager.setStackFromEnd(true);
      highestScorersRecyclerView.setLayoutManager(topScoresLayoutManager);
      highestScorersRecyclerView.setAdapter(topScoresAdapter);
      setUpUserInfo();
    }
    else{
      highestScorersTitleTv.setVisibility(View.GONE);
    }
  }

  public void onChooseImageClick() {

    CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setActivityTitle("Choose an image for your profile")
            .setCropMenuCropButtonTitle("Done")
            .setRequestedSize(userPhotoIV.getWidth(), userPhotoIV.getHeight())
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            .setOutputCompressQuality(50)
            .start(getContext(), this);

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // handle result of CropImageActivity
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);

      if (resultCode == RESULT_OK) {

        Uri imageUri = result.getUri();
        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(userPhotoIV);

        savePhotoToFirebase(imageUri);
      }

      else  {
        ((BaseActivity)getActivity()).showSnackbar(getString(R.string.failed_to_upload_photo));
      }
    }
  }

  private void savePhotoToFirebase(Uri uri){
    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

    currentUser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
      if (task.isSuccessful()){
        savePhotoToStorage(uri, currentUser.getUid());
      }
      else{
        ((BaseActivity)getActivity()).showSnackbar(getString(R.string.failed_to_upload_photo));

      }
    });
  }

  private void savePhotoToStorage(Uri uri, String userId){

    Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/6, bitmap.getHeight()/6, false);
    try {
      Uri condensedUri = convertBitmapToCondensedUri(scaledBitmap);
      databaseInteractor.saveProfilePhotoToStorage(condensedUri, userId, task -> {
        if (task.isSuccessful()) {
          ((BaseActivity) getActivity()).showSnackbar(getString(R.string.uploaded_photo));
        } else {
          ((BaseActivity) getActivity()).showSnackbar(getString(R.string.failed_to_upload_photo));

        }
      });
    }
    catch (IOException exception){
      Timber.e(exception);
    }
  }

  private Uri convertBitmapToCondensedUri(Bitmap bitmap) throws IOException{
    File tempDir= getActivity().getApplication().getCacheDir();
    tempDir = new File(tempDir.getAbsolutePath()+"/british_heritage_temp/");
    tempDir.mkdir();
    File tempFile = File.createTempFile("image", ".jpg", tempDir);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    byte[] bitmapData = bytes.toByteArray();

    FileOutputStream fos = new FileOutputStream(tempFile);
    fos.write(bitmapData);
    fos.flush();
    fos.close();
    return Uri.fromFile(tempFile);
  }


  @Override
  public void onLandmarkClicked(Landmark landmark, boolean requiresNav) {

    try {
      MainActivity activity = (MainActivity) getActivity();
      activity.navigateWithLandmark(LandmarkActivity.class, landmark, requiresNav);
    }
    catch (Exception e){
      Timber.d(e);
    }
  }


  public void showDialog(String title, String message, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener){

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(title);
    builder.setMessage(message);

    String yes = getString(R.string.yes);
    builder.setPositiveButton(yes, positiveClickListener);

    String no = getString(R.string.no);
    builder.setNegativeButton(no, negativeClickListener);

    AlertDialog alert = builder.create();
    alert.show();

  }


}
