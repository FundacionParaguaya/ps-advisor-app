package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveySummaryAdapter;

import java.util.List;

/**
 * Created by alex on 1/28/2018.
 */

public class SurveySummaryComponent extends ConstraintLayout {

    Context context;
    AttributeSet attributeSet;

    ImageView layoutIcon;
    TextView layoutTitle;
    TextView numSkipped;

    RecyclerView recyclerView;
    SurveySummaryAdapter rvAdapter;

    public enum SurveySummaryState{COMPLETE, INCOMPLETE}

    SurveySummaryState state;

    public SurveySummaryComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.attributeSet = attrs;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_review_surveysummary, this);

        layoutIcon = (ImageView) findViewById(R.id.surveysummary_icon);
        layoutTitle = (TextView) findViewById(R.id.surveysummary_layout_title);
        numSkipped = (TextView) findViewById(R.id.surveysummary_layout_numskipped);

        recyclerView = (RecyclerView) findViewById(R.id.surveysummary_layout_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SurveySummaryComponent, 0, 0 );

        try {
            layoutTitle.setText(attributes.getResourceId(R.styleable.SurveySummaryComponent_surveysummary_title, -1));
        } finally {
            attributes.recycle();
        }
    }

    public SurveySummaryAdapter getAdapter(){
        return rvAdapter;
    }

    public void setNames(List<String> names){
        rvAdapter = new SurveySummaryAdapter(context,names);
        recyclerView.setAdapter(rvAdapter);
    }

    public void setState(SurveySummaryState state){
        this.state = state;
        switch (state){
            case COMPLETE:
                layoutIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.surveysummary_complete));
                numSkipped.setText(" ");
                break;
            case INCOMPLETE:
                layoutIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.surveysummary_incomplete));
                break;
            default:
                //do nothing
        }
    }

    public void setNumSkipped(int num){

        numSkipped.setText(num + " " + getResources().getString(R.string.survey_summary_questionsleft));

    }


}
