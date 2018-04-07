package org.fundacionparaguaya.adviserplatform.ui.common.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.weiwangcn.betterspinner.library.BetterSpinner;

/**
 * Spinner class with some optimizations.
 *
 * Found on github: https://github.com/Lesilva/BetterSpinner/issues/88
 */
public class EvenBetterSpinner extends BetterSpinner {

    @Nullable private OnDismissListener onDismissListener;
    @Nullable
    private AdapterView.OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;

    public EvenBetterSpinner(Context context) {
        super(context);
        init();
    }

    public EvenBetterSpinner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public EvenBetterSpinner(Context context, AttributeSet attributeSet, int arg2) {
        super(context, attributeSet, arg2);
        init();
    }

    private void init() {
        super.setOnDismissListener(() -> {
            clearFocus();
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
        });
        super.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public void setOnDismissListener(OnDismissListener dismissListener) {
        this.onDismissListener = dismissListener;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        if(position >= 0) {
            selectedPosition = position;
            ListAdapter adapter = getAdapter();
            if (adapter == null) {
                setText("");
                return;
            }
            Object object = getAdapter().getItem(position);
            setText(object == null ? "" : object.toString());
        }
    }

    public void selectFirstItem()
    {
        if(getAdapter().getCount()>0)
        {
            setSelectedPosition(0);
            onItemClick(null, null, getSelectedPosition(), this.getId());
        }
    }
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        //Fix for setText() and disappearing items
    }

    @Nullable
    public Object getSelectedItem() {
        if (selectedPosition < 0) {
            return null;
        }
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return null;
        }
        return adapter.getItem(selectedPosition);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //Fix for empty data
        ListAdapter adapter = getAdapter();
        return !(adapter == null || adapter.isEmpty()) && super.onTouchEvent(event);
    }
}