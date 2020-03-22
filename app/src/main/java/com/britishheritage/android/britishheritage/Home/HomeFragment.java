package com.britishheritage.android.britishheritage.Home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Main.FavouritesAdapter;
import com.britishheritage.android.britishheritage.Main.MainActivity;
import com.britishheritage.android.britishheritage.Main.MainViewModel;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {


  private MainViewModel mainViewModel;
  private RecyclerView favouriteRecyclerView;
  private FavouritesAdapter favouritesAdapter;
  private LinearLayoutManager layoutManager;
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
    userPhotoIV = view.findViewById(R.id.home_fragment_photo);
    mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel .class);
    mainViewModel.getFavouriteListLiveData().observe(getViewLifecycleOwner(), this::onFavouritesUpdated);
    layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
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

    favouritesAdapter = new FavouritesAdapter(landmarks, getContext());
    favouriteRecyclerView.setLayoutManager(layoutManager);
    favouriteRecyclerView.setAdapter(favouritesAdapter);

  }

  public void onChooseImageClick() {

    CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setActivityTitle("Choose an image for your profile")
            .setCropMenuCropButtonTitle("Done")
            .setRequestedSize(userPhotoIV.getWidth(), userPhotoIV.getHeight())
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
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

    databaseInteractor.saveProfilePhotoToStorage(uri, userId, task -> {
      if (task.isSuccessful()){
        ((BaseActivity)getActivity()).showSnackbar(getString(R.string.uploaded_photo));
      }
      else{
        ((BaseActivity)getActivity()).showSnackbar(getString(R.string.failed_to_upload_photo));

      }
    });

  }



}
