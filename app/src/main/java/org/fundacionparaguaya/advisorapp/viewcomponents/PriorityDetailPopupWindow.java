package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.PriorityChangeCallback;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Pop up window that allows the user to input some details about the priority..
 *
 * Three questions
 *
 * 1. Why they don't have the priority
 * 2. What will they do to get the priority
 * 3. When they will get the priority
 */

public class PriorityDetailPopupWindow extends BlurPopupWindow implements DatePickerListener, View.OnClickListener{

    private AppCompatImageView mIndicatorColor;
    private TextView mIndicatorTitle;

    private Button mBtnSubmit;
    private ImageButton mBtnExit;

    private AppCompatImageView mImageWhy;
    private AppCompatImageView mImageStrategy;

    /**The priority that was inputted when init. null if new priority
     */
    private LifeMapPriority mGivenPriority;

    /**
     * The priority that is being built with this dialog... it's a copy of the priority inputted (if any) or just
     * a brand new object
     */
    private LifeMapPriority mNewPriority;

    private IndicatorOption mIndicatorOption;

    private EditText mEtWhy;
    private EditText mEtStrategy;
    private HorizontalPicker mHorizontalCal;

    private String mResponseWhy;
    private String mResponseStrategy;
    private Date mResponseWhen;

    private PriorityChangeCallback mCallback;


    public PriorityDetailPopupWindow(@NonNull Context context) {
        super(context);
    }

    private void updateWhy(String s)
    {
        mResponseWhy = s;

        if(s!=null && !s.isEmpty())
        {
            mImageWhy.setVisibility(INVISIBLE);
        }
        else mImageWhy.setVisibility(VISIBLE);

        mNewPriority.setReason(mResponseWhy);
    }

    private void updateStrategy(String s)
    {
        mResponseStrategy = s;

        if(s!=null && !s.isEmpty())
        {
            mImageStrategy.setVisibility(INVISIBLE);
        }
        else mImageStrategy.setVisibility(VISIBLE);

        mNewPriority.setStrategy(mResponseStrategy);
    }

