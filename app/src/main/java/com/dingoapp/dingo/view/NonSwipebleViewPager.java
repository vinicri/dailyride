package com.dingoapp.dingo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by guestguest on 04/04/16.
 */
public class NonSwipebleViewPager extends ViewPager {

    private boolean mAllowSwipe;

    public NonSwipebleViewPager(Context context) {
        super(context);
    }

    public NonSwipebleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAllowSwipe(boolean allow){
        mAllowSwipe = allow;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(mAllowSwipe){
            return super.onInterceptTouchEvent(event);
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mAllowSwipe){
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }
}