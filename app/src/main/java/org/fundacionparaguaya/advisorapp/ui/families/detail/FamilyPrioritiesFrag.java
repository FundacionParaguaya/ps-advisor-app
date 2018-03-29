package org.fundacionparaguaya.advisorapp.ui.families.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.R;

/**
 * Displays a list of priorities and a description of each priority
 * - Implemented in FamilyDetailFrag
 *
 */

public class FamilyPrioritiesFrag extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_familypriorities, container, false);

        //checkDisplayConditions(view);

        return view;
    }

//    /**
//     * Checks to see size and orientation of the screen,
//     * then sets up the fragment accordingly
//     *
//     * @param view
//     */
//    private void checkDisplayConditions(View view){
//
//
//
//    }
}