package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.LifeMapAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.LifeMapFragmentCallback;
import org.fundacionparaguaya.advisorapp.util.ScreenCalculations;

/**
 * Shows all of the indicators that a family has and their red/yellow/green status. Selecting one opens up a dialog,
 * that when filled out, adds the priority to the view model
 */

public class LifeMapFragment extends Fragment
{
    private static final float INDICATOR_WIDTH = 128;
    private static final float INDICATOR_MARGIN = 32;

    protected RecyclerView mRvIndicators;
    protected LifeMapAdapter mIndicatorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndicatorAdapter = new LifeMapAdapter();
        mIndicatorAdapter.setClickHandler(getCallback());

        getCallback().getPriorities().observe(this, mIndicatorAdapter::setPriorities);
        getCallback().getSnapshotIndicators().observe(this, mIndicatorAdapter::setIndicators);
    }

    public LifeMapFragmentCallback getCallback()
    {
        try
        {
            return ((LifeMapFragmentCallback)getParentFragment());
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Parent fragment of LifeMap must implement LifeMapFragmentCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lifemap, container, false);

        mRvIndicators = v.findViewById(R.id.rv_lifemap_indicators);
        mRvIndicators.setLayoutManager(new GridLayoutManager(getContext(),
                ScreenCalculations.calculateNoOfColumns(INDICATOR_WIDTH, INDICATOR_MARGIN, getContext())));
        mRvIndicators.setAdapter(mIndicatorAdapter);

        return v;
    }
}
