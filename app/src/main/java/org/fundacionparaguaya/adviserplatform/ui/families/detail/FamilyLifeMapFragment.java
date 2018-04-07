package org.fundacionparaguaya.adviserplatform.ui.families.detail;

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
import org.fundacionparaguaya.adviserplatform.AdviserApplication;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.ui.common.LifeMapAdapter;
import org.fundacionparaguaya.adviserplatform.ui.common.LifeMapFragmentCallback;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;
import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.ui.common.LifeMapFragment;
import org.fundacionparaguaya.adviserplatform.ui.common.widget.EvenBetterSpinner;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;

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
        ((AdviserApplication) getActivity().getApplication())
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
        mFamilyDetailViewModel.Snapshots().removeObservers(this);
        mFamilyDetailViewModel.SelectedSnapshot().removeObservers(this);
    }

    public void addViewModelObservers()
    {
        mFamilyDetailViewModel.Snapshots().observe(this, (snapshots) -> {
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
        return mFamilyDetailViewModel.Priorities();
    }

    @Override
    public LiveData<Collection<IndicatorOption>> getIndicatorResponses() {
        return mFamilyDetailViewModel.SelectedSnapshotIndicators();
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
