package org.fundacionparaguaya.advisorapp.data.repositories;

import android.net.Uri;
import android.support.test.espresso.core.internal.deps.guava.cache.CacheLoader;
import android.util.Log;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.instabug.library.Instabug;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorOption;
import org.fundacionparaguaya.advisorapp.data.model.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.data.model.Survey;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The utility for the storage of snapshots.
 */

public class ImageRepository {
    private static final String TAG = "ImageRepository";

    private static final String NO_IMAGE= "NONE";

    private final FamilyRepository mFamilyRepository;
    private final SurveyRepository mSurveyRepository;

    @Inject
    public ImageRepository(FamilyRepository familyRepository,
                           SurveyRepository surveyRepository) {
        this.mFamilyRepository = familyRepository;
        this.mSurveyRepository = surveyRepository;
    }

    /**
     * Synchronizes the local snapshots with the remote database.
     * @return Whether the sync was successful.
     */
    boolean sync() {

        boolean result = true;

        List<Uri> imagesDownloaded = new ArrayList<>();

        //todo: add timeout once it is added to fresco https://github.com/facebook/fresco/pull/2068
        for(Survey survey: mSurveyRepository.getSurveysNow())
        {
            for(IndicatorQuestion indicatorQuestion: survey.getIndicatorQuestions())
            {
                for(IndicatorOption option: indicatorQuestion.getOptions())
                {
                    if(!option.getImageUrl().contains(NO_IMAGE)) {
                        Uri uri = Uri.parse(option.getImageUrl());
                        imagesDownloaded.add(uri);
                        result &= downloadImage(uri);
                    }
                }
            }
        }

        result &= verifyCacheResults(imagesDownloaded);

        return result;
    }

    /**
     * @param uris of images downloaded during sync
     * @return true if all downloaded images still exist in cache
     */
    private boolean verifyCacheResults(List<Uri> uris)
    {
        int notSaved = 0;

        for(Uri uri: uris)
        {
            if(!Fresco.getImagePipeline().isInDiskCacheSync(uri)) notSaved++;
        }

        if(notSaved>0)
        {
            Log.e(TAG, "ERROR: " + notSaved + " out of " + uris.size() + " pictures not saved to cache.");
            //MixpanelHelper.BugEvents.imagesMissedCache(get, notSaved);
        }
        else
        {
            Log.d(TAG, "Successfully synced indicator images: " + uris.size() + " pictures saved to cache.");
        }

        return notSaved == 0;
    }

    private boolean downloadImage(Uri imageUri)
    {
        boolean result = true;

        if(!Fresco.getImagePipeline().isInDiskCacheSync(imageUri)) {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(imageUri)
                    .setCacheChoice(ImageRequest.CacheChoice.SMALL) // cache choice = small just allows us
                    .build();                                       //to isolate survey pictures in their own cache

            DataSource<Void> prefetchDataSource = Fresco.getImagePipeline().prefetchToDiskCache(imageRequest, CallerThreadExecutor.getInstance());

            try {
                DataSources.waitForFinalResult(prefetchDataSource);

                if(prefetchDataSource.isFinished()) Log.d(TAG, "Downloaded Picture: " + imageUri.toString());

            } catch (Throwable throwable) {
                result = false; //error downloading
                Log.d(TAG, "Downloaded Failed: " + imageUri.toString());
            }
        }

        return result;
    }

    /**
     * Clears all image caches
     */
    void clean() {
        Fresco.getImagePipeline().clearCaches();
    }
}
