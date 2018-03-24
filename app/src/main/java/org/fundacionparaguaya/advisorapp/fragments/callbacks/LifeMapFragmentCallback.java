package org.fundacionparaguaya.advisorapp.fragments.callbacks;

import android.arch.lifecycle.LiveData;
import org.fundacionparaguaya.advisorapp.adapters.LifeMapAdapter;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;

import java.util.Collection;
import java.util.List;

/**
 * Callback for LifeMap fragment
 */

public interface LifeMapFragmentCallback {
    LiveData<List<LifeMapPriority>> getPriorities();
    LiveData<Collection<IndicatorOption>> getIndicatorResponses();
    void onLifeMapIndicatorClicked(LifeMapAdapter.LifeMapIndicatorClickedEvent e);
}
