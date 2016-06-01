package gotr.bgu.final_project;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class NonSwipeableViewPager extends ViewPager {

    private boolean enabled = true;

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (TouchImageView.b) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (TouchImageView.b) {

            try {
                enabled = super.onInterceptTouchEvent(event);
            } catch (Exception e) {
            }
            return enabled;
        }
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}