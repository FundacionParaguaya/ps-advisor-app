package org.fundacionparaguaya.advisorapp;

import android.support.multidex.MultiDexApplication;
import com.evernote.android.job.JobManager;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.novoda.merlin.Merlin;
import org.fundacionparaguaya.advisorapp.data.remote.ConnectivityWatcher;
import org.fundacionparaguaya.advisorapp.injection.ApplicationComponent;
import org.fundacionparaguaya.advisorapp.injection.ApplicationModule;
import org.fundacionparaguaya.advisorapp.injection.DaggerApplicationComponent;
import org.fundacionparaguaya.advisorapp.injection.DatabaseModule;
import org.fundacionparaguaya.advisorapp.jobs.JobCreator;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;

import javax.inject.Inject;

/**
 * The advisor application.
 */

public class AdvisorApplication extends MultiDexApplication {

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

        new Instabug.Builder(this, BuildConfig.INSTABUG_API_KEY_STRING)
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .build();

        MixpanelHelper.identify(getApplicationContext());

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
