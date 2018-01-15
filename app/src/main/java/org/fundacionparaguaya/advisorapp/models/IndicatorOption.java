package org.fundacionparaguaya.advisorapp.models;

import java.net.URL;

/**
 *  An indicator option is a definition for one of the three levels an indicator can have. It's level is determined
 *  by the parent indicator
 */

public class IndicatorOption
{
    enum Level {Red, Yellow, Green, None}

    private URL mImageUrl;
    private String mDescription;

    public boolean equals(IndicatorOption obj)
    {
        return (obj.mImageUrl.equals(mImageUrl) && obj.mDescription.equals(mDescription));
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof IndicatorOption)
        {
            return this.equals((IndicatorOption)obj);
        }
        else return super.equals(obj);
    }
}