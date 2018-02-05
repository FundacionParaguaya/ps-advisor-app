package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.FamiliesAdapter;
import org.fundacionparaguaya.advisorapp.viewmodels.AllFamiliesViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;

import javax.inject.Inject;

import static com.instabug.library.Instabug.getApplicationContext;

/**
 *  The fragment that displays all of the families the advisor is working with, and upcoming visits.
 *  It will allow them to search the families they're working with, and open up the family records by tapping
 *  on the family cards.
 */

public class AllFamiliesStackedFrag extends AbstractStackedFrag implements View.OnClickListener {

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

                int id = e.getSelectedFamily().getId();
                FamilyDetailFrag f = FamilyDetailFrag.build(id);

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
                AbstractStackedFrag addFamily = new AddFamilyFrag();
                navigateTo(addFamily);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_allfamilies, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.all_families_view);

        //see: https://stackoverflow.com/questions/16886077/android-scrollview-doesnt-start-at-top-but-at-the-beginning-of-the-gridview
        recyclerView.setFocusable(false);

        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mFamiliesAdapter);

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}

class Utility {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 280);
        return noOfColumns;
    }
}




