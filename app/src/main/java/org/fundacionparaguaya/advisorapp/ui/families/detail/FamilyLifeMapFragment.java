package org.fundacionparaguaya.advisorapp.ui.families.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.ui.common.LifeMapAdapter;
import org.fundacionparaguaya.advisorapp.ui.common.LifeMapFragmentCallback;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorOption;
import org.fundacionparaguaya.advisorapp.data.model.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.data.model.Snapshot;
import org.fundacionparaguaya.advisorapp.ui.common.LifeMapFragment;
import org.fundacionparaguaya.advisorapp.ui.common.widget.EvenBetterSpinner;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Fragment that displays a life map for a fragment. It uses a {@link LifeMapFragment}
 * and provides all of the necessary callbacks.
 *
 * This fragment requires a {@link FamilyDetailViewModel} to exit
 * with it's context.
 */

public class FamilyLifeMapFragment extends Fragment implements LifeMapFragmentCallback {

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    FamilyDetailViewModel mFamilyDetailViewModel;

    ArrayAdapter<Snapshot> mSpinnerAdapter;
    EvenBetterSpinner mSnapshotSpinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mFamilyDetailViewModel = ViewModelProviders
                .of(getParentFragment(), mViewModelFactory)
                .get(FamilyDetailViewModel.class);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_familylifemap, container, false);

        Fragment lifeMapFragment = getChildFragmentManager().findFragmentByTag(LifeMapFragment.class.getSimpleName());

        if(lifeMapFragment == null)
        {
            lifeMapFragment = new LifeMapFragment();

            getChildFragmentManager()
                    .beginTransaction()
                    .add( R.id.fragment_container, lifeMapFragment, LifeMapFragment.class.getSimpleName())
                    .commit();
        }

        mSnapshotSpinner = view.findViewById(R.id.spinner_familylifemap_snapshot);
        mSpinnerAdapter = new ArrayAdapter<>(this.getContext(), R.layout.item_tv_spinner);

        mSnapshotSpinner.setAdapter(mSpinnerAdapter);

        mSnapshotSpinner.setOnItemClickListener((parent, view1, position, id) ->
                mFamilyDetailViewModel.setSelectedSnapshot(mSpinnerAdapter.getItem(position)));

        addViewModelObservers();

        return view;
    }



    public void removeViewModelObservers()
    {
        mFamilyDetailViewModel.getSnapshots().removeObservers(this);
        mFamilyDetailViewModel.SelectedSnapshot().removeObservers(this);
    }

    public void addViewModelObservers()
    {
        mFamilyDetailViewModel.getSnapshots().observe(this, (snapshots) -> {
            //This is necessary to completely clear the Array Adapter every time snapshots get updated
            mSpinnerAdapter = new ArrayAdapter<>(this.getContext(), R.layout.item_tv_spinner);
            mSnapshotSpinner.setAdapter(mSpinnerAdapter);

            if(snapshots!=null) {
                mSpinnerAdapter.addAll(snapshots);

                mFamilyDetailViewModel.SelectedSnapshot().removeObservers(this);
                mFamilyDetailViewModel.SelectedSnapshot().observe(this, (snapshot) -> {
                    if(snapshot == null) {
                        mFamilyDetailViewModel.selectFirstSnapshot();
                    }
                    else
                    {
                        mSnapshotSpinner.setSelectedPosition(mSpinnerAdapter.getPosition(snapshot));
                    }
                });
            }
        });
    }

    @Override
    public LiveData<List<LifeMapPriority>> getPriorities() {
        return mFamilyDetailViewModel.getPriorities();
    }

    @Override
    public LiveData<Collection<IndicatorOption>> getIndicatorResponses() {
        return mFamilyDetailViewModel.getSnapshotIndicators();
    }

    @Override
    public void onLifeMapIndicatorClicked(LifeMapAdapter.LifeMapIndicatorClickedEvent e) {

    }

    @Override
    public void onDestroyView() {
        removeViewModelObservers();
        mSnapshotSpinner = null;

        super.onDestroyView();
    }
}
