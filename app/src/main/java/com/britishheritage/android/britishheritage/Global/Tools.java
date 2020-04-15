package com.britishheritage.android.britishheritage.Global;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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

    public static String getRanking(int score){
        int absScore = Math.abs(score);
        int index = absScore/50;
        if (index < Constants.RANKING_ARRAY.length){
            return Constants.RANKING_ARRAY[index];
        }
        int finalIndex = Constants.RANKING_ARRAY.length - 1;
        return Constants.RANKING_ARRAY[finalIndex];
    }
}
