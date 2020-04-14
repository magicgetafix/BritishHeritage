package com.britishheritage.android.britishheritage.LandmarkDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Base.BaseActivity;
import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Model.Landmark;
import com.britishheritage.android.britishheritage.Model.Review;
import com.britishheritage.android.britishheritage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class AddReviewDialogFragment extends DialogFragment {

    TextView landmarkTitleTV;
    TextInputLayout titleLayout;
    TextInputLayout reviewTextLayout;
    TextInputEditText titleET;
    TextInputEditText reviewET;
    Button saveReviewButton;
    DatabaseInteractor databaseInteractor;
    ProgressBar progressBar;

    private Landmark mainLandmark;
    private String userId;
    private String username;
    private DialogListener listener;

    public static String ARG_REVIEW_LANDMARK = "british_heritage_review_landmark_id";
    public static String ARG_REVIEW_USERNAME = "british_heritage_review_username";
    public static String ARG_LANDMARK_TITLE = "british_heritage_review_landmark_name";
    public static String ARG_REVIEW_USER_ID = "british_heritage_review_user_id";

    public static AddReviewDialogFragment newInstance(String title, Landmark landmark, String userId, String username) {

        Bundle args = new Bundle();
        args.putString(ARG_LANDMARK_TITLE, title);
        args.putParcelable(ARG_REVIEW_LANDMARK, landmark);
        args.putString(ARG_REVIEW_USERNAME, username);
        args.putString(ARG_REVIEW_USER_ID, userId);
        AddReviewDialogFragment fragment = new AddReviewDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(DialogListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_add_review, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseInteractor = DatabaseInteractor.getInstance(view.getContext());
        titleET = view.findViewById(R.id.add_review_title_edit_text);
        reviewET = view.findViewById(R.id.add_review_text_edit_text);
        saveReviewButton = view.findViewById(R.id.add_review_save_button);
        titleLayout = view.findViewById(R.id.add_review_title_input);
        reviewTextLayout = view.findViewById(R.id.add_review_text_input);
        saveReviewButton = view.findViewById(R.id.add_review_save_button);
        landmarkTitleTV = view.findViewById(R.id.add_review_title);
        progressBar = view.findViewById(R.id.add_review_progress_bar);

        String titleError = getString(R.string.title_error);
        String tooLongReviewError = getString(R.string.review_error);
        String tooShortReview = getString(R.string.short_review_error);
        String reviewNotValid = getString(R.string.review_not_valid);
        titleLayout.setError(titleError);
        titleLayout.setErrorEnabled(false);

        Bundle arguments = getArguments();
        userId = arguments.getString(ARG_REVIEW_USER_ID);
        username = arguments.getString(ARG_REVIEW_USERNAME);
        Landmark landmark = arguments.getParcelable(ARG_REVIEW_LANDMARK);
        String title = arguments.getString(ARG_LANDMARK_TITLE);

        if (title!=null){
            landmarkTitleTV.setText(title);
        }
        if (landmark!=null){
            mainLandmark = landmark;
        }


        saveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleET.getText().toString().trim();
                String text = reviewET.getText().toString().trim();

                if (title.length() < 5 || title.length() > 40){
                    titleLayout.setErrorEnabled(true);
                    return;
                }
                else{
                    titleLayout.setErrorEnabled(false);
                }

                if (text.length() > 400){
                    reviewTextLayout.setError(tooLongReviewError);
                    reviewTextLayout.setErrorEnabled(false);
                    return;
                }

                if (text.length() < 151){
                    reviewTextLayout.setError(tooShortReview);
                    reviewTextLayout.setErrorEnabled(true);
                    return;
                }

                if (!checkReviewIsValid(text)){
                    reviewTextLayout.setError(reviewNotValid);
                    reviewTextLayout.setErrorEnabled(true);
                    return;
                }

                reviewTextLayout.setErrorEnabled(false);

                if (userId!=null && !userId.isEmpty() && username!=null && !username.isEmpty() && mainLandmark!=null){

                    final Review review = new Review(text, title, username, userId, mainLandmark);
                    progressBar.setVisibility(View.VISIBLE);
                    databaseInteractor.addReviewToLandmark(landmark.getId(), review, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);
                                dismiss();
                                if (listener!=null){
                                    listener.onDismiss(review);
                                }
                            }
                            else{
                                FragmentActivity activity = getActivity();
                                progressBar.setVisibility(View.INVISIBLE);
                                if (activity instanceof BaseActivity){
                                    String failedToAddReview = getString(R.string.failed_to_add_review);
                                    ((BaseActivity) activity).showSnackbar(failedToAddReview);
                                }
                            }
                        }
                    });
                }
                else{
                    FragmentActivity activity = getActivity();
                    if (activity instanceof BaseActivity){
                        String failedToAddReview = getString(R.string.failed_to_add_review);
                        ((BaseActivity) activity).showSnackbar(failedToAddReview);
                    }
                    return;
                }
            }
        });
    }

    public boolean checkReviewIsValid(String fullReview){

        String reviewStr = fullReview.toLowerCase();
        String[] commonWordsArray = {"the", "there", "this", "and", "it", "i",
                "is", "see", "saw", "we", "has", "was", "were", "by", "have", "near", "could",
                "find", "found", "in", "on", "really", "can", "you", "from", "but", "as", "would", "so",
                "interest", "if", "also", "because", "only", "nothing", "no", "hard", "area", "our", "visit"};
        int numberOfMatches = 0;
        for (int i = 0; i < commonWordsArray.length; i++){
            String word = commonWordsArray[i];
            if (reviewStr.contains(word)){
                numberOfMatches++;
                if (numberOfMatches >= 5){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public interface DialogListener{

        void onDismiss(Review review);
    }

}
