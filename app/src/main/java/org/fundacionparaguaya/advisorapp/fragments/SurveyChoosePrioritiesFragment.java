package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.R;

/**
 * Top most fragment for displaying the life map
 */

public class SurveyChoosePrioritiesFragment extends AbstractSurveyFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setShowFooter(false);
        setTitle(getString(R.string.choosepriorities_title));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choosepriorities, container, false);
        return v;
    }

    //live data for priorities


        //on change
        // if no priorities, show turtle image
        //  otherwise on change diff and show priorities.. update header
        //clicking on priority should launch dialog with it prefilled for editing
        //reordering should update order of live data object

        //recycler view
        //when priorities change
        //so if isSelected, just update number
        //or select/deselect
        //do diff?

        //when indicator is clicked -> show popup dialog

}
