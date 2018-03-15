package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.util.IndicatorUtilities;

import java.util.ArrayList;

/**
 * Default class for a SurveyCard inside of the survey
 * Each survey page will have 3 instances of this
 */

public class IndicatorCard extends LinearLayout{

    private static final boolean DEFAULT_HORIZONTAL_ATTRIBUTE = false;
    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 1000;

    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;

    private Context context;

    private LinearLayout mIndicatorBackground;
    private CardView mIndicatorCard;
    private SimpleDraweeView mImage;
    private TextView mText;

    private TextView mSelectedText;

    private ArrayList<IndicatorClickedHandler> indicatorHandlers = new ArrayList<>();

    private IndicatorOption mIndicatorOption;

    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    private boolean stayedWithinClickDistance;

    private Uri mImageUri;

    private boolean isSelected = false;

    @SuppressLint("ClickableViewAccessibility")
    public IndicatorCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        TypedArray attrs = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.IndicatorCard, 0, 0);

        LayoutInflater inflater = LayoutInflater.from(context);

        if(attrs.getBoolean(R.styleable.IndicatorCard_horizontal, DEFAULT_HORIZONTAL_ATTRIBUTE))
        {
            //if explicitly set to horizontal, inflate horizontal view
            inflater.inflate(R.layout.view_indicatorcard_horizontal, this, true);
        }
        else
        {   //default inflate vertical card layout
            inflater.inflate(R.layout.indicator_card, this, true);
        }

        mIndicatorBackground = findViewById(R.id.survey_card_selected);
        mIndicatorCard = findViewById(R.id.survey_card_background);
        mImage = findViewById(R.id.survey_card_image);
        mText = findViewById(R.id.survey_card_text);
        mSelectedText = findViewById(R.id.indicatorcard_selectedtext);

        try{
            setText(attrs.getResourceId(R.styleable.IndicatorCard_indicator_text, R.string.defaultindicatortext));
        } finally {
            attrs.recycle();
        }

        if(!isInEditMode()) {
            mText.setMovementMethod(new ScrollingMovementMethod());

            //performClick is added, the fact that the function is still highlighted is a bug in Android Studio
            mText.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        pressStartTime = System.currentTimeMillis();
                        pressedX = event.getX();
                        pressedY = event.getY();
                        stayedWithinClickDistance = true;
                        mText.performClick();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        float x = event.getX();
                        float y = event.getY();
                        float distance = distance(pressedX, pressedY, x, y);
                        if (stayedWithinClickDistance && distance > MAX_CLICK_DISTANCE) {
                            stayedWithinClickDistance = false;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
                            notifyHandlers();
                            return true;
                        }
                    }
                    default:
                        break;
                }
                return false;
            });
        }
    }

    public void setOption(IndicatorOption option)
    {
        mIndicatorOption = option;

        this.setImage(Uri.parse(option.getImageUrl()));
        this.setText(option.getDescription());

        IndicatorUtilities.setViewColorFromLevel(option.getLevel(), mIndicatorCard);
    }

    public IndicatorOption getOption()
    {
        return mIndicatorOption;
    }

    public void setSelected(boolean selected){
        isSelected = selected;
        if (isSelected){
              mIndicatorBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.survey_card_selected));
              mSelectedText.setVisibility(VISIBLE);
        } else {
              mIndicatorBackground.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
              mSelectedText.setVisibility(INVISIBLE);
        }
    }

    public boolean getSelected(){
        return isSelected;
    }

    public void setImage(Uri uri){
        mImageUri = uri;

        mImage.setImageURI(mImageUri, context);
    }

    public void setImage(int image){
        mImage.setImageURI(Uri.parse(getResources().getString(image)));
    }

    public void setText(int id) {
        mText.setText(id);
    }

    public void setText(String text){
        mText.setText(text);
    }

    public void clearImageFromMemory()
    {
        Fresco.getImagePipeline().evictFromCache(mImageUri);
    }

    //performClick is added, the fact that the function is still highlighted is a bug in Android Studio
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                pressStartTime = System.currentTimeMillis();
                pressedX = e.getX();
                pressedY = e.getY();
                stayedWithinClickDistance = true;
                performClick();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (stayedWithinClickDistance && distance(pressedX, pressedY, e.getX(), e.getY()) > MAX_CLICK_DISTANCE) {
                    stayedWithinClickDistance = false;
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                long pressDuration = System.currentTimeMillis() - pressStartTime;
                if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
                    notifyHandlers();
                    return true;
                }
            }
            default:
                break;
        }
        return true;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / getResources().getDisplayMetrics().density;
    }

    public void addIndicatorClickedHandler(IndicatorClickedHandler handler){
        indicatorHandlers.add(handler);
    }

    private void notifyHandlers(){
        for (IndicatorClickedHandler handler : indicatorHandlers){
            handler.onIndicatorSelection(this);
        }
    }

    public interface IndicatorClickedHandler {
        void onIndicatorSelection(IndicatorCard card);
    }

}
