package org.fundacionparaguaya.advisorapp.ui.families.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.data.model.Snapshot;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.ui.common.PrioritiesListAdapter;

import javax.inject.Inject;

/**
 * List of priorities on the PrioritiesPage
 * - Only appears in Horizontal mode
 * * Made with love using Super Cow Powers
 */

public class FamilyPrioritiesListFrag extends Fragment implements PrioritiesListAdapter.PriorityClickedHandler {


    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected FamilyDetailViewModel mFamilyViewModel;

    AppCompatTextView mPrioritiesCount;
    RecyclerView mRvIndicatorList;

    private PrioritiesListAdapter mAdapter = new PrioritiesListAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //Need to getParentFragment twice to get to the familydetails fragment
        mFamilyViewModel = ViewModelProviders
                .of(getParentFragment().getParentFragment(), mViewModelFactory)
                .get(FamilyDetailViewModel.class);

        mAdapter.addSelectedPriorityHandler(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_familypage_priorities_prioritylist, container, false);

        mPrioritiesCount = view.findViewById(R.id.family_priorities_list_title);
        mRvIndicatorList = view.findViewById(R.id.family_priorities_list);

        mRvIndicatorList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRvIndicatorList.setAdapter(mAdapter);

        /*
        if(orientation == vertical)
        {
            observe selected snapshot
                if not null, switchTo(detailfragment)
                else switchTo(list fragment)
        }
        else //if horizontal, always display as list
        { //set initial fragment
            switchTo(list fragment)
        }

        TODO: add back button to priority detil fragment that shows up when orientation is veritcal. just set the selected
        priority to null. Make sure tht first one is selected on rotation.

        also, in layout page, make sure that priority fragment is the same instance as the one we switch to here.
         */

        //TODO if vertical, indicator card should be much taller. if horizontal too tho tbh

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            ViewCompat.setBackgroundTintList(mRvIndicatorList, ColorStateList.valueOf(
                    ContextCompat.getColor(getContext(), R.color.app_offwhite)));
        }
        else
        {
            //mAdapter.selectFirst();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeToViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeViewModelObservers();
    }

    private void subscribeToViewModel(){
        mFamilyViewModel.SelectedSnapshot().observe(this, this::updateSnapshot);
        mFamilyViewModel.SelectedPriority().observe(this, mAdapter::setSelected);
    }

    private void updateSnapshot(Snapshot snapshot){
        if(snapshot!=null) {
            mAdapter.setSnapshot(snapshot);
            String title = getContext().getText(R.string.priorities_listcounttitle) +
                    " (" + snapshot.getPriorities().size() + ")";
            mPrioritiesCount.setText(title);
        }
    }

    private void removeViewModelObservers() {
        mFamilyViewModel.SelectedSnapshot().removeObservers(this);
    }

    @Override
    public void onPrioritySelected(PrioritiesListAdapter.PriorityClickedEvent event) {
        mFamilyViewModel.setSelectedPriority(event.getPriority());
    }
}
