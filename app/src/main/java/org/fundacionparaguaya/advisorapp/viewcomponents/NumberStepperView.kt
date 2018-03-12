package org.fundacionparaguaya.advisorapp.viewcomponents

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import org.fundacionparaguaya.advisorapp.R

/**
 *
 */

class NumberStepperView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    var minValue: Int = Int.MIN_VALUE
    var maxValue: Int = Int.MAX_VALUE

    private val DISABLED_ALPHA = 0.2f
    private val ENABLED_ALPHA = 1f

    private val mMinusButton: AppCompatImageButton
    private val mPlusButton: AppCompatImageButton
    var valueLiveData = MutableLiveData<Int>()

    private var currentValue: Int = 0
    set(i)
    {
        if(i >= minValue || i <= maxValue)
        {
            field = i
            mTextSwitcher.setText(Integer.toString(i))
        }

        valueLiveData.value = i

        updateButtons()
    }

    /* Text switchers allows for nicer animations between values, particularly double digit values */
    private val mTextSwitcher: TextSwitcher

    init {

        val view = View.inflate(context, R.layout.view_numberstepper, null)

        mMinusButton = view.findViewById(R.id.btn_numberstepper_minus)
        mPlusButton = view.findViewById(R.id.btn_numberstepper_plus)
        mTextSwitcher = view.findViewById(R.id.textswitcher_numberstepper)

        mTextSwitcher.setFactory {
            val textView = AppCompatTextView(context, null, 0)
            TextViewCompat.setTextAppearance(textView, R.style.BigHero)
            textView.setTextColor(ContextCompat.getColor(context, R.color.app_white))

            return@setFactory textView
        }

        val inAnim = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_in)

        val outAnim = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_out)

        inAnim.duration = 200
        outAnim.duration = 200

        mTextSwitcher.inAnimation = inAnim
        mTextSwitcher.outAnimation = outAnim

        addView(view)
        addListeners()

        if(isInEditMode)
        {
            mTextSwitcher.setText("2")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        setMeasuredDimension(178, 56)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun addListeners()
    {
        mPlusButton.setOnClickListener({ view -> currentValue++})
        mMinusButton.setOnClickListener({view -> currentValue--})
    }

    private fun updateButtons()
    {
        setButtonEnabled(mMinusButton, currentValue >= minValue)
        setButtonEnabled(mPlusButton, currentValue <= maxValue)
    }

    private fun setButtonEnabled(button: AppCompatImageButton, enabled: Boolean)
    {
        button.isEnabled = enabled

        if(button.isEnabled)
        {
            button.alpha = ENABLED_ALPHA
        }
        else
        {
            button.alpha = DISABLED_ALPHA
        }
    }

    fun getValue() = valueLiveData
}
