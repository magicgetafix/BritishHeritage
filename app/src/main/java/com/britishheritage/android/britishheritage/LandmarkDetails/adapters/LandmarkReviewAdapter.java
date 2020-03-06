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
        View view = LayoutInflater.from(context).inflate(R.layout.wiki_landmark_view, parent, false);
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
        TextView reviewScoreTextView;
        ImageView userProfilePhoto;
        ImageButton addReviewButton;
        TextView reviewTextView;
        ImageView upvoteReviewButton;
        ImageView downvoteReviewButton;
        ImageView reviewDownvotedIV;
        ImageView reviewUpvotedIV;


        public ReviewViewHolder(@NonNull View itemView, Context context, OnClickListener listener ) {
            super(itemView);
            this.itemView = itemView;
            this.context = context;
            this.listener = listener;
            databaseInteractor = DatabaseInteractor.getInstance(context);
            this.usernameTextView = itemView.findViewById(R.id.review_username);
            this.reviewScoreTextView = itemView.findViewById(R.id.review_points);
            this.userProfilePhoto = itemView.findViewById(R.id.review_user_photo);
            this.addReviewButton = itemView.findViewById(R.id.review_add_new_review);
            this.reviewTextView = itemView.findViewById(R.id.review_text);
            this.upvoteReviewButton = itemView.findViewById(R.id.review_upvote);
            this.reviewUpvotedIV = itemView.findViewById(R.id.review_upvoted);
            this.downvoteReviewButton = itemView.findViewById(R.id.review_downvote);
            this.reviewDownvotedIV = itemView.findViewById(R.id.review_downvoted);


        }

        public void setContent(Review review){

            if (!review.isPlaceholder()) {
                addReviewButton.setVisibility(View.INVISIBLE);
                usernameTextView.setText(review.getText());
                String score = context.getString(R.string.points, review.getUpvotes());
                reviewScoreTextView.setText(score);
                if (databaseInteractor.hasReviewBeenUpvoted(review.getReviewId())) {
                    reviewUpvotedIV.setVisibility(View.VISIBLE);
                    reviewDownvotedIV.setVisibility(View.INVISIBLE);
                } else if (databaseInteractor.hasReviewBeenDownvoted(review.getReviewId())) {
                    reviewDownvotedIV.setVisibility(View.VISIBLE);
                    reviewUpvotedIV.setVisibility(View.INVISIBLE);
                }
                upvoteReviewButton.setOnClickListener(v->{
                    if (listener!=null){
                        listener.upvoted(review);
                        reviewDownvotedIV.setVisibility(View.INVISIBLE);
                        reviewUpvotedIV.setVisibility(View.VISIBLE);
                    }
                });
                downvoteReviewButton.setOnClickListener(v->{
                    if (listener!=null){
                        listener.downvoted(review);
                        reviewDownvotedIV.setVisibility(View.VISIBLE);
                        reviewUpvotedIV.setVisibility(View.INVISIBLE);
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
                addReviewButton.setVisibility(View.VISIBLE);
                addReviewButton.setOnClickListener(v->{
                    if (listener!=null){
                        listener.addReview();
                    }
                });
            }
        }

        public interface OnClickListener {
            void addReview();
            void upvoted(Review review);
            void downvoted(Review review);
        }
    }
}
