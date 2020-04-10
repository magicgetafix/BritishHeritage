package com.britishheritage.android.britishheritage.Home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Main.LandmarksAdapter;
import com.britishheritage.android.britishheritage.Main.MainViewModel;
import com.britishheritage.android.britishheritage.Model.Landmark;
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


public class HomeFragment extends Fragment {


  private MainViewModel mainViewModel;
  private RecyclerView favouriteRecyclerView;
  private LandmarksAdapter favouritesAdapter;
  private LinearLayoutManager favouritesLayoutManager;
  private RecyclerView checkedInLandmarksRecyclerView;
  private LandmarksAdapter checkedInLandmarksAdapter;
  private LinearLayoutManager checkedInLandmarksManager;
  private ImageView userPhotoIV;
  private FirebaseUser currentUser;
  private DatabaseInteractor databaseInteractor;

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
    mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel .class);
    favouritesLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
    checkedInLandmarksManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    //set up with default values
    onCheckedInLandmarksUpdated(new ArrayList<Landmark>());
    //set up with default values
    onFavouritesUpdated(new ArrayList<Landmark>());
    mainViewModel.getFavouriteListLiveData().observe(getViewLifecycleOwner(), this::onFavouritesUpdated);
    mainViewModel.getCheckedInLandmarkLiveData().observe(getViewLifecycleOwner(), this::onCheckedInLandmarksUpdated);
    userPhotoIV.setOnClickListener(v->{
      onChooseImageClick();
    });

    if (currentUser.getPhotoUrl() != null){
      Glide.with(this)
              .load(currentUser.getPhotoUrl())
              .into(userPhotoIV);
    }
  }

  private void onFavouritesUpdated(List<Landmark> landmarks) {

    if (landmarks.isEmpty()){
      Landmark tempLandmark = new Landmark();
      String tempFavText = getString(R.string.temp_favourite_text);
      tempLandmark.setName(tempFavText);
      tempLandmark.setId("0");
      tempLandmark.setType(Constants.LISTED_BUILDINGS_ID);
      landmarks.add(tempLandmark);
    }
    favouritesAdapter = new LandmarksAdapter(landmarks, getContext());
    favouriteRecyclerView.setLayoutManager(favouritesLayoutManager);
    favouriteRecyclerView.setAdapter(favouritesAdapter);

  }

  private void onCheckedInLandmarksUpdated(List<Landmark> landmarks){

    if (landmarks.isEmpty()){
      Landmark tempLandmark = new Landmark();
      String tempFavText = getString(R.string.temp_checked_in_landmarks);
      tempLandmark.setName(tempFavText);
      tempLandmark.setId("0");
      tempLandmark.setType(Constants.SCHEDULED_MONUMENTS_ID);
      landmarks.add(tempLandmark);
    }
    checkedInLandmarksAdapter = new LandmarksAdapter(landmarks, getContext());
    checkedInLandmarksRecyclerView.setLayoutManager(checkedInLandmarksManager);
    checkedInLandmarksRecyclerView.setAdapter(checkedInLandmarksAdapter);

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
            .setOutputCompressQuality(30)
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


}
