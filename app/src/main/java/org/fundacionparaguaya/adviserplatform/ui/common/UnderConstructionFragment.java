package org.fundacionparaguaya.adviserplatform.ui.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractStackedFrag;

/**
 * Page that just says under construction
 */

public class UnderConstructionFragment extends AbstractStackedFrag
{

    private static String PAGE_NAME_KEY = "PAGE_NAME";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_under_construction, container, false);

        TextView tv_title = view.findViewById(R.id.tv_construction_title);

        if(this.getArguments()!=null) {
//            String pageName = this.getArguments().getString(PAGE_NAME_KEY);
//            tv_title.setText(pageName);
        }

        return view;
    }

    public static UnderConstructionFragment build(String pageName)
    {
        Bundle b = new Bundle();
        b.putString(PAGE_NAME_KEY, pageName);

        UnderConstructionFragment fragment = new UnderConstructionFragment();
        fragment.setArguments(b);

        return fragment;
    }
}
