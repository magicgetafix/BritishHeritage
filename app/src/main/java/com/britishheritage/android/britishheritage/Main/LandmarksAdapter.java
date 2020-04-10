package com.britishheritage.android.britishheritage.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;


public class LandmarksAdapter extends RecyclerView.Adapter<LandmarksAdapter.FavouritesViewHolder> {

    private List<Landmark> favouritesList = new ArrayList<>();
    private Context context;

    public LandmarksAdapter(List<Landmark> favouritesList, Context context){
        this.favouritesList = favouritesList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourite_landmark, parent, false);
        return new FavouritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder holder, int position) {
        Landmark landmark = favouritesList.get(position);
        holder.setContent(landmark);
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public static class FavouritesViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        View hillfortBackground;
        View listedBuildingBackground;
        View scheduledMonumentBackground;
        View parkAndGardenBackground;
        View battlefieldBackground;

        public FavouritesViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.favourites_text_view);
            hillfortBackground = itemView.findViewById(R.id.hillfort_background);
            listedBuildingBackground = itemView.findViewById(R.id.listed_building_background);
            scheduledMonumentBackground = itemView.findViewById(R.id.monument_background);
            parkAndGardenBackground = itemView.findViewById(R.id.park_garden_background);
            battlefieldBackground = itemView.findViewById(R.id.battlefield_background);

        }

        public void setContent(Landmark landmark){

            hillfortBackground.setVisibility(View.INVISIBLE);
            listedBuildingBackground.setVisibility(View.INVISIBLE);
            scheduledMonumentBackground.setVisibility(View.INVISIBLE);
            parkAndGardenBackground.setVisibility(View.INVISIBLE);
            battlefieldBackground.setVisibility(View.INVISIBLE);

            if (landmark.getName()!=null) {
                textView.setText(landmark.getName());
            }
            if (landmark.getType()!=null){

                String type = landmark.getType();
                if (type.equalsIgnoreCase("Buildings") || type.equals(Constants.LISTED_BUILDINGS_ID)){
                    type = "Listed Building";
                    listedBuildingBackground.setVisibility(View.VISIBLE);
                }

                if (landmark.getType().equalsIgnoreCase("Scheduled Monument") || type.equals(Constants.SCHEDULED_MONUMENTS_ID)){
                    scheduledMonumentBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Listed Building") || type.equals(Constants.LISTED_BUILDINGS_ID)){
                    listedBuildingBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Hillfort") || type.equals(Constants.HILLFORTS_ID)){
                    hillfortBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Park or Garden") || type.equals(Constants.PARKS_AND_GARDENS_ID)){
                    parkAndGardenBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Battlefield") || type.equals(Constants.BATTLEFIELDS_ID)){
                    battlefieldBackground.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}


