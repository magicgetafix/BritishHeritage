package com.britishheritage.android.britishheritage.Global;

import android.animation.Animator;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.britishheritage.android.britishheritage.Model.Landmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;

public class Tools {


    public static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    public static void animateToOpaqueAlpha(View view, int milliSeconds){
            view.animate()
                .alpha(1)
                .setDuration(milliSeconds)
                .setListener(null);
    }

    public static void animateToTransAlpha(View view, int milliSeconds){
        view.animate()
                .alpha(0f)
                .setDuration(milliSeconds)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    public static String getRanking(int score){
        int absScore = Math.abs(score);
        int index = absScore/40;
        if (index < Constants.RANKING_ARRAY.length){
            return Constants.RANKING_ARRAY[index];
        }
        int finalIndex = Constants.RANKING_ARRAY.length - 1;
        return Constants.RANKING_ARRAY[finalIndex];
    }

    public static String formatTitle(String title){

        String newTitle = title.replaceAll(" i ", " I ");
        newTitle = newTitle.replaceAll(" ii ", " II ");
        newTitle = newTitle.replaceAll("\"", "").replaceAll("Ww", "WW");

        return newTitle;
    }

    public static void sortLandmarksAtoZ(List<Landmark> landmarkList){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            sortLandmarks(landmarkList);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void sortLandmarks(List<Landmark> landmarks){
        Pattern pattern = Pattern.compile("[0-9]");

        landmarks.sort(new Comparator<Landmark>() {
            @Override
            public int compare(Landmark o1, Landmark o2) {

                if (o1 == null || o2 == null){
                    return 1;
                }
                if (o1.getName() == null || o2.getName() == null){
                    return 1;
                }
                char firstChar = o1.getName().charAt(0);
                Matcher matcher = pattern.matcher(Character.toString(firstChar));
                if (matcher.matches()){
                    return 1;
                }
                
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public static String convertToTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String newStr = "";
        String[] split = text.split(" ");
        for (int i = 0; i<split.length; i++){
            String word = split[i];
            if (word.length() > 1){
                String firstLetter = word.substring(0,1);
                if (firstLetter.equals("\u0027") && word.length() > 2){
                    word = word.substring(0, 2).toUpperCase()+word.substring(2,word.length());
                }
                else {
                    word = word.substring(0, 1).toUpperCase() + word.substring(1, word.length());
                }
            }
            if (i == 0){
                newStr = word;
            }
            else{
                newStr += " "+word;
            }

        }
        if (!newStr.endsWith(".")){
            newStr = newStr+".";
        }
        return newStr;
    }
}
