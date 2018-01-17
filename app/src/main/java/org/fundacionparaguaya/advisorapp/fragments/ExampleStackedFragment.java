package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.fundacionparaguaya.advisorapp.R;

/**
 * An Example of the StackedFrag class
 *
 * The only important thing in this entire class is the navigateTo(StackedFrag) function called in onCreate()
 */

public class ExampleStackedFragment extends StackedFrag
{
    public static String BUNDLE_ID_TEXT_TO_DISPLAY = "TEXT_TO_DISPLAY";

    public String mDisplayText;
    public TextView mTextView;

    public Button mButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null && getArguments().getString(BUNDLE_ID_TEXT_TO_DISPLAY) != null)
        {
            mDisplayText = getArguments().getString(BUNDLE_ID_TEXT_TO_DISPLAY);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_test, container, false);

        mTextView = (TextView) rootView.findViewById(R.id.textView);
        mTextView.setText(mDisplayText);

        mButton = (Button)rootView.findViewById(R.id.button);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTextView.setText(mDisplayText);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //navigate to a new fragment and increment the label when the button is clicked
        mButton.setOnClickListener((clickEvent) -> {
            if(getParentFragment()!=null){
                navigateTo(ExampleStackedFragment.build(getParentFragment().getChildFragmentManager()
                        .getBackStackEntryCount() + 2));
            }
        });
    }

    /**
     * Constructs this fragment with necessary arguments.
     *
     * @param fragmentNumber number to display after "Fragment " in textView
     * @return A constructed ExampleStackedFragment
     */
    public static ExampleStackedFragment build(int fragmentNumber)
    {
        ExampleStackedFragment fragment = new ExampleStackedFragment();
        Bundle args = new Bundle();
        args.putString(ExampleStackedFragment.BUNDLE_ID_TEXT_TO_DISPLAY, "Fragment " + fragmentNumber);
        fragment.setArguments(args);

        return fragment;
    }
}
