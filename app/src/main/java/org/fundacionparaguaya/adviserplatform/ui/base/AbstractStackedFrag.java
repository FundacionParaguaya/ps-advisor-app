package org.fundacionparaguaya.adviserplatform.ui.base;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import org.fundacionparaguaya.adviserplatform.R;

/**
 * A AbstractStackedFrag is a fragment that is nested in a AbstractTabbedFrag. When it needs to navigate, it is able to communicate
 * with the parent fragment.
 */

public abstract class AbstractStackedFrag extends Fragment
{
    private NavigationListener mNavigateCallback;

    boolean mDidEnter = false;
    int lastEnterAnimId = -1;

    /**
     * Gets parent fragment (of type AbstractTabbedFrag) and then calls navigation function. Current
     * fragment gets placed in a stack.
     *
     * @param fragment fragment to navigate to
     */
    public void navigateTo(AbstractStackedFrag fragment)
    {
        mNavigateCallback.onNavigateNext(fragment);
    }

    /**
     * Goes back to the previous fragment
     */
    public void navigateBack()
    {
        mNavigateCallback.onNavigateBack();
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        boolean shouldNotAnimate = enter && mDidEnter && nextAnim == lastEnterAnimId;

        lastEnterAnimId = nextAnim;

        if(enter)
        {
            mDidEnter = true;
            lastEnterAnimId = nextAnim;
        }

        if(shouldNotAnimate) return AnimationUtils.loadAnimation(getActivity(), R.anim.none);
        else
        {
            Animation animation = super.onCreateAnimation(transit, enter, nextAnim);

            // HW layer support only exists on API 11+
            if ( Build.VERSION.SDK_INT == 11) {
                if (animation == null && nextAnim != 0) {
                    animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
                }

                View view = getView();

                if (animation != null && view!=null) {
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        public void onAnimationEnd(Animation animation) {
                            view.setLayerType(View.LAYER_TYPE_NONE, null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }

            return animation;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            //if this is a nested fragment
            if(getParentFragment() != null)
            {
                mNavigateCallback = (NavigationListener) getParentFragment();
            }
            else
            {
                //just nested inside of an activity
                mNavigateCallback = (NavigationListener) context;
            }
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Parent activity or fragment must implement NavigationListener");
        }
    }
}
