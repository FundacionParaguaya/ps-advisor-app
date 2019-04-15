package org.fundacionparaguaya.adviserplatform.util;

import android.util.Log;
import com.instabug.library.Instabug;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;

import timber.log.Timber;

/**
 * Timber Logging Tree for Release Builds
 *
 * https://medium.com/@caueferreira/timber-enhancing-your-logging-experience-330e8af97341
 */

public class ReleaseLoggingTree extends Timber.Tree {

    MixpanelHelper mixpanelHelper;

    public ReleaseLoggingTree(AdviserAssistantApplication adviserAssistantApplication) {
        super();

        mixpanelHelper = new MixpanelHelper(adviserAssistantApplication);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if(priority == Log.ERROR || priority == Log.WARN) {
            if (t != null) {
                Instabug.reportException(t);
            }

            if (tag!=null && tag.contains("Repository")) {
                mixpanelHelper.syncError(tag + ": " + message);
            }

            //todo: log error somewhere else
        }
    }
}
