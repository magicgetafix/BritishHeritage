package com.britishheritage.android.britishheritage.LandmarkDetails.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Model.Review;
import com.britishheritage.android.britishheritage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LandmarkReviewAdapter extends RecyclerView.Adapter<LandmarkReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList = new ArrayList<>();
    private Context context;
    private ReviewViewHolder.OnClickListener listener;

    public LandmarkReviewAdapter(List<Review> reviewList, Context context, ReviewViewHolder.OnClickListener listener){
       this.reviewList = reviewList;
       this.context = context;
       this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review, parent, false);
        return new ReviewViewHolder(view, this.context, this.listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = this.reviewList.get(position);
        holder.setContent(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder{

        private Context context;
        private DatabaseInteractor databaseInteractor;
        private OnClickListener listener;
        View itemView;
        TextView usernameTextView;
        TextView reviewTitle;
        TextView reviewScoreTextView;
        ImageView userProfilePhoto;
        ImageButton addReviewButton;
        TextView reviewTextView;
        ImageView upvoteReviewIcon;
        ImageView downvoteReviewIcon;
        ImageView reviewDownvotedIV;
        ImageView reviewUpvotedIV;
        ImageButton upvoteButton;
        ImageButton downvoteButton;


        public ReviewViewHolder(@NonNull View itemView, Context context, OnClickListener listener ) {
            super(itemView);
            this.itemView = itemView;
            this.context = context;
            this.listener = listener;
            databaseInteractor = DatabaseInteractor.getInstance(context);
            this.usernameTextView = itemView.findViewById(R.id.review_username);
            this.reviewTitle = itemView.findViewById(R.id.review_title);
            this.reviewScoreTextView = itemView.findViewById(R.id.review_points);
            this.userProfilePhoto = itemView.findViewById(R.id.review_user_photo);
            this.addReviewButton = itemView.findViewById(R.id.review_add_new_review);
            this.reviewTextView = itemView.findViewById(R.id.review_text);
            this.upvoteReviewIcon = itemView.findViewById(R.id.review_upvote);
            this.reviewUpvotedIV = itemView.findViewById(R.id.review_upvoted);
            this.downvoteReviewIcon = itemView.findViewById(R.id.review_downvote);
            this.reviewDownvotedIV = itemView.findViewById(R.id.review_downvoted);
            this.upvoteButton = itemView.findViewById(R.id.upvote__big_button);
            this.downvoteButton = itemView.findViewById(R.id.downvote__big_button);


        }

        public void setContent(Review review){

            if (!review.isPlaceholder()) {
                addReviewButton.setVisibility(View.INVISIBLE);
                usernameTextView.setText(review.getUsername());
                reviewTitle.setText(review.getReviewTitle());
                reviewTextView.setText(review.getText());
                setScore(review, context, reviewScoreTextView);
                downvoteReviewIcon.setVisibility(View.VISIBLE);
                upvoteReviewIcon.setVisibility(View.VISIBLE);
                if (databaseInteractor.hasReviewBeenUpvoted(review.getReviewId())) {
                    reviewUpvotedIV.setVisibility(View.VISIBLE);
                    reviewDownvotedIV.setVisibility(View.INVISIBLE);
                } else if (databaseInteractor.hasReviewBeenDownvoted(review.getReviewId())) {
                    reviewDownvotedIV.setVisibility(View.VISIBLE);
                    reviewUpvotedIV.setVisibility(View.INVISIBLE);
                }

                upvoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!databaseInteractor.hasReviewBeenUpvoted(review.getReviewId())){
                            if (listener!=null){
                                listener.upvoted(review);
                                reviewDownvotedIV.setVisibility(View.INVISIBLE);
                                reviewUpvotedIV.setVisibility(View.VISIBLE);
                                review.addUpvote();
                                setScore(review, context, reviewScoreTextView);
                            }
                        }
                    }
                });
                downvoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!databaseInteractor.hasReviewBeenDownvoted(review.getReviewId())){
                            if (listener!=null){
                                listener.downvoted(review);
                                reviewDownvotedIV.setVisibility(View.VISIBLE);
                                reviewUpvotedIV.setVisibility(View.INVISIBLE);
                                review.downVote();
                                setScore(review, context, reviewScoreTextView);
                            }
                        }
                    }
                });

            }
            else{
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    usernameTextView.setText(user.getDisplayName());
                }
                reviewScoreTextView.setText("");
                reviewDownvotedIV.setVisibility(View.INVISIBLE);
                reviewUpvotedIV.setVisibility(View.INVISIBLE);
                upvoteReviewIcon.setVisibility(View.VISIBLE);
                downvoteReviewIcon.setVisibility(View.VISIBLE);
                addReviewButton.setVisibility(View.VISIBLE);
                addReviewButton.setOnClickListener(v->{
                    if (listener!=null){
                        listener.addReview();
                    }
                });
                reviewTitle.setText(context.getString(R.string.write_review));
            }
        }

        private void setScore(Review review, Context context, TextView reviewScoreTextView) {
            String score = context.getString(R.string.points, review.getUpvotes());
            reviewScoreTextView.setText(score);
        }

        public interface OnClickListener {
            void addReview();
            void upvoted(Review review);
            void downvoted(Review review);
        }
    }
}
