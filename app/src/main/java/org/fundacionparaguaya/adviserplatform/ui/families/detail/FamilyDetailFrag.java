package org.fundacionparaguaya.adviserplatform.ui.families.detail;

import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.instabug.library.Instabug;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.ui.survey.SurveyActivity;
import org.fundacionparaguaya.adviserplatform.ui.common.LifeMapAdapter;
import org.fundacionparaguaya.adviserplatform.ui.common.LifeMapFragmentCallback;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractStackedFrag;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;


public class FamilyDetailFrag extends AbstractStackedFrag implements Observer<Family>, LifeMapFragmentCallback {

    private static String SELECTED_FAMILY_KEY = "SELECTED_FAMILY";

    private TextView mFamilyName;
    private TextView mAddress;
    private TextView mLocation;
    private SimpleDraweeView mFamilyImage;
    private TextView mPhoneNumber;

    int mFamilyId = -1;

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    FamilyDetailViewModel mFamilyInformationViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        ((AdviserAssistantApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mFamilyInformationViewModel = ViewModelProviders
               .of(this, mViewModelFactory)
                .get(FamilyDetailViewModel.class);

        if (savedInstanceState==null && getArguments() != null) {
            Bundle args = getArguments();
            mFamilyId = args.getInt(SELECTED_FAMILY_KEY);
            mFamilyInformationViewModel.setFamily(mFamilyId);
            //wait for family to load here
        }
        else if(getArguments() == null)
        {
            throw new IllegalArgumentException(FamilyDetailFrag.class.getName() + " requires the family id to be displayed" +
                    "to be passed in as an argument.");
        }

        mFamilyInformationViewModel.SelectedSnapshotIndicators().observe(this, indicatorOptions -> {
            Log.d("", "Updated");
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_familydetail, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFamilyName = view.findViewById(R.id.family_view_name);
        mPhoneNumber = view.findViewById(R.id.familyview_phone);
       // mAddress = view.findViewById(R.id.familydetail_location_content);
     //   mLocation = view.findViewById(R.id.description_content);
        mFamilyImage = view.findViewById(R.id.family_image_2);

        ViewPager viewPager = view.findViewById(R.id.pager_familydetail);
        FamilyViewPagerAdapter adapter = new FamilyViewPagerAdapter(getChildFragmentManager());


        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs_familydetail);
        tabLayout.setupWithViewPager(viewPager);

        view.findViewById(R.id.btn_familydetail_newsnapshot).setOnClickListener((v)-> takeSnapshot());

        try{
            //observer is added onViewCreated so the LiveData will renotify the observers when the view is
            // destoryed/recreated
            mFamilyInformationViewModel.CurrentFamily().observe(this, this);
        }
        catch (IllegalStateException e)
        {
            Log.e(FamilyDetailFrag.class.getName(), e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFamilyInformationViewModel.CurrentFamily().removeObserver(this);
    }

    @Override
    public void onChanged(@Nullable Family family) {
        mFamilyName.setText(family.getName());

        /*
        mAddress.setText(family.getAddress());
        mLocation.setText((CharSequence) family.getLocation());


        if (family.getMember() != null){
            mAddress.setText(family.getAddress());
        } else {
            mAddress.setText(getString(R.string.familydetails_locationdefault));
        }  */

        if (family.getMember() != null) {
            mPhoneNumber.setText(family.getMember().getPhoneNumber());
        } else {
            mPhoneNumber.setText(getText(R.string.familydetails_phonenumberdefault));
        }

        if (mFamilyInformationViewModel.hasImageUri()) {
            Uri uri = mFamilyInformationViewModel.getImageUri();
            mFamilyImage.setImageURI(uri);
        }
    }

    public static FamilyDetailFrag build(int familyId)
    {
        Bundle args = new Bundle();
        args.putInt(SELECTED_FAMILY_KEY, familyId);
        FamilyDetailFrag f = new FamilyDetailFrag();
        f.setArguments(args);

        return f;
    }

    public void takeSnapshot() {
        if (mFamilyInformationViewModel.CurrentFamily().getValue().getMember() == null) {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.familydetail_nullmember_title))
                    .setContentText(getString(R.string.familydetail_nullmember_content))
                    .setConfirmText(getString(R.string.all_okay))
                    .setConfirmClickListener(Dialog::dismiss).show();

            Instabug.reportException(new Exception(getString(R.string.familydetail_nullmember_content)));
        }
        else {
            MixpanelHelper.SurveyEvents.startResurvey(getContext());

            Intent surveyIntent = SurveyActivity.build(getContext(),
                    mFamilyInformationViewModel.CurrentFamily().getValue());

            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getContext(),
                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

            startActivity(surveyIntent, bundle);
        }
    }

    @Override
    public LiveData<List<LifeMapPriority>> getPriorities() {
        return mFamilyInformationViewModel.Priorities();
    }

    @Override
    public LiveData<Collection<IndicatorOption>> getIndicatorResponses() {
        return mFamilyInformationViewModel.SelectedSnapshotIndicators();
    }

    @Override
    public void onLifeMapIndicatorClicked(LifeMapAdapter.LifeMapIndicatorClickedEvent e) {

    }

    class FamilyViewPagerAdapter extends FragmentPagerAdapter {
        FamilyViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new FamilyLifeMapFragment();

                case 1:
                    return new FamilyPrioritiesFrag();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position)
            {
                case 0:
                    return getResources().getString(R.string.life_map_title);

                case 1:
                    return getResources().getString(R.string.priorities);

                default:
                    return null;
            }
        }
    }
}