    private void updateWhen(Date d)
    {
        mResponseWhen = d;
        mNewPriority.setWhen(mResponseWhen);
        //mResponseWhen = s;
    }

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_prioritypopup, parent, false);

        mIndicatorColor = view.findViewById(R.id.iv_prioritypopup_color);
        mIndicatorTitle = view.findViewById(R.id.tv_prioritypopup_title);

        mImageWhy = view.findViewById(R.id.iv_prioritypopup_why);
        mImageStrategy = view.findViewById(R.id.iv_prioritypopup_strategy);

        mEtWhy = view.findViewById(R.id.et_prioritypopup_why);
        mEtStrategy = view.findViewById(R.id.et_prioritypopup_strategy);

        mBtnSubmit = view.findViewById(R.id.btn_prioritypopup_submit);
        mBtnExit = view.findViewById(R.id.btn_prioritypopup_close);

        mBtnExit.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);

        mEtWhy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateWhy(mEtWhy.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtStrategy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateStrategy(mEtStrategy.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mHorizontalCal = view.findViewById(R.id.cal_prioritypopup_when);
        mHorizontalCal.setListener(this).init();
        mHorizontalCal.setDateSelectedColor(R.id.indicatorcard_green);

        return view;
    }


    public void setIndicatorOption(IndicatorOption i)
    {
        IndicatorUtilities.setViewColorFromResponse(i, mIndicatorColor);
        mIndicatorTitle.setText(i.getIndicator().getTitle());

        mIndicatorOption = i;

        mNewPriority= new LifeMapPriority(i.getIndicator(), "", "", null);
    }

    public void setPriority(LifeMapPriority p)
    {
        mGivenPriority = p;

        updateWhy(p.getReason());
        updateStrategy(p.getAction());
        updateWhen(p.getEstimatedDate());

        mEtWhy.setText(mResponseWhy);
        mEtStrategy.setText(mResponseStrategy);
        mHorizontalCal.setDate(new DateTime(mResponseWhen));
    }

    public void setResponseCallback(PriorityChangeCallback c)
    {
        mCallback = c;
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        updateWhen(dateSelected.toDate());
    }

    @Override
    public void onClick(View view) {
        PriorityPopupFinishedEvent e = null;

        if(view.equals(mBtnExit))
        {
            e = new PriorityPopupFinishedEvent();
            e.setResultType(PriorityPopupFinishedEvent.ResultType.CANCEL);
        }
        else if(view.equals(mBtnSubmit))
        {
            if(mGivenPriority!=null)
            {
                e = PriorityPopupFinishedEvent.buildReplaceEvent(mGivenPriority, mNewPriority, mIndicatorOption);

                MixpanelHelper.PrioritiesEvent.prioritiesSet(getContext());
            }
            else
            {
                e = new PriorityPopupFinishedEvent(mNewPriority, mIndicatorOption, PriorityPopupFinishedEvent.ResultType.ADD);
            }
            //TODO update for editing existing
        }

        mCallback.onPriorityChanged(this, e);
    }

    public static class PriorityPopupFinishedEvent
    {
        public enum ResultType {ADD, REPLACE, CANCEL, DELETE};

        ResultType mState;
        IndicatorOption mOption;
        LifeMapPriority mNewPriority = null;
        LifeMapPriority mOriginalPriority = null;


        PriorityPopupFinishedEvent()
        {

        }

        PriorityPopupFinishedEvent(IndicatorOption o, ResultType s)
        {
            mState = s;
            mOption = o;
        }

        PriorityPopupFinishedEvent(LifeMapPriority p, IndicatorOption o, ResultType s)
        {
            this(o, s);
            mNewPriority = p;
        }

        private void setResultType(ResultType type)
        {
            mState = type;
        }

        public ResultType getResultType()
        {
            return mState;
        }

        public IndicatorOption getIndicatorOption()
        {
            return mOption;
        }

        public LifeMapPriority getNewPriority()
        {
            return mNewPriority;
        }
        private void setOriginalPriority(LifeMapPriority orig)
        {
            mOriginalPriority = orig;
        }

        public LifeMapPriority getOriginalPriority()
        {
            return mOriginalPriority;
        }

        static PriorityPopupFinishedEvent buildReplaceEvent
                (LifeMapPriority oldPriority, LifeMapPriority newPriority, IndicatorOption o)
        {
            PriorityPopupFinishedEvent e = new PriorityPopupFinishedEvent(newPriority, o, ResultType.REPLACE);
            e.setOriginalPriority(oldPriority);

            return e;
        }
    }

    public static class Builder extends BlurPopupWindow.Builder<PriorityDetailPopupWindow> {

        LifeMapPriority mPriority = null;
        IndicatorOption mIndicatorOption = null;
        PriorityChangeCallback mCallback;

        public Builder(Context context) {
            super(context);
            this.setScaleRatio(0.25f).setGravity(Gravity.CENTER).setBlurRadius(10).setTintColor(0x30000000).setDismissOnTouchBackground(false);
        }

        public Builder setIndicatorOption(IndicatorOption option)
        {
            mIndicatorOption = option;
            return this;
        }

        public Builder setPriority(LifeMapPriority p) {
            mPriority = p;
            return this;
        }

        public Builder setResponseCallback(PriorityChangeCallback c)
        {
            mCallback = c;
            return this;
        }

        @Override
        public PriorityDetailPopupWindow build() {
            PriorityDetailPopupWindow window = super.build();

            if(mIndicatorOption != null)
            {
                window.setIndicatorOption(mIndicatorOption);
            }
            else throw new IllegalArgumentException(PriorityDetailPopupWindow.class.getName() + "" +
                    " requires an IndicatorOption to be set (setIndicatorResponse)");

            if(mPriority != null)
            {
                window.setPriority(mPriority);
            }

            if(mCallback!=null)
            {
                window.setResponseCallback(mCallback);
            }
            else
            {
                Log.e(this.getClass().getName(), "No callback was set for the PriorityPopup.. So I'm not going" +
                        "to be very useful.");
            }

            return window;
        }


        public void onSave()
        {
        }

        @Override
        protected PriorityDetailPopupWindow createPopupWindow() {
            return new PriorityDetailPopupWindow(mContext);
        }
    }
}
