package com.britishheritage.android.britishheritage.LandmarkDetails.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Model.WikiLandmark;
import com.britishheritage.android.britishheritage.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WikiLandmarkAdapter extends RecyclerView.Adapter<WikiLandmarkAdapter.WikiLandmarkViewHolder> {

    private List<WikiLandmark> wikiLandmarkList = new ArrayList<>();
    private Context context;
    private OnWikiLandmarkClickListener wikiLandmarkClickListener;

    public WikiLandmarkAdapter(List<WikiLandmark> wikiLandmarkList, Context context, OnWikiLandmarkClickListener wikiLandmarkClickListener){
        this.wikiLandmarkList = wikiLandmarkList;
        this.context = context;
        this.wikiLandmarkClickListener = wikiLandmarkClickListener;
    }

    @NonNull
    @Override
    public WikiLandmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wiki_landmark_view, parent, false);
        return new WikiLandmarkViewHolder(view, this.wikiLandmarkClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WikiLandmarkViewHolder holder, int position) {
        WikiLandmark wikiLandmark = wikiLandmarkList.get(position);
        holder.setContent(wikiLandmark);
    }

    @Override
    public int getItemCount() {
        return wikiLandmarkList.size();
    }

    public static class WikiLandmarkViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView summaryTextView;
        View itemView;
        OnWikiLandmarkClickListener wikiLandmarkClickListener;

        public WikiLandmarkViewHolder(@NonNull View itemView, OnWikiLandmarkClickListener wikiLandmarkClickListener) {
            super(itemView);
            this.itemView = itemView;
            titleTextView = itemView.findViewById(R.id.wiki_landmark_title);
            summaryTextView = itemView.findViewById(R.id.wiki_land_summary);
            this.wikiLandmarkClickListener = wikiLandmarkClickListener;

        }

        public void setContent(WikiLandmark wikilandmark){

            if (wikilandmark!=null){
                titleTextView.setText(wikilandmark.getTitle());
                summaryTextView.setText(wikilandmark.getSummary());
            }
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (wikiLandmarkClickListener!=null){
                        wikiLandmarkClickListener.onItemClick(wikilandmark.getUrl());
                    }
                }
            });
        }
    }

    public interface OnWikiLandmarkClickListener{

        void onItemClick(String url);
    }

}

