package org.fundacionparaguaya.advisorapp.fragments;

import android.app.Fragment;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.FamilyAdapter;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.viewmodels.AllFamiliesViewModel;


import java.util.List;

/**
 * Created by Mone Elokda on 1/13/2018.
 */

public class AllFamiliesFragment extends StackedFrag implements View.OnClickListener {

    private FamilyAdapter mFamilyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AllFamiliesViewModel viewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(AllFamiliesViewModel.class);
        LiveData<List<Family>> families = viewModel.getFamily();

        mFamilyAdapter = new FamilyAdapter(families.getValue());

        mFamilyAdapter.addFamilySelectedHandler(new FamilyAdapter.FamilySelectedHandler() {
            @Override
            public void onFamilySelected(FamilyAdapter.FamilySelectedEvent e) {
                String FamilyName = e.getSelectedFamily().getName();
                Toast.makeText(getContext(),FamilyName + "Family Selected", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.families_fragment, container, false);

        ImageButton addFamilyButton = (ImageButton) view.findViewById(R.id.add_families_button);

        Uri uri = Uri.parse("https://raw.githubusercontent.com/facebook/fresco/master/docs/static/logo.png");
        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.family_image);
        draweeView.setImageURI(uri);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.all_families_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        return view;
    }

    private void subscribeUi(AllFamiliesViewModel viewModel){
        viewModel.getFamily().observe((LifecycleOwner) this, new Observer<List<Family>>() {
            @Override
            public void onChanged(@Nullable List<Family> families) {
                if(families != null ){
                    mFamilyAdapter.setFamilyList(families);
                }

            }
        });
    }


    @Override
    public void onClick(View view) {

    }
}



