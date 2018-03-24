package org.fundacionparaguaya.advisorapp.fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

/**
 * Abstract class that all survey fragments subclass. (Intro, background, indicators, and review)
 */

public abstract class AbstractSurveyFragment extends Fragment implements Step {
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

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }
}
