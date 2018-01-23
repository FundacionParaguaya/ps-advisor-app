package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Just an example of the TabbedFrag class. Jee wiz, isn't this easy?
 */

public class ExampleTabbedFragment extends TabbedFrag {
    ExampleStackedFragment mFrag1;

    boolean mHasBeenInitialized = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFrag1 = ExampleStackedFragment.build(1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mHasBeenInitialized) {
            this.setInitialFragment(mFrag1);
            mHasBeenInitialized = true;
        }
    }
}
