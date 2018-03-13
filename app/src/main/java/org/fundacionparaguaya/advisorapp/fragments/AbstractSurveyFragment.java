package org.fundacionparaguaya.advisorapp.fragments;

import android.support.v4.app.Fragment;

/**
 * Abstract class that all survey fragments subclass. (Intro, background, indicators, and review)
 */

public abstract class AbstractSurveyFragment extends Fragment{
    private boolean mShowFooter = true;
    private boolean mShowHeader = true;

    private String mTitle;

    public boolean isShowHeader() {
        return mShowHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.mShowHeader= showHeader;
    }

    public boolean isShowFooter() {
        return mShowFooter;
    }

    public void setShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
