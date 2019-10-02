package com.britishheritage.android.britishheritage.Maps.LocationQueries;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonGeometryObject {

    public String formatted_address;
    public JSONObject location;
    public String location_type;
    public JSONObject viewport;
    //public JSONObject bounds;

    //empty constructor
    public JsonGeometryObject() {

    }
    /**
     *
     * @param jsonObject takes the raw JsonObject from the JsonArray returned from
     *                   the API
     * @return returns a structured JsonGeometryObject which contains the location
     * data we require
     */
    public static JsonGeometryObject fromJson(JSONObject jsonObject){

        JsonGeometryObject jsonGeometryObject = new JsonGeometryObject();

        if (jsonObject.has("formatted_address")){
            try {
                jsonGeometryObject.formatted_address = jsonObject.getString("formatted_address");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.has("geometry")){

            try{
                JSONObject geoObject = jsonObject.getJSONObject("geometry");

                if (geoObject.has("location")){
                    jsonGeometryObject.location = geoObject.getJSONObject("location");
                }
                if (geoObject.has("location_type")){
                    jsonGeometryObject.location_type = geoObject.getString("location_type");
                }
                if (geoObject.has("viewport")){
                    jsonGeometryObject.viewport = geoObject.getJSONObject("viewport");
                }
                return jsonGeometryObject;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonGeometryObject;
    }
}
