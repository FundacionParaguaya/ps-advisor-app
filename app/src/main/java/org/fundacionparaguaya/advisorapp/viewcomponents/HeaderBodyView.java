package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.fundacionparaguaya.advisorapp.R;

/**
 * A simple, reusable custom view that shows a header with body text underneath.
 */

public class HeaderBodyView extends LinearLayout {

    TextView mTvHeader;
    TextView mTvBody;

    public HeaderBodyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_headerbodyview, this, true);

        mTvHeader = findViewById(R.id.tv_header);
        mTvBody = findViewById(R.id.tv_body);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IndicatorCard, 0, 0);

        try {
            setHeaderText(a.getString(R.styleable.HeaderBodyView_header));
            setBodyText(a.getString(R.styleable.HeaderBodyView_body));
        }
        finally {
            a.recycle();
        }
    }

    public void setHeaderText(String text)
    {
        mTvHeader.setText(text);
    }

    public void setBodyText(String text)
    {
        mTvBody.setText(text);
    }
}
