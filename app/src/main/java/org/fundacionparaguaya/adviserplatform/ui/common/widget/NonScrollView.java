package org.fundacionparaguaya.adviserplatform.ui.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * A scroll view that isn't able to be scrolled
 */

public class NonScrollView extends ScrollView {
    // true if we can scroll the ScrollView
    // false if we cannot scroll
    private boolean scrollable = true;

    public NonScrollView(Context context) {
        super(context);
    }

    public NonScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollingEnabled(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        if (!scrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }
}
