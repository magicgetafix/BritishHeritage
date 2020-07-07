package com.britishheritage.android.britishheritage.LandmarkDetails;

import android.app.Application;

import com.britishheritage.android.britishheritage.Api.FindNearbyWikipediaApi;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.Realm.WikiLandmarkRealmObj;
import com.britishheritage.android.britishheritage.Model.Review;
import com.britishheritage.android.britishheritage.Model.WikiLandmark;
import com.britishheritage.android.britishheritage.Response.Geoname;
import com.britishheritage.android.britishheritage.Response.NearbyWikipediaResponse;
import com.google.android.libraries.maps.model.LatLng;

import org.reactivestreams.Subscription;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class LandmarkViewModel extends AndroidViewModel {

    private DatabaseInteractor databaseInteractor;
    private MutableLiveData<List<WikiLandmark>> wikiLandmarkLiveData = new MutableLiveData<>();
    private LiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();
    private List<WikiLandmark> wikiLandmarkList = new ArrayList<>();
    private RealmResults<WikiLandmarkRealmObj> wikiLandmarkRealmResults;

    private RealmChangeListener<RealmResults<WikiLandmarkRealmObj>> wikiLandmarkChangeListener = new RealmChangeListener<RealmResults<WikiLandmarkRealmObj>>() {
        @Override
        public void onChange(RealmResults<WikiLandmarkRealmObj> wikiLandmarkRealmObjs) {

            wikiLandmarkList.clear();
            Iterator<WikiLandmarkRealmObj> landmarkRealmObjIterator = wikiLandmarkRealmObjs.iterator();
            while (landmarkRealmObjIterator.hasNext()){
                WikiLandmarkRealmObj landmarkRealmObj = landmarkRealmObjIterator.next();
                WikiLandmark wikiLandmark = new WikiLandmark(landmarkRealmObj);
                wikiLandmarkList.add(wikiLandmark);
            }

            wikiLandmarkLiveData.setValue(wikiLandmarkList);
        }
    };

    private Observer<NearbyWikipediaResponse> geoCodeObserver = new Observer<NearbyWikipediaResponse>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(NearbyWikipediaResponse nearbyWikipediaResponse) {

            List<Geoname> geonameList = nearbyWikipediaResponse.getGeonames();
            if (geonameList!=null && !geonameList.isEmpty()){
                if (databaseInteractor!=null){
                    databaseInteractor.addWikiLandmarks(geonameList);
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

        if (wikiLandmarkRealmResults!=null) {
            wikiLandmarkRealmResults.removeChangeListener(wikiLandmarkChangeListener);
        }



        Double lat = latLng.latitude;
        BigDecimal bigDecimalLat = new BigDecimal(lat).setScale(4, RoundingMode.HALF_DOWN);
        lat = bigDecimalLat.doubleValue();
        Double lng = latLng.longitude;
        BigDecimal bigDecimalLng = new BigDecimal(lng).setScale(4, RoundingMode.HALF_DOWN);
        lng = bigDecimalLng.doubleValue();

        FindNearbyWikipediaApi findNearbyWikipediaApi = getRetrofit().create(FindNearbyWikipediaApi.class);
        Observable<NearbyWikipediaResponse> wikiResponse = findNearbyWikipediaApi.getWikiResponse(
                lat,
                lng,
                Constants.GEOCODE_USERNAME,
                "20");
        wikiResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(geoCodeObserver);
        wikiLandmarkRealmResults = databaseInteractor.getNearbyWikiLandmarks(latLng);
        wikiLandmarkRealmResults.addChangeListener(wikiLandmarkChangeListener);
    }

    public LiveData<List<Review>> getReviews(Landmark landmark){
        return databaseInteractor.getReviews(landmark.getId());
    }

    public Retrofit getRetrofit(){

        return new Retrofit.Builder()
                .baseUrl("http://api.geonames.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();


    }

    public LiveData<List<WikiLandmark>> getWikiLandmarkLiveData(){
        return wikiLandmarkLiveData;
    }

    public LiveData<List<Review>> getReviewsLiveData(Landmark landmark){
        return getReviews(landmark);
    }

    public void upvoteReview(Review review, Landmark landmark){
        databaseInteractor.upvoteReview(review.getReviewId(), review, landmark);
    }

    public void downvoteReview(Review review, Landmark landmark){
        databaseInteractor.downvoteReview(review.getReviewId(), review, landmark);
    }
}
