package com.britishheritage.android.britishheritage.Main.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;

import java.util.List;

import androidx.annotation.NonNull;

public class VerticalLandmarksAdapter extends LandmarksAdapter {

    public VerticalLandmarksAdapter(List<Landmark> favouritesList, Context context, Listener listener) {
        super(favouritesList, context, listener);
        isVerticalLandmarkAdapter = true;
    }

    @NonNull
    @Override
    public LandmarksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        try {
            view = LayoutInflater.from(context).inflate(R.layout.landmark_elongated, parent, false);
        }
        catch (OutOfMemoryError outOfMemoryError){
            view = LayoutInflater.from(context).inflate(R.layout.landmark_elongated_no_drawables, parent, false);
        }
        return new LandmarksViewHolder(view, isVerticalLandmarkAdapter);
    }
}
