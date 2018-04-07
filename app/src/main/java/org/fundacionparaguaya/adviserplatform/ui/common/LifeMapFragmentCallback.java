package org.fundacionparaguaya.adviserplatform.ui.common;

import android.arch.lifecycle.LiveData;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;

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
