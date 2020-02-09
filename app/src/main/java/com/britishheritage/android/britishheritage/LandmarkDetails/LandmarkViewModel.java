package com.britishheritage.android.britishheritage.LandmarkDetails;

import android.app.Application;

import com.britishheritage.android.britishheritage.Api.FindNearbyWikipediaApi;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Response.Geoname;
import com.britishheritage.android.britishheritage.Response.NearbyWikipediaResponse;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class LandmarkViewModel extends AndroidViewModel {

    private DatabaseInteractor databaseInteractor;

    private Observer<NearbyWikipediaResponse> geoCodeObserver = new Observer<NearbyWikipediaResponse>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(NearbyWikipediaResponse nearbyWikipediaResponse) {

            List<Geoname> geonameList = nearbyWikipediaResponse.getGeonames();
            if (geonameList!=null && !geonameList.isEmpty()){
                for (Geoname geoname: geonameList){
                    if (databaseInteractor!=null){
                        databaseInteractor.addWikiLandmark(geoname);
                    }
                }
            }

        }

        @Override
        public void onError(Throwable e) {
            Timber.d(e);
        }

        @Override
        public void onComplete() {

        }
    };


    public LandmarkViewModel(@NonNull Application application) {
        super(application);
        databaseInteractor = DatabaseInteractor.getInstance(application.getApplicationContext());
    }

    public void getWikiGeocodeData(LatLng latLng){

        FindNearbyWikipediaApi findNearbyWikipediaApi = getRetrofit().create(FindNearbyWikipediaApi.class);
        Observable<NearbyWikipediaResponse> wikiResponse = findNearbyWikipediaApi.getWikiResponse(true,
                latLng.latitude,
                latLng.longitude,
                Constants.GEOCODE_USERNAME,
                Constants.GEOCODE_STYLE);
        wikiResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(geoCodeObserver);
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
