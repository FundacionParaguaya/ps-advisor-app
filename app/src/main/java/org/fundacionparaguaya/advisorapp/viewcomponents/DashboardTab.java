package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;

/**
 * Custom DashTabType for the DashboardTabBarView
 */

public class DashboardTab extends LinearLayout {

    private Context context;

    private ImageView mImageIcon;
    private TextView mTextViewCaption;
    private LinearLayout mTabLayout;

    private static boolean DEFAULT_SELECTED_STATE = false;

    private TabType mTabType;

    public enum TabType {
        FAMILY,
        MAP,
        ARCHIVE,
        SETTINGS
    }

    public DashboardTab(Context context, AttributeSet attr){
        super(context, attr);

        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dashboardtab, this);

        mImageIcon = (ImageView) findViewById(R.id.imageView_icon);
        mTextViewCaption = (TextView) findViewById(R.id.textView_caption);
        mTabLayout = (LinearLayout) findViewById(R.id.dashboardtab);

        //Find custom xml attributes and apply them
        TypedArray attrs = context.getTheme().obtainStyledAttributes(attr, R.styleable.DashboardTab,0, 0);
        try {
            mImageIcon.setImageResource(attrs.getResourceId(R.styleable.DashboardTab_tabImage, R.drawable.dashtab_friendsicon)); //set image to icon
            mTextViewCaption.setText(attrs.getResourceId(R.styleable.DashboardTab_tabCaption, R.string.familytab_title));                //set caption text
            mTabLayout.setBackgroundResource(R.color.dashboardtab_tabnotselected);                                                     //set default background
        } finally {
            attrs.recycle();
        }

        setSelected(DEFAULT_SELECTED_STATE);

    }

    /**
     * Inits this tab with all necessary objects
     *
     * @param type The type of this tab
     * @param listener Listener for onClickEvents
     */
    public void initTab(TabType type, OnClickListener listener)
    {
        this.setOnClickListener(listener);
        this.setTabType(type);
    }

    /**
     * Sets the type for this tab
     *
     * Currently one of: Family, Map, Archive, Settings
     *
     * @param type Type of tab
     */
    public void setTabType(TabType type)
    {
        mTabType = type;
    }

    /**
     * Gets the type for this tab
     *
     * Currently one of: Family, Map, Archive, Settings
     *
     * @return type of tab
     */
    public TabType getTabType()
    {
        return mTabType;
    }

    /**
     * Sets whether or not this tab should be in a selected state
     *
     * @param isSelected Whether or not this tab is in a selected state
     */
    public void setSelected(boolean isSelected){
        if (isSelected) {
            mTabLayout.setBackgroundResource(R.color.dashboardtab_tabSelected);//Change DashTabType Background
            mImageIcon.setColorFilter(new PorterDuffColorFilter(context.getColor(R.color.dashboardtab_iconselected), PorterDuff.Mode.MULTIPLY));//Change Icon Color

            mTextViewCaption.setTextColor(context.getColor(R.color.dashbaordtab_captionselected));//Change Text Color
        } else {
            mTabLayout.setBackgroundResource(R.color.dashboardtab_tabnotselected);//Change DashTabType Background
            mImageIcon.setColorFilter(R.color.dashboardtab_iconnotselected);//Change Icon Color
            mTextViewCaption.setTextColor(getResources().getColor(R.color.dashboardtab__captionnotselected));//Change Text Color
        }
    }

    public void setText(int id){
        mTextViewCaption.setText(id);
    }

}