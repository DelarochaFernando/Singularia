package com.delarocha.singularia.auxclasses;

import android.content.Context;
//import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.delarocha.singularia.pageindicator.MyPageIndicator;

import java.lang.reflect.Field;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by jmata on 14/12/2018.
 */

public class ViewPagerCustomScrollSpeed extends ViewPager {

    private FixedSpeedScroller mScroller = null;
    private Context mContext;
    private float downX = 0f;
    private float upX = 0f;
    private Tools tools;
    private int moves = 0;

    public ViewPagerCustomScrollSpeed(Context context) {
        super(context);
        this.mContext = context;
        tools = new Tools(mContext);
        init();
    }

    public ViewPagerCustomScrollSpeed(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        tools = new Tools(mContext);
        init();
    }

    private void init() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            mScroller = new FixedSpeedScroller(mContext,
                    new DecelerateInterpolator());
            scroller.set(this, mScroller);
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        /*switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                if(upX-downX>0){
                    String movement = "derecha";
                    tools.setStringPreference(Tools.SWIPE_DIRECTION,movement);
                }else{
                    String movement = "izquierda";
                    tools.setStringPreference(Tools.SWIPE_DIRECTION,movement);
                }
                break;
        }*/

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moves++;
                Log.i("MOVES", String.valueOf(moves));
                tools.setIntPreference(Tools.COUNT_MOVES,moves);
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                if(upX-downX>0){
                    String movement = "derecha";
                    Log.i("MOVE_HACIA", movement);
                    tools.setStringPreference(Tools.SWIPE_DIRECTION,movement);
                }else{
                    String movement = "izquierda";
                    Log.i("MOVE_HACIA", movement);
                    tools.setStringPreference(Tools.SWIPE_DIRECTION,movement);
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Checks the X position value of the event and compares it to
     * previous MotionEvents. Returns a true/false value based on if the
     * event was an swipe to the right or a swipe to the left.
     *
     * @param event -   Motion Event triggered by the ViewPager
     * @return      -   True if the swipe was from left to right. False otherwise
     */
    private boolean wasSwipeToRightEvent(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                return false;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                return downX - event.getX()>0;
            default:
                return false;
        }
    }

    public void setScrollDuration(int duration){
        mScroller.setScrollDuration(duration);
    }

    private class FixedSpeedScroller extends Scroller {

        private int mDuration = 500;
        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, duration);
        }

        public void setScrollDuration(int Duration){
            mDuration = Duration;
        }
    }
}
