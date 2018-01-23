package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.activities.LoginActivity;
import org.fundacionparaguaya.advisorapp.activities.SurveyActivity;
import org.fundacionparaguaya.advisorapp.adapters.FamiliesAdapter;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.viewmodels.AllFamiliesViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;

import java.util.List;

import javax.inject.Inject;

/**
 * The fragment that displays all of the families the advisor is working with, and upcoming visits.
 * It will allow them to search the families they're working with, and open up the family records by tapping
 * on the family cards.
 */

public class AllFamiliesStackedFrag extends StackedFrag implements View.OnClickListener {

    private FamiliesAdapter mFamiliesAdapter;

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    AllFamiliesViewModel mAllFamiliesViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inject the view model factory
        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //inject dependencies for view model
        mAllFamiliesViewModel = ViewModelProviders
                .of((FragmentActivity) getActivity(), mViewModelFactory)
                .get(AllFamiliesViewModel.class);

        mFamiliesAdapter = new FamiliesAdapter();

        //subscribe to all call backs from the view model
        subscribeToViewModel(mAllFamiliesViewModel);

        mFamiliesAdapter.addFamilySelectedHandler(new FamiliesAdapter.FamilySelectedHandler() {
            @Override
            public void onFamilySelected(FamiliesAdapter.FamilySelectedEvent e) {
                Intent surveyIntent = SurveyActivity.build(getContext(), e.getSelectedFamily());
                startActivity(surveyIntent);
            }
        });
    }

    /**
     * Subscribe to all of the required call backs in the view model (ex. for LiveData objects)
     *
     * @param viewModel ViewModel for this View
     */
    private void subscribeToViewModel(@NonNull AllFamiliesViewModel viewModel) {

        //attach a call back from the families to update the families list
        viewModel.getFamilies().observe(this, (familiesList) -> {
            mFamiliesAdapter.setFamilyList(familiesList);
        });

        //additional callbacks should go here
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton addButton = view.findViewById(R.id.add_families_button);

        addButton.setOnClickListener((event) ->
        {
            mAllFamiliesViewModel.sync();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.families_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.all_families_view);

        //see: https://stackoverflow.com/questions/16886077/android-scrollview-doesnt-start-at-top-but-at-the-beginning-of-the-gridview
        recyclerView.setFocusable(false);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mFamiliesAdapter);

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}



