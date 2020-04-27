package com.britishheritage.android.britishheritage.Maps.MapAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Tools;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapInfoWindow implements GoogleMap.InfoWindowAdapter {

  private Context context;

  public MapInfoWindow(Context context){
    this.context = context;

  }
  @Override
  public View getInfoWindow(Marker marker) {

    if (marker.getSnippet()!=null) {

      String[] dataCSV = marker.getSnippet().split("@@");
      String locationName = "";
      String typeOfLocation = "";
      String id = "";
      if (dataCSV.length == 6){
        id = dataCSV[0];
        locationName = dataCSV[1];
        typeOfLocation = dataCSV[4];
      }

      if (typeOfLocation.equalsIgnoreCase("Buildings")){
        typeOfLocation = "Listed Building";
      }

      //to reorder in cases where String ends with "east of, north-east of" etc...
      if (locationName.endsWith("of")){

        String[] locationNameSegments = locationName.split(";");
        String direction = locationNameSegments[locationNameSegments.length-1];
        String nearbyLoc;
        if (locationNameSegments.length>1){
          nearbyLoc = locationNameSegments[locationNameSegments.length-2];
          locationNameSegments[locationNameSegments.length-1] = nearbyLoc;
        }

        locationNameSegments[locationNameSegments.length-2] = direction;

        String newLocationString = "";
        for (int i = 0; i < locationNameSegments.length; i++){
          newLocationString +=locationNameSegments[i];
        }
        locationName = newLocationString;
      }

      locationName = locationName.replace(";", ",");
      locationName = Tools.formatTitle(locationName);



      DatabaseInteractor databaseInteractor = DatabaseInteractor.getInstance(context);

      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View v = inflater.inflate(R.layout.map_info_window, null);
      TextView title = v.findViewById(R.id.map_entity_title);
      TextView type = v.findViewById(R.id.type_of_marker);
      ImageView image = v.findViewById(R.id.map_info_window_image);
      ImageView star = v.findViewById(R.id.map_checked_in_star);
      title.setText(locationName);
      type.setText(typeOfLocation);
      if (databaseInteractor.isCheckedInLandmark(id)){
        star.setVisibility(View.VISIBLE);
      }
      else{
        star.setVisibility(View.INVISIBLE);
      }
      return v;

    }

    else return null;
  }

  @Override
  public View getInfoContents(Marker marker) {
    return null;
  }
}
