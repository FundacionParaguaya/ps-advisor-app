package org.fundacionparaguaya.assistantadvisor;

import android.support.multidex.MultiDexApplication;
import com.evernote.android.job.JobManager;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.novoda.merlin.Merlin;

import org.fundacionparaguaya.assistantadvisor.BuildConfig;
import org.fundacionparaguaya.adviserplatform.data.remote.ConnectivityWatcher;
import org.fundacionparaguaya.adviserplatform.injection.ApplicationComponent;
import org.fundacionparaguaya.adviserplatform.injection.ApplicationModule;
import org.fundacionparaguaya.adviserplatform.injection.DaggerApplicationComponent;
import org.fundacionparaguaya.adviserplatform.injection.DatabaseModule;
import org.fundacionparaguaya.adviserplatform.jobs.JobCreator;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;
import org.fundacionparaguaya.adviserplatform.util.ReleaseLoggingTree;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * The advisor application.
 */

public class AdviserAssistantApplication extends MultiDexApplication {

    private static final long INDICATOR_CACHE_SIZE = 500 * ByteConstants.MB;
    private static final long MIN_INDICATOR_CACHE_SIZE = 70 * ByteConstants.MB;

    private ApplicationComponent applicationComponent;

    @Inject
    Merlin mMerlin;

    @Inject
    ConnectivityWatcher mConnectivityWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        DiskCacheConfig indicatorCacheConfig = DiskCacheConfig
                .newBuilder(this)
                .setMaxCacheSize(INDICATOR_CACHE_SIZE)
                .setMaxCacheSizeOnLowDiskSpace(INDICATOR_CACHE_SIZE)
                .setMaxCacheSizeOnVeryLowDiskSpace(MIN_INDICATOR_CACHE_SIZE)
                .build();

        ImagePipelineConfig config = ImagePipelineConfig
                .newBuilder(this)
                .setSmallImageDiskCacheConfig(indicatorCacheConfig)
                .build();

        Fresco
                .initialize(this, config);

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .build();

        applicationComponent.inject(this);

        mMerlin.bind();

        Instabug.Builder instabugBuilder = new Instabug.Builder(this, BuildConfig.INSTABUG_API_KEY_STRING)
                .setInvocationEvent(InstabugInvocationEvent.SHAKE);

        if(BuildConfig.DEBUG)
        {
            Timber.plant(new Timber.DebugTree());
            instabugBuilder.setCrashReportingState(Feature.State.DISABLED); //disable automatic crash reporting in debug
        }
        else
        {
            Timber.plant(new ReleaseLoggingTree(this));
            MixpanelHelper.identify(getApplicationContext());

        }

        instabugBuilder.build();
        JobManager.create(this).addJobCreator(new JobCreator(this));
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        mMerlin.unbind();
    }

}
