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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.britishheritage.android.britishheritage.Base.BaseSideView;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Global.Constants;
import com.britishheritage.android.britishheritage.Main.Adapters.LandmarksAdapter;
import com.britishheritage.android.britishheritage.Main.Adapters.VerticalLandmarksAdapter;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.R;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private CheckBox englandCheckBox;
    private CheckBox scotlandCheckBox;
    private CheckBox walesCheckBox;
    private ArrayList<String> urlList;
    private String searchTerm = "dolmen";

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
        englandCheckBox = v.findViewById(R.id.checkboxEng);
        scotlandCheckBox = v.findViewById(R.id.checkboxScot);
        walesCheckBox = v.findViewById(R.id.checkboxWales);
        urlList = new ArrayList<>();

        englandCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!urlList.contains(Constants.ENG_START_OF_URL)) {
                        urlList.add(Constants.ENG_START_OF_URL);
                        refreshSearch();
                    }
                }
                else{
                    urlList.remove(Constants.ENG_START_OF_URL);
                    refreshSearch();
                }

            }
        });

        scotlandCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!urlList.contains(Constants.SCOT_START_OF_URL)) {
                        urlList.add(Constants.SCOT_START_OF_URL);
                        refreshSearch();
                    }
                }
                else{
                    urlList.remove(Constants.SCOT_START_OF_URL);
                    refreshSearch();

                }

            }
        });

        walesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!urlList.contains(Constants.WALES_START_OF_URL)) {
                        urlList.add(Constants.WALES_START_OF_URL);
                        refreshSearch();
                    }
                }
                else{
                    urlList.remove(Constants.WALES_START_OF_URL);
                    refreshSearch();

                }

            }
        });

        if (!urlList.contains(Constants.ENG_START_OF_URL)) {
            urlList.add(Constants.ENG_START_OF_URL);
        }
        if (!urlList.contains(Constants.SCOT_START_OF_URL)) {
            urlList.add(Constants.SCOT_START_OF_URL);
        }
        if (!urlList.contains(Constants.WALES_START_OF_URL)) {
            urlList.add(Constants.WALES_START_OF_URL);
        }

        try {
            landmarkLiveData = databaseInteractor.searchDb("dolmen", urlList);
            landmarkLiveData.observeForever(this::processLandmarks);
        }
        catch(Exception e){
            Timber.e(e);
        }

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
                    try {
                        searchTerm = s.toString();
                        landmarkLiveData = databaseInteractor.searchDb(s.toString(), urlList);
                        landmarkLiveData.observeForever(SearchLandmarkView.this::processLandmarks);
                    }
                    catch (Exception e){
                        Timber.e(e);
                    }
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

    private void refreshSearch(){
        try {
            landmarkLiveData = databaseInteractor.searchDb(searchTerm, urlList);
            landmarkLiveData.observeForever(SearchLandmarkView.this :: processLandmarks);
        }
        catch(Exception e){
            Timber.e(e);
        }
    }

    private void processLandmarks(List<Landmark> landmarks){
        verticalLandmarksAdapter = new VerticalLandmarksAdapter(landmarks, getContext(), onLandmarkClickListener);
        searchRecyclerView.setLayoutManager(landmarksManager);
        searchRecyclerView.setAdapter(verticalLandmarksAdapter);
    }

}
