package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import org.fundacionparaguaya.advisorapp.R;
import org.jetbrains.annotations.NotNull;

public final class NumberStepperView extends FrameLayout {

    private static final int DESIRED_HEIGHT = 178;
    private static final int DESIRED_WIDTH = 56;
    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;

    private static final float DISABLED_ALPHA = 0.2f;
    private static final float ENABLED_ALPHA = 1f;

    private final AppCompatImageButton mMinusButton;
    private final AppCompatImageButton mPlusButton;

    private int currentValue;
    private final TextSwitcher mTextSwitcher;

    @NotNull
    private final MutableLiveData<Integer> valueLiveData = new MutableLiveData<>();

    public final void setMinValue(int value) {
        minValue = value;
        if(currentValue < value) {
            setCurrentValue(value);
        }
    }

    public final void setMaxValue(int value) {
        maxValue = value;
    }

    public final void setCurrentValue(int i) {
        if(i >= minValue || i <= maxValue) {
            currentValue = i;
            mTextSwitcher.setText(Integer.toString(i));
        }

        valueLiveData.setValue(i);
        updateButtons();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(DESIRED_WIDTH, DESIRED_HEIGHT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void addListeners() {
        this.mPlusButton.setOnClickListener(v -> setCurrentValue(currentValue+1));
        this.mMinusButton.setOnClickListener(v -> setCurrentValue(currentValue-1));
    }

    private void updateButtons() {
        setButtonEnabled(mMinusButton, currentValue >= minValue);
        setButtonEnabled(mPlusButton, currentValue <= maxValue);
    }

    private void setButtonEnabled(AppCompatImageButton button, boolean enabled) {
        button.setEnabled(enabled);
        if(button.isEnabled()) {
            button.setAlpha(ENABLED_ALPHA);
        } else {
            button.setAlpha(DISABLED_ALPHA);
        }

    }

    public final LiveData<Integer> getValue() {
        return this.valueLiveData;
    }

    public NumberStepperView(@NonNull final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View view = View.inflate(context, R.layout.view_numberstepper, null);

        mMinusButton = view.findViewById(R.id.btn_numberstepper_minus);
        mPlusButton = view.findViewById(R.id.btn_numberstepper_plus);
        mTextSwitcher = view.findViewById(R.id.textswitcher_numberstepper);

        mTextSwitcher.setFactory((() -> {
            AppCompatTextView textView = new AppCompatTextView(context, null, 0);
            TextViewCompat.setTextAppearance(textView, R.style.BigHero);
            textView.setTextColor(ContextCompat.getColor(context, R.color.app_white));
            return textView;
        }));

        Animation inAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        inAnim.setDuration(200);
        outAnim.setDuration(200);

        mTextSwitcher.setInAnimation(inAnim);
        mTextSwitcher.setOutAnimation(outAnim);

        addView(view);
        addListeners();

        if(isInEditMode()) {
            mTextSwitcher.setText("2");
        }
    }
}
