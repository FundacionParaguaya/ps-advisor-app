package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.activities.SurveyActivity;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.SubTabFragmentCallback;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.viewmodels.FamilyInformationViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.json.JSONObject;

import javax.inject.Inject;


public class FamilyDetailFrag extends AbstractStackedFrag implements Observer<Family>, SubTabFragmentCallback {

    private static String SELECTED_FAMILY_KEY = "SELECTED_FAMILY";

    private TextView mFamilyName;
    private TextView mAddress;
    private TextView mLocation;
    private SimpleDraweeView mFamilyImage;
    private TextView mPhoneNumber;
    private MixpanelAPI mMixpanel;

    int mFamilyId = -1;

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    FamilyInformationViewModel mFamilyInformationViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mFamilyInformationViewModel = ViewModelProviders
                .of((FragmentActivity) getActivity(), mViewModelFactory)
                .get(FamilyInformationViewModel.class);

        mMixpanel = MixpanelAPI.getInstance(getContext(), BuildConfig.MIXPANEL_API_KEY_STRING);

        JSONObject props = new JSONObject();
        mMixpanel.track("Family details checked", props);

        if (getArguments() != null) {
            Bundle args = getArguments();
            mFamilyId = args.getInt(SELECTED_FAMILY_KEY);


            mFamilyInformationViewModel.setFamily(mFamilyId);
            //wait for family to load here
        }
        else
        {
            throw new IllegalArgumentException(FamilyDetailFrag.class.getName() + " requires the family id to be displayed" +
                    "to be passed in as an argument.");
        }
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

        mFamilyName = (TextView) view.findViewById(R.id.family_view_name);
        mPhoneNumber = (TextView) view.findViewById(R.id.familyview_phone);
        mAddress = (TextView) view.findViewById(R.id.familydetail_location_content);
        mLocation = (TextView) view.findViewById(R.id.description_content);
        mFamilyImage = (SimpleDraweeView) view.findViewById(R.id.family_image_2);

        try{
            //observer is added onViewCreated so the LiveData will renotify the observers when the view is
            // destoryed/recreated
            mFamilyInformationViewModel.getCurrentFamily().observe(this, this);
        }
        catch (IllegalStateException e)
        {
            Log.e(FamilyDetailFrag.class.getName(), e.getMessage());
        }


        Uri uri = Uri.parse(getString(R.string.family_imagePlaceholder));
        mFamilyImage.setImageURI(uri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFamilyInformationViewModel.getCurrentFamily().removeObserver(this);
    }

    @Override
    public void onChanged(@Nullable Family family) {
        mFamilyName.setText(family.getName());
        mAddress.setText(family.getAddress());
        mLocation.setText((CharSequence) family.getLocation());

        if (family.getMember() != null){
            mAddress.setText(family.getAddress());
        } else {
            mAddress.setText(getString(R.string.familydetails_locationdefault));
        }

        if (family.getMember() != null) {
            mPhoneNumber.setText(family.getMember().getPhoneNumber());
        } else {
            mPhoneNumber.setText(getText(R.string.familydetails_phonenumberdefault));
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

    @Override
    public void onTakeSnapshot() {
        Intent surveyIntent = SurveyActivity.build(getContext(),
                mFamilyInformationViewModel.getCurrentFamily().getValue());

        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getContext(),
            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        startActivity(surveyIntent, bundle);
    }
}
