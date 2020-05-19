package com.britishheritage.android.britishheritage.Base;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseSideView extends ConstraintLayout {

    public interface SidePaneCloseListener {
        void onSidePaneClosed();
    }

    private SidePaneCloseListener closeListener;

    // closedTranslationX is the value for the TranslationX attribute of the pane when closed.
// The initial value for 'closed position' is set from the initial xml layout width.
// Animating from the closedTranslationX value to 0 will cause the pane to open.
// Animating the x axis of the opened pane from 0 to closedTranslationX will close the pane.
// The open and close pane animations have the duration set by: PANE_ANIMATION_TIME_MILLIS
    private float closedTranslationX;
    private final static long PANE_ANIMATION_TIME_MILLIS = 250;

    private boolean isOpen = false;
    private boolean isLayedOut;
    private View shimView;  // Reference to an external shim view (may be null).

    public BaseSideView(Context context) {
        super(context);
        //gestureDetector= new GestureDetector(context, new GestureListener());
        init(context);
    }

    public BaseSideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //gestureDetector = new GestureDetector(context, new GestureListener());
        init(context);
    }

    public BaseSideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //gestureDetector = new GestureDetector(context, new GestureListener());
        init(context);
    }


    /**
     * If a 'shim' view is set by calling this function, SidePaneView will:
     *  fade the shim in with openPane and
     *  fade out with closePane
     *
     * @param shimView The view to fade in and out.  Can be null.
     */
    public void setShim(View shimView) {
        this.shimView = shimView;
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Users of SidePaneView must implement this function to inflate the view layout.
     *
     * @param context a valid context
     */
    public abstract void init(Context context);

    /**
     * This function will be called after the pane close animation completes.
     * Override this function if you need to take actions after the pane closes.
     * If you override, you should also call this function if you want to inform listeners.
     */
    public void onPaneClosed() {
        if (closeListener != null) {
            closeListener.onSidePaneClosed();
        }
    }


// Base animation functions...

    public void openPane() {
        isOpen = true;
        // Handle the shim fade-in
        if (shimView != null) {
            shimView.setAlpha(0);
            shimView.setVisibility(View.VISIBLE);
            shimView.animate().alpha(1).setDuration(PANE_ANIMATION_TIME_MILLIS).setListener(null).start();
        }
        // Handle the pane slide-in.  Moving to zero in the x-axis is the open position
        animate().translationX(0).setDuration(PANE_ANIMATION_TIME_MILLIS).setListener(null).start();
    }

    public void closePane() {
        // Handle the shim fade-out
        if (shimView != null) {
            shimView.animate().alpha(0).setDuration(PANE_ANIMATION_TIME_MILLIS).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) { }
                @Override
                public void onAnimationRepeat(Animator animation) { }
                @Override
                public void onAnimationEnd(Animator animation) {
                    // The shim should be removed after it has faded out.
                    shimView.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                    // The shim should be removed after it has faded out.
                    shimView.setVisibility(View.GONE);
                }
            }).start();
        }

        // Handle the pane slide-out
        animate().translationX(closedTranslationX).setDuration(PANE_ANIMATION_TIME_MILLIS).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationRepeat(Animator animation) { }
            @Override
            public void onAnimationEnd(Animator animation) {
                isOpen = false;
                onPaneClosed();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                isOpen = false;
                onPaneClosed();
            }
        }).start();
    }

    @Nullable
    public SidePaneCloseListener getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(@Nullable SidePaneCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    // We need to delay the side menu positioning until the view has completed layout.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isLayedOut) {
            // Place menu off-screen right.
            closedTranslationX = getWidth();
            setTranslationX(closedTranslationX);
            isLayedOut = true;
        }
    }

}
