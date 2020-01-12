package com.britishheritage.android.britishheritage.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;


public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder> {

    private List<Landmark> favouritesList = new ArrayList<>();
    private Context context;

    public FavouritesAdapter(List<Landmark> favouritesList, Context context){
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
                if (type.equalsIgnoreCase("Buildings")){
                    type = "Listed Building";
                }

                Timber.d(landmark.getType());

                if (landmark.getType().equalsIgnoreCase("Scheduled Monument")){
                    scheduledMonumentBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Listed Building")){
                    listedBuildingBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Hillfort")){
                    hillfortBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Park or Garden")){
                    parkAndGardenBackground.setVisibility(View.VISIBLE);
                }
                else if (landmark.getType().equalsIgnoreCase("Battlefield")){
                    battlefieldBackground.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}


