package com.hukex.punpun.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

// To fix  java.lang.IllegalArgumentException: pointerIndex out of range .... Dammn
public class ViewPagerFixed extends androidx.viewpager.widget.ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
        }
        return false;
    }

    // When rotate there was a problem is you want to add margin between images =)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w - this.getPageMargin(), h, oldw - this.getPageMargin(), oldh);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
        }
        return false;
    }
}