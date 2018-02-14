package org.fundacionparaguaya.advisorapp.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;

/**
 * Abstract class that all survey fragments subclass. (Intro, background, indicators, and review)
 */

public abstract class AbstractSurveyFragment extends Fragment{

    private boolean mShowHeader = true;
    private int mHeaderColor = 0;

    private boolean mShowFooter = true;
    private int mFooterColor = 0;

    private String mTitle;

    public boolean isShowHeader() {
        return mShowHeader;
    }

    public void setShowHeader(boolean mShowHeader) {
        this.mShowHeader = mShowHeader;
    }

    public int getHeaderColor() {
        return mHeaderColor;
    }

    public void setHeaderColor(int mHeaderColor) {
        this.mHeaderColor = mHeaderColor;
    }

    public boolean isShowFooter() {
        return mShowFooter;
    }

    public void setShowFooter(boolean mShowFooter) {
        this.mShowFooter = mShowFooter;
    }

    public int getFooterColor() {
        return mFooterColor;
    }

    public void setFooterColor(int mFooterColor) {
        this.mFooterColor = mFooterColor;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
