package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.Indicator;

/**
 * Created by alex on 1/28/2018.
 */

public class SurveySummaryIndicators extends LinearLayout{

    Context context;
    AttributeSet attributeSet;

    TextView textView;

    public SurveySummaryIndicators(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.attributeSet = attrs;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.item_surveysummary_row, this);

        textView = (TextView) findViewById(R.id.surveysummary_skippedindicatortext);
    }

    public void setIndicator(Indicator indicator) {
        textView.setText(indicator.getName());
    }
}
