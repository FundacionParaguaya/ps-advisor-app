package org.fundacionparaguaya.advisorapp.viewcomponents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import java.util.ArrayList;

/**
 * Default class for a SurveyCard inside of the survey
 * Each survey page will have 3 instances of this
 */

public class IndicatorCard extends LinearLayout{

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

    private ViewTreeObserver observer;

    private int height;
    private int width;

    private ArrayList<IndicatorSelectedHandler> indicatorHandlers = new ArrayList<>();

    private IndicatorOption mIndicatorOption;

    public enum CardColor {
        RED, YELLOW, GREEN
    }

    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    private boolean stayedWithinClickDistance;

    @SuppressLint("ClickableViewAccessibility")
    public IndicatorCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.indicator_card, this, true);

        mIndicatorBackground = (LinearLayout) findViewById(R.id.survey_card_selected);
        mIndicatorCard = (CardView) findViewById(R.id.survey_card_background);
        mImage = (SimpleDraweeView) findViewById(R.id.survey_card_image);
        mText = (TextView) findViewById(R.id.survey_card_text);
        mSelectedText = (TextView) findViewById(R.id.indicatorcard_selectedtext);

        mText.setMovementMethod(new ScrollingMovementMethod());

        TypedArray attrs = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.IndicatorCard, 0, 0);

        //When view is created, resize the textview
        observer = mText.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                resize();
            }
        });

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

        try{
            setColor(attrs.getResourceId(R.styleable.IndicatorCard_indicator_color, Color.TRANSPARENT));
            setText(attrs.getResourceId(R.styleable.IndicatorCard_indicator_text, R.string.defaultindicatortext));
            setImage(attrs.getResourceId(R.styleable.IndicatorCard_indicator_image, R.string.family_imagePlaceholder));
        } finally {
            attrs.recycle();
        }
    }

    /**
     * Sets maxHeight for the textview based on the size of the indicator card and the picture
     */
    public void resize(){
       // int picWidth = mImage.getWidth();
     //   mText.setMaxHeight(mIndicatorCard.getHeight() - convertDpToPixel(17)*3 - picWidth*9/16); //Min 16x9 aspect ratio for the image
    }

    public int getIndicatorWidth(){
        return mIndicatorCard.getWidth();
    }

    public int getIndicatorHeight(){
        return mIndicatorCard.getHeight();
    }

    public void setOption(IndicatorOption option)
    {
        mIndicatorOption = option;

        this.setImage(Uri.parse(option.getImageUrl()));
        this.setText(option.getDescription());
    }

    public IndicatorOption getOption()
    {
        return mIndicatorOption;
    }

    public void setSelected(boolean isSelected){
        if (isSelected){
              mIndicatorBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.survey_card_selected));
              mSelectedText.setVisibility(VISIBLE);
        } else {
              mIndicatorBackground.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
              mSelectedText.setVisibility(INVISIBLE);
        }
    }

    public void setColor(CardColor color){
        switch(color) {
            case RED:
                setColor(R.color.indicator_card_red);
                break;
            case YELLOW:
                setColor(R.color.indicator_card_yellow);
                break;
            case GREEN:
                setColor(R.color.indicator_card_green);
                break;
        }
    }

    public void setColor(int color){
        mIndicatorCard.setCardBackgroundColor(ContextCompat.getColor(context, color));
    }

    public void setImage(Uri uri){
        mImage.setImageURI(uri, context);
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

    public static int convertDpToPixel(int dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) Math.round(px);
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

    public void addIndicatorSelectedHandler(IndicatorSelectedHandler handler){
        indicatorHandlers.add(handler);
    }

    private void notifyHandlers(){
        for (IndicatorSelectedHandler handler : indicatorHandlers){
            handler.onIndicatorSelection(this);
        }
    }

    public interface IndicatorSelectedHandler {
        void onIndicatorSelection(IndicatorCard card);
    }

}