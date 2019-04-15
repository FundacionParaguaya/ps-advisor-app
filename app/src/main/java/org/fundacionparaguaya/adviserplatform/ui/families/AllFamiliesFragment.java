package org.fundacionparaguaya.adviserplatform.ui.families;

import android.app.Activity;
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
import android.widget.Toast;

import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.adviserplatform.ui.dashboard.DashActivity;
import org.fundacionparaguaya.adviserplatform.ui.families.detail.FamilyDetailFrag;
import org.fundacionparaguaya.adviserplatform.ui.survey.SurveyActivity;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;
import org.fundacionparaguaya.adviserplatform.util.ScreenUtils;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.assistantadvisor.R;

import javax.inject.Inject;

/**
 *  The fragment that displays all of the families the advisor is working with, and upcoming visits.
 *  It will allow them to search the families they're working with, and open up the family records by tapping
 *  on the family cards.
 */

public class AllFamiliesFragment extends AbstractStackedFrag {

    private FamiliesAdapter mFamiliesAdapter;
    private static final int NEW_FAMILY_REQUEST = 31;

    private final static float FAMILY_CARD_WIDTH = 228f;
    private final static float FAMILY_CARD_MARGIN = 24f;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    public AllFamiliesViewModel mAllFamiliesViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inject the view model factory
        ((AdviserAssistantApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //inject dependencies for view model
        mAllFamiliesViewModel = ViewModelProviders
                .of(getActivity(), mViewModelFactory)
                .get(AllFamiliesViewModel.class);

        mFamiliesAdapter = new FamiliesAdapter();

        //subscribe to all call backs from the view model
        subscribeToViewModel(mAllFamiliesViewModel);

        mFamiliesAdapter.addFamilySelectedHandler(e -> openFamily(e.getSelectedFamily().getId()));
    }

    private void openFamily(int familyId)
    {
        FamilyDetailFrag f = FamilyDetailFrag.build(familyId);
        MixpanelHelper.FamilyOpened.openFamily(getContext());

        navigateTo(f);
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
                if(DashActivity.getSnapshotQueue() >= AppConstants.MAXIMUM_CAPACITY) {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.survey_disable), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Intent surveyIntent = new Intent(getContext(), SurveyActivity.class);
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

                    startActivityForResult(surveyIntent, NEW_FAMILY_REQUEST, bundle);

                    MixpanelHelper.SurveyEvents.newFamily(getContext());
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case NEW_FAMILY_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    openFamily(SurveyActivity.getFamilyId(data));
                }
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_allfamilies, container, false);

        android.support.v7.widget.SearchView mSearchFamilies = view.findViewById(R.id.all_families_search);

        mSearchFamilies.setOnClickListener(v -> mSearchFamilies.setIconified(false));

        mSearchFamilies.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAllFamiliesViewModel.filter(newText);
                return true;
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.all_families_view);

        //see: https://stackoverflow.com/questions/16886077/android-scrollview-doesnt-start-at-top-but-at-the-beginning-of-the-gridview
        recyclerView.setFocusable(false);
        recyclerView.setOnTouchListener((v, event) -> {
// disallow the onTouch for your scrollable parent view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        recyclerView.setNestedScrollingEnabled(true);

        int mNoOfColumns = ScreenUtils.calculateNoOfColumns(FAMILY_CARD_WIDTH, FAMILY_CARD_MARGIN, getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mFamiliesAdapter);

        return view;
    }
}



