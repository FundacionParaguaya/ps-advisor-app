package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

/**
 * Just an example of the TabbedFrag class. Jee wiz, isn't this easy?
 */

public class FamilyTabbedFragment extends TabbedFrag {
    AllFamiliesStackedFrag mFrag1;

    static String ALL_FAMILIES_TAG = "ALL_FAM";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager manager = getFragmentManager();
        mFrag1 = (AllFamiliesStackedFrag) manager.findFragmentByTag(ALL_FAMILIES_TAG);

        if (mFrag1 == null) {
            mFrag1 = new AllFamiliesStackedFrag();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setInitialFragment(mFrag1);
    }
}
