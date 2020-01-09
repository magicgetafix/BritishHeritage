package com.britishheritage.android.britishheritage.Main.Dialogs;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    private TextView directionsTV;
    private TextView favouritesTV;
    private TextView viewDetailsTV;
    private ImageView directionsIV;
    private ImageView favouritesIV;
    private ImageView viewDetailsIV;

    private ItemClickListener clickListener;

    public static BottomDialogFragment newInstance() {
        return new BottomDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_fragment_options, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        directionsTV = view.findViewById(R.id.direction_text);
        directionsIV = view.findViewById(R.id.direction_image);
        favouritesTV = view.findViewById(R.id.favourite_text);
        favouritesIV = view.findViewById(R.id.favourite_image);
        viewDetailsTV = view.findViewById(R.id.view_detail_text);
        viewDetailsIV = view.findViewById(R.id.view_detail_image);

        directionsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (clickListener!=null){
                    clickListener.onViewDirectionsClick();
                }
            }
        });

        directionsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (clickListener!=null){
                    clickListener.onViewDirectionsClick();
                }
            }
        });

        favouritesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.onAddToFavouritesClick();
                }
            }
        });

        favouritesIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.onAddToFavouritesClick();
                }
            }
        });

        viewDetailsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.onViewDetailsClick();
                }
            }
        });

        viewDetailsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.onViewDetailsClick();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            clickListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickListener = null;
    }

    public interface ItemClickListener {
        void onViewDirectionsClick();
        void onAddToFavouritesClick();
        void onViewDetailsClick();

    }
}
