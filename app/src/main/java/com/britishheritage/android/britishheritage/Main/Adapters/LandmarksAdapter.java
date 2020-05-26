package com.britishheritage.android.britishheritage.Main.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class LandmarksAdapter extends RecyclerView.Adapter<LandmarksAdapter.LandmarksViewHolder> {

    protected List<Landmark> favouritesList = new ArrayList<>();
    protected Context context;
    protected Listener listener;
    protected boolean isVerticalLandmarkAdapter = false;

    public LandmarksAdapter(List<Landmark> favouritesList, Context context, Listener listener){
        this.favouritesList = favouritesList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public LandmarksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        try {
            view = LayoutInflater.from(context).inflate(R.layout.favourite_landmark, parent, false);
        }
        catch (OutOfMemoryError outOfMemoryError){
            view = LayoutInflater.from(context).inflate(R.layout.favourite_landmark_no_drawables, parent, false);
        }
        return new LandmarksViewHolder(view, isVerticalLandmarkAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull LandmarksViewHolder holder, int position) {
        Landmark landmark = favouritesList.get(position);
        holder.setContent(landmark, this.listener);
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public static class LandmarksViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private View hillfortBackground;
        private View listedBuildingBackground;
        private View scheduledMonumentBackground;
        private View parkAndGardenBackground;
        private View battlefieldBackground;
        private View itemView;
        private Listener listener;
        private View overlay;
        private boolean isVerticalView = false;

        public LandmarksViewHolder(@NonNull View itemView, boolean isVerticalView) {
            super(itemView);
            this.itemView = itemView;
            this.isVerticalView = isVerticalView;
            textView = itemView.findViewById(R.id.favourites_text_view);
            hillfortBackground = itemView.findViewById(R.id.hillfort_background);
            listedBuildingBackground = itemView.findViewById(R.id.listed_building_background);
            scheduledMonumentBackground = itemView.findViewById(R.id.monument_background);
            parkAndGardenBackground = itemView.findViewById(R.id.park_garden_background);
            battlefieldBackground = itemView.findViewById(R.id.battlefield_background);
            overlay = itemView.findViewById(R.id.overlay);

        }

        public void setContent(Landmark landmark, Listener listener){

            this.listener = listener;
            hillfortBackground.setVisibility(View.INVISIBLE);
            listedBuildingBackground.setVisibility(View.INVISIBLE);
            scheduledMonumentBackground.setVisibility(View.INVISIBLE);
            parkAndGardenBackground.setVisibility(View.INVISIBLE);
            battlefieldBackground.setVisibility(View.INVISIBLE);
            overlay.setVisibility(View.VISIBLE);
            overlay.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.trans_overlay));

            if (landmark == null){
                return;
            }

            //check not a temporary landmark
            if (landmark.getLatitude() != null && landmark.getLongitude() != null) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            if (!isVerticalView) {
                                listener.onLandmarkClicked(landmark, false);
                            }
                            else{
                                listener.onLandmarkClicked(landmark, true);
                            }
                        }
                    }
                });
            }

            if (landmark.getType()!=null){

                String type = landmark.getType();
                if (type.equalsIgnoreCase("Buildings") || type.equals(Constants.LISTED_BUILDINGS_ID)){
                    type = "Listed Building";
                    listedBuildingBackground.setVisibility(View.VISIBLE);
                    if (landmark.getName()!=null) {
                        String name = landmark.getName();
                        if (name.contains("\u0027")) {
                            landmark.setName(Tools.convertToTitleCase(name));
                        }
                        textView.setText(Tools.formatTitle(landmark.getName()));
                    }
                }

                if (landmark.getType().equalsIgnoreCase("Scheduled Monument") || type.equals(Constants.SCHEDULED_MONUMENTS_ID)){
                    scheduledMonumentBackground.setVisibility(View.VISIBLE);
                    if (landmark.getName()!=null) {
                        String name = landmark.getName();
                        if (name.contains("\u0027")) {
                            landmark.setName(Tools.convertToTitleCase(name));
                        }
                        textView.setText(Tools.formatTitle(landmark.getName()));
                    }
                }
                else if (landmark.getType().equalsIgnoreCase("Listed Building") || type.equals(Constants.LISTED_BUILDINGS_ID)){
                    listedBuildingBackground.setVisibility(View.VISIBLE);
                    if (landmark.getName()!=null) {
                        String name = landmark.getName();
                        if (name.contains("\u0027")) {
                            landmark.setName(Tools.convertToTitleCase(name));
                        }
                        textView.setText(Tools.formatTitle(landmark.getName()));
                    }
                }
                else if (landmark.getType().equalsIgnoreCase("Hillfort") || type.equals(Constants.HILLFORTS_ID)){
                    hillfortBackground.setVisibility(View.VISIBLE);
                    if (landmark.getName()!=null) {
                        String name = landmark.getName();
                        if (name.contains("\u0027")) {
                            landmark.setName(Tools.convertToTitleCase(name));
                        }
                        textView.setText(Tools.formatTitle(landmark.getName()));
                    }
                }
                else if (landmark.getType().equalsIgnoreCase("Park or Garden") || type.equals(Constants.PARKS_AND_GARDENS_ID)){
                    parkAndGardenBackground.setVisibility(View.VISIBLE);
                    if (landmark.getName()!=null) {
                        String name = landmark.getName();
                        if (name.contains("\u0027")) {
                            landmark.setName(Tools.convertToTitleCase(name));
                        }
                        textView.setText(Tools.formatTitle(landmark.getName()));
                    }
                }
                else if (landmark.getType().equalsIgnoreCase("Battlefield") || type.equals(Constants.BATTLEFIELDS_ID)){
                    battlefieldBackground.setVisibility(View.VISIBLE);
                    if (landmark.getName()!=null) {
                        String name = landmark.getName();
                        if (name.contains("\u0027")) {
                            landmark.setName(Tools.convertToTitleCase(name));
                        }
                        textView.setText(Tools.formatTitle(landmark.getName()));
                    }
                }
                else if (landmark.getType().equalsIgnoreCase("Blue Plaque") || type.equals(Constants.BLUE_PLAQUES)){
                    String title = landmark.getName().split("!!")[0];
                    title = Tools.convertToTitleCase(title);
                    textView.setText(title);
                    scheduledMonumentBackground.setVisibility(View.INVISIBLE);
                    battlefieldBackground.setVisibility(View.INVISIBLE);
                    parkAndGardenBackground.setVisibility(View.INVISIBLE);
                    hillfortBackground.setVisibility(View.INVISIBLE);
                    listedBuildingBackground.setVisibility(View.INVISIBLE);
                    overlay.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.diagonal_primary_fade));

                }
            }
        }
    }

    public interface Listener{
        void onLandmarkClicked(Landmark landmark, boolean shouldNavOnMap);
    }
}


