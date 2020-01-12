package com.britishheritage.android.britishheritage.Home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.britishheritage.android.britishheritage.Main.FavouritesAdapter;
import com.britishheritage.android.britishheritage.Main.MainViewModel;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;

import java.util.List;


public class HomeFragment extends Fragment {


  private MainViewModel mainViewModel;
  private RecyclerView favouriteRecyclerView;
  private FavouritesAdapter favouritesAdapter;
  private LinearLayoutManager layoutManager;

  public HomeFragment() {
    // Required empty public constructor
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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
    favouriteRecyclerView = view.findViewById(R.id.favourites_recycler_view);
    mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel .class);
    mainViewModel.getFavouriteListLiveData().observe(this, this::onFavouritesUpdated);
    layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
  }

  private void onFavouritesUpdated(List<Landmark> landmarks) {

    favouritesAdapter = new FavouritesAdapter(landmarks, getContext());
    favouriteRecyclerView.setLayoutManager(layoutManager);
    favouriteRecyclerView.setAdapter(favouritesAdapter);

  }



}
