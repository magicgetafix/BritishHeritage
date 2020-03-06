package com.britishheritage.android.britishheritage.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.britishheritage.android.britishheritage.Daos.LandmarkDao;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Model.Realm.FavouriteLandmarkRealmObj;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.Realm.WikiLandmarkRealmObj;
import com.britishheritage.android.britishheritage.Model.Review;
import com.britishheritage.android.britishheritage.Model.WikiLandmark;
import com.britishheritage.android.britishheritage.Response.Geoname;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class DatabaseInteractor {

    private static DatabaseInteractor instance;
    private Context context;
    private LandmarkDatabase db;
    private Realm realm;
    private SharedPreferences sharedPrefs;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reviewReference;

    public static DatabaseInteractor getInstance(Context context){
        if (instance == null){
            instance = new DatabaseInteractor(context);
        }
        return instance;
    }

    public DatabaseInteractor(Context context){
        this.context = context;
        db = Room.databaseBuilder(context, LandmarkDatabase.class, Constants.DATABASE_NAME).build();
        Realm.init(context);
        try{
            realm = Realm.getDefaultInstance();

        } catch (Exception e){

            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
        }
        sharedPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        reviewReference = firebaseDatabase.getReference().child("reviews");

    }

    public void addAllLandmarks(List<Landmark> landmarks){
        new insertLandmarkAsyncTask(db.landmarkDao()).execute(landmarks);
    }

    private static class insertLandmarkAsyncTask extends AsyncTask<List<Landmark>, Void, Void>{

        private LandmarkDao landmarkDao;

        insertLandmarkAsyncTask(LandmarkDao landmarkDao){
            this.landmarkDao = landmarkDao;
        }

        @Override
        protected Void doInBackground(List<Landmark>... lists) {
            landmarkDao.insert(lists[0]);
            return null;
        }
    }

    public LiveData<Integer> getDatabaseSize(){
        return db.landmarkDao().getNumberOfEntries();
    }

    /**A method which returns a LiveData<List>of Landmarks
     *
     * @param topRightLatLng the top right or north east coordinates of the map
     * @param bottomLeftLatLng the bottom left or south west coordinates of the map
     * @return
     */
    public LiveData<List<Landmark>> getLandmarksInBox(LatLng topRightLatLng, LatLng bottomLeftLatLng){

        double maxLat = topRightLatLng.latitude;
        double minLat = bottomLeftLatLng.latitude;
        double maxLong = topRightLatLng.longitude;
        double minLong = bottomLeftLatLng.longitude;
        return db.landmarkDao().getNearestLandmarks(maxLat, minLat, maxLong, minLong);
    }

    public void addFavourite(Landmark landmark){

        FavouriteLandmarkRealmObj favourite = new FavouriteLandmarkRealmObj(landmark);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(favourite);
        realm.commitTransaction();

    }

    public RealmResults<FavouriteLandmarkRealmObj> getFavourites(){
        return realm.where(FavouriteLandmarkRealmObj.class)
                .sort("latitude", Sort.DESCENDING)
                .findAllAsync();
    }

    public void addWikiLandmarks(List<Geoname> geonameList){

        realm.beginTransaction();
        for (Geoname geoname: geonameList){
            WikiLandmarkRealmObj wikiLandmark = new WikiLandmarkRealmObj(geoname);
            realm.copyToRealmOrUpdate(wikiLandmark);
        }
        realm.commitTransaction();
    }

    public RealmResults<WikiLandmarkRealmObj> getNearbyWikiLandmarks(LatLng latLng){

        BigDecimal bigDecimalLat = new BigDecimal(latLng.latitude).setScale(1, RoundingMode.HALF_DOWN);
        BigDecimal bigDecimalLng = new BigDecimal(latLng.longitude).setScale(1, RoundingMode.HALF_DOWN);

        return realm.where(WikiLandmarkRealmObj.class)
                .equalTo("lat", bigDecimalLat.doubleValue())
                .equalTo("lng", bigDecimalLng.doubleValue())
                .sort("title", Sort.ASCENDING)
                .findAll();

    }

    public void upvoteReview(String reviewId, Review review, Landmark landmark){
        sharedPrefs.edit().putBoolean(reviewId+"_up", true).apply();
        sharedPrefs.edit().putBoolean(reviewId+"_down", false).apply();
        reviewReference.child(landmark.getId())
                       .child(review.getUserId())
                       .child(Constants.REVIEW_SCORE_KEY)
                       .setValue(review.getUpvotes()+1);

    }

    public boolean hasReviewBeenUpvoted(String reviewId){
        return sharedPrefs.getBoolean(reviewId+"_up", false);
    }

    public void downvoteReview(String reviewId, Review review, Landmark landmark){
        sharedPrefs.edit().putBoolean(reviewId+"_dwn", true).apply();
        sharedPrefs.edit().putBoolean(reviewId+"_up", false).apply();
        reviewReference.child(landmark.getId())
                .child(review.getUserId())
                .child(Constants.REVIEW_SCORE_KEY)
                .setValue(review.getUpvotes()-1);
    }

    public boolean hasReviewBeenDownvoted(String reviewId){
        return sharedPrefs.getBoolean(reviewId+"_dwn", false);
    }

    public void addReviewToLandmark(String landmarkId, Review review){
        reviewReference.child(landmarkId).child(review.getUserId()).setValue(review);
    }

    public LiveData<List<Review>> getReviews(String landmarkId){

        MutableLiveData<List<Review>> reviewLiveData = new MutableLiveData<>();
        List<Review> reviews = new ArrayList<>();
        Review firstReview = new Review();
        firstReview.setAsPlaceholder(true);

        reviews.add(firstReview);

        reviewReference.child(landmarkId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> snapshotIterator = dataSnapshot.getChildren().iterator();
                while (snapshotIterator.hasNext()){
                    DataSnapshot snapshot = snapshotIterator.next();
                    Timber.d(snapshot.toString());
                    Review review = snapshot.getValue(Review.class);
                    if (review!=null){
                        reviews.add(review);
                    }
                }
                Timber.d("Number of reviews: "+reviews.size());
                reviewLiveData.setValue(reviews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reviews.clear();
                Timber.e(databaseError.getMessage());
            }
        });

        return reviewLiveData;
    }
}
