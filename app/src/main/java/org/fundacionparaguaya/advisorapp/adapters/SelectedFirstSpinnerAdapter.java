package org.fundacionparaguaya.advisorapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
/**
 * An adaptor for spinners that puts the selected item as the first in the list. Also includes an option to have an
 * empty first item
 */

public class SelectedFirstSpinnerAdapter<T> extends ArrayAdapter<String> {

    private boolean mHasEmptyPlaceholder = false;

    // Your custom values for the spinner (User)
    protected T[] values;

    //the currently selected item. -1 -> no selection
    private int mSelectedArrayIndex = -1;

    private String mPlaceholderString;

    public SelectedFirstSpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public void showEmptyPlaceholder(String placeholder)
    {
        mPlaceholderString = placeholder;
        mHasEmptyPlaceholder = true;
        mSelectedArrayIndex = -1;
    }

    public void setSelected(T selectedData) {
        int indexOfData = -1;

        if (selectedData == null) indexOfData = 0;
        else if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(selectedData)) indexOfData = i;
            }
        }

        if (indexOfData != -1) {
            mSelectedArrayIndex = indexOfData;
        }
        else {
            Log.e(this.getClass().getName(), "Selected Snapshot was not found in values of Adapter");
        }
    }

    public void setSelected(int spinnerIndex)
    {
        mSelectedArrayIndex = convertSpinnerToValuesIndex(spinnerIndex);
    }

    public void setValues(T[] values)
    {
        this.values = values;

        notifyDataSetChanged();
    }

    public T getDataAt(int position)
    {
        int adjustedPosition = convertSpinnerToValuesIndex(position);

        if(adjustedPosition == -1)
        {
            return null;
        }
        else
        {
            return this.values[adjustedPosition];
        }
    }

    @Nullable
    @Override
    public String getItem(int position) {
        //Array
        //// [   0   ]
        //// [   1   ]   <- selected array index = 1, loc = 0
        //// [   2   ]
        //// [   3   ]

        //Spinner
        ////Index 0: [   1   ] <- currently selected
        ////Index 1: [   0   ]
        ////Index 2: [   2   ]
        ////Index 3: [   3   ]

        //so if index 0 is clicked, that's our currently selected
        //if index 1 is clicked that's actually the value -1

        //adjusted position, when accounting for reordering around selected index
        int adjustedPosition = convertSpinnerToValuesIndex(position);

        if(adjustedPosition == -1)
        {
            return mPlaceholderString;
        }
        else {
            return values[adjustedPosition].toString();
        }
    }

    /** Converts a spinner index to the proper index in values;
     *
     * @param position
     * @return
     */
    private int convertSpinnerToValuesIndex(int position)
    {
        int adjustedPosition;

        if(mSelectedArrayIndex== -1 && mHasEmptyPlaceholder) {
            adjustedPosition=position-1;
        }
        else if(mSelectedArrayIndex==-1) {
            adjustedPosition=position;
        }
        else {
            if (position == 0) {
                adjustedPosition = mSelectedArrayIndex;
            }
            else if (position <= mSelectedArrayIndex) {
                adjustedPosition = position - 1;
            }
            else //if(position> mSelectedArrayIndex)
            {
                adjustedPosition = position;
            }
        }

        return adjustedPosition;
    }

    @Override
    public int getCount() {
        int count;

        if(this.values==null)
        {
            count = 0;
        }
        else if(mSelectedArrayIndex== -1 && mHasEmptyPlaceholder)
        {
            count = values.length+1;
        }
        else
        {
            count = values.length;
        }

        return count;
    }
}

