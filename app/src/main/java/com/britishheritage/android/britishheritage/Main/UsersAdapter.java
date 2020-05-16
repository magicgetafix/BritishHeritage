package com.britishheritage.android.britishheritage.Main;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.britishheritage.android.britishheritage.Database.DatabaseInteractor;
import com.britishheritage.android.britishheritage.Model.User;
import com.britishheritage.android.britishheritage.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> userList = new ArrayList<>();
    private Context context;
    private LifecycleOwner lifecycleOwner;
    private boolean noDrawables = false;

    public UsersAdapter(Context context, List<User> userList, LifecycleOwner lifecycleOwner){
        this.userList = userList;
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void clear(){
        userList.clear();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        try {
            view = LayoutInflater.from(context).inflate(R.layout.top_users_card, parent, false);
        }
        catch (OutOfMemoryError error){
            view = LayoutInflater.from(context).inflate(R.layout.top_users_card_no_drawables, parent, false);
            Timber.e(error);
            noDrawables = true;
        }
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.setContent(user, noDrawables);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView usernameTv;
        private TextView scoreTv;
        private TextView numReviews;
        private TextView numOfCheckIns;
        private ImageView userPhotoIV;
        private ImageView genericUserImage;
        private DatabaseInteractor databaseInteractor;
        private Random random = new Random();

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.top_score_username);
            scoreTv = itemView.findViewById(R.id.top_score_points);
            userPhotoIV = itemView.findViewById(R.id.top_score_user_photo);
            numOfCheckIns = itemView.findViewById(R.id.top_score_num_check_ins);
            numReviews = itemView.findViewById(R.id.top_score_num_reviews);
            try {
                genericUserImage = itemView.findViewById(R.id.top_score_user_frame);
            }
            catch (Exception exception){

            }
            databaseInteractor = DatabaseInteractor.getInstance(context);
        }

        public void setContent(User user, boolean noDrawables){
            if (user!=null) {
                if (genericUserImage!=null) {
                    int color = Color.argb(255, 216, random.nextInt(255), random.nextInt(180));
                    genericUserImage.setColorFilter(color);
                }
                usernameTv.setText(user.getUsername());
                //have to use absolute value because Firebase database is only
                //able to order integers in descending order
                int points = Math.abs(user.getTotalPoints());
                String scoreStr = Integer.toString(points);
                scoreTv.setText(scoreStr);
                String reviews = context.getString(R.string.number_of_reviews, Math.abs(user.getNumOfReviews()));
                String checkIns = context.getString(R.string.number_of_check_ins, Math.abs(user.getNumOfCheckIns()));
                numReviews.setText(reviews);
                numOfCheckIns.setText(checkIns);
                try {
                    LiveData<File> fileLiveData = databaseInteractor.getProfilePhoto(user.getId());
                    fileLiveData.observe(lifecycleOwner, file -> {
                        Glide.with(context).load(file).circleCrop().into(userPhotoIV);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!noDrawables){

            }
        }
    }
}
