package com.britishheritage.android.britishheritage.Model.Realm;

import android.text.Html;

import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Response.Geoname;
import com.britishheritage.android.britishheritage.Response.NearbyWikipediaResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WikiLandmarkRealmObj extends RealmObject {

    @PrimaryKey
    private String url = "";
    private String summary = "";
    private String title = "";
    private double lat = 0.0;
    private double lng = 0.0;

    public WikiLandmarkRealmObj(){

    }

    public WikiLandmarkRealmObj(Geoname geoname){

        String webUrl = geoname.getWikipediaUrl();
        if (webUrl.length()>5) {
            String startOfUrl = webUrl.substring(0, 5);
            if (!startOfUrl.contains("http") && !startOfUrl.contains("HTTP")){
                webUrl = "https://"+webUrl;
            }
        }
        if (webUrl!=null) {
            this.url = webUrl;
        }
        String summary = geoname.getSummary();
        if (summary!=null) {
            summary = Html.fromHtml(summary).toString();
            //removing any weblinks
            summary = summary.replaceAll(Constants.URL_REGEX, "");
            String dots = "(...)";
            if (summary.contains(dots)) {
                summary = summary.substring(0, summary.length() - dots.length());
                summary = summary.trim();
                String finalCharacter = String.valueOf(summary.charAt(summary.length() - 1));
                if (!finalCharacter.equalsIgnoreCase(".")) {
                    summary = summary + "...";
                }
            }
            this.summary = summary;
        }
        if (geoname.getTitle()!=null) {
            this.title = geoname.getTitle();
        }
        //latitude and longitude of two decimal places are accurate to a km- this will allow us to index by more results
        //from around the area
        BigDecimal bigDecimalLat = new BigDecimal(geoname.getLat()).setScale(Constants.BIG_DEC_SCALE, RoundingMode.HALF_DOWN);
        this.lat = bigDecimalLat.doubleValue();
        BigDecimal bigDecimalLng = new BigDecimal(geoname.getLng()).setScale(Constants.BIG_DEC_SCALE, RoundingMode.HALF_DOWN);
        this.lng = bigDecimalLng.doubleValue();
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
