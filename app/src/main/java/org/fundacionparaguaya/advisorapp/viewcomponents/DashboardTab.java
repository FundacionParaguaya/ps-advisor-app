package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
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

    private AppCompatImageView mImageIcon;
    private TextView mTextViewCaption;
    private LinearLayout mTabLayout;

    private static boolean DEFAULT_SELECTED_STATE = false;

    private TabType mTabType;

    public enum TabType {
        FAMILY,
        MAP,
        SOCIAL,
        SETTINGS
    }

    public DashboardTab(Context context, AttributeSet attr){
        super(context, attr);

        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dashboardtab, this);

        mImageIcon = findViewById(R.id.imageView_icon);
        mTextViewCaption = findViewById(R.id.textView_caption);
        mTabLayout = findViewById(R.id.dashboardtab);

        //Find custom xml attributes and apply them
        TypedArray attrs = context.getTheme().obtainStyledAttributes(attr, R.styleable.DashboardTab,0, 0);
        try {
            mImageIcon.setImageResource(attrs.getResourceId(R.styleable.DashboardTab_tabImage, R.drawable.dashtab_friendsicon)); //set image to icon
            mTextViewCaption.setText(attrs.getResourceId(R.styleable.DashboardTab_tabCaption, R.string.familytab_title));                //set caption text

            boolean showCaption = attrs.getBoolean(R.styleable.DashboardTab_showCaption, true);

            //hide caption, used when in portrait mode
            if(!showCaption)
            {
                mTextViewCaption.setVisibility(GONE);
            }
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
    @SuppressWarnings("RestrictedApi") //Google bug: https://stackoverflow.com/questions/41150995/appcompatactivity-oncreate-can-only-be-called-from-within-the-same-library-group/41251316#41251316)
    public void setSelected(boolean isSelected){
        if (isSelected) {
            mImageIcon.setSupportImageTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));
            mTextViewCaption.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));//Change Text Color
        } else {
            mImageIcon.setSupportImageTintList(ContextCompat.getColorStateList(context, R.color.app_lightgray));
            mTextViewCaption.setTextColor(getResources().getColor(R.color.app_lightgray));//Change Text Color
        }
    }

    public void setText(int id){
        mTextViewCaption.setText(id);
    }

}