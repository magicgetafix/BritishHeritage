package com.britishheritage.android.britishheritage.LandmarkDetails;

import android.app.Application;

import com.britishheritage.android.britishheritage.Api.FindNearbyWikipediaApi;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Response.NearbyWikipediaResponse;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LandmarkViewModel extends AndroidViewModel {


    public LandmarkViewModel(@NonNull Application application) {
        super(application);
    }

    public void getWikiGeocodeData(LatLng latLng){

        FindNearbyWikipediaApi findNearbyWikipediaApi = getRetrofit().create(FindNearbyWikipediaApi.class);
        Observable<NearbyWikipediaResponse> wikiResponse = findNearbyWikipediaApi.getWikiResponse(true,
                latLng.latitude,
                latLng.longitude,
                Constants.GEOCODE_USERNAME,
                Constants.GEOCODE_STYLE);
        wikiResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(y->{
            y.getGeonames();
        }).subscribe();


    }

    public Retrofit getRetrofit(){

        return new Retrofit.Builder()
                .baseUrl("http://api.geonames.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();


    }
}
