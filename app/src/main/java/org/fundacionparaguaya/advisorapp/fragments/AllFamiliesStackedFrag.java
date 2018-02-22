package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.activities.SurveyActivity;
import org.fundacionparaguaya.advisorapp.adapters.FamiliesAdapter;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;
import org.fundacionparaguaya.advisorapp.util.ScreenCalculations;
import org.fundacionparaguaya.advisorapp.viewmodels.AllFamiliesViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;

import javax.inject.Inject;

/**
 *  The fragment that displays all of the families the advisor is working with, and upcoming visits.
 *  It will allow them to search the families they're working with, and open up the family records by tapping
 *  on the family cards.
 */

public class AllFamiliesStackedFrag extends AbstractStackedFrag {

    private FamiliesAdapter mFamiliesAdapter;

    private final static float FAMILY_CARD_WIDTH = 228f;
    private final static float FAMILY_CARD_MARGIN = 24f;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    private AllFamiliesViewModel mAllFamiliesViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inject the view model factory
        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //inject dependencies for view model
        mAllFamiliesViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(AllFamiliesViewModel.class);

        mFamiliesAdapter = new FamiliesAdapter();

        //subscribe to all call backs from the view model
        subscribeToViewModel(mAllFamiliesViewModel);

        mFamiliesAdapter.addFamilySelectedHandler(new FamiliesAdapter.FamilySelectedHandler() {
            @Override

            public void onFamilySelected(FamiliesAdapter.FamilySelectedEvent e) {

                int id = e.getSelectedFamily().getId();
                FamilyDetailFrag f = FamilyDetailFrag.build(id);
                MixpanelHelper.FamilyOpened.openFamily(getContext());

                navigateTo(f);
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent surveyIntent = new Intent(getContext(), SurveyActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

                startActivity(surveyIntent, bundle);

                MixpanelHelper.SurveyEvents.newFamily(getContext());
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_allfamilies, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.all_families_view);

        //see: https://stackoverflow.com/questions/16886077/android-scrollview-doesnt-start-at-top-but-at-the-beginning-of-the-gridview
        recyclerView.setFocusable(false);

        int mNoOfColumns = ScreenCalculations.calculateNoOfColumns(FAMILY_CARD_WIDTH, FAMILY_CARD_MARGIN, getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mFamiliesAdapter);

        return view;
    }
}



