package com.britishheritage.android.britishheritage.Home;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.britishheritage.android.britishheritage.Base.BaseSideView;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Main.Adapters.LandmarksAdapter;
import com.britishheritage.android.britishheritage.Main.Adapters.VerticalLandmarksAdapter;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SearchLandmarkView extends BaseSideView {

    private ImageView closeButton;
    private EditText searchText;
    private RecyclerView searchRecyclerView;
    private DatabaseInteractor databaseInteractor;
    private float x1;
    private float x2;
    private float MIN_DISTANCE = 100;
    private GestureDetector detector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private LiveData<List<Landmark>> landmarkLiveData = new MutableLiveData<>();
    private VerticalLandmarksAdapter verticalLandmarksAdapter;
    private LinearLayoutManager landmarksManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    private LandmarksAdapter.Listener onLandmarkClickListener;

    public SearchLandmarkView(Context context) {
        super(context);

    }

    public SearchLandmarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchLandmarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnLandmarkClickListener(LandmarksAdapter.Listener listener){
        this.onLandmarkClickListener = listener;
    }

    @Override
    public void init(Context context) {
        databaseInteractor = DatabaseInteractor.getInstance(context);
        View v = LayoutInflater.from(context).inflate(R.layout.search_landmark_view, this, true);
        searchText = v.findViewById(R.id.searchbar);
        searchText.setHint(context.getString(R.string.enter_your_query));
        closeButton = v.findViewById(R.id.close_button);
        searchRecyclerView = v.findViewById(R.id.search_recyclerView);
        landmarkLiveData = databaseInteractor.searchDb("dolmen");
        landmarkLiveData.observeForever(this::processLandmarks);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null && s.length() != 0) {
                    landmarkLiveData = databaseInteractor.searchDb(s.toString());
                    landmarkLiveData.observeForever(SearchLandmarkView.this::processLandmarks);
                }
            }
        });

        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closePane();
            }
        });

        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            this.setMinHeight(height);
        }
        catch (Exception e){
            Timber.e(e);
        }



    }

    private void processLandmarks(List<Landmark> landmarks){
        verticalLandmarksAdapter = new VerticalLandmarksAdapter(landmarks, getContext(), onLandmarkClickListener);
        searchRecyclerView.setLayoutManager(landmarksManager);
        searchRecyclerView.setAdapter(verticalLandmarksAdapter);
    }

}
