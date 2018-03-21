package org.fundacionparaguaya.advisorapp.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import org.fundacionparaguaya.advisorapp.models.*;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

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

        for(Survey survey: mSurveyRepository.getSurveysNow())
        {
            for(IndicatorQuestion indicatorQuestion: survey.getIndicatorQuestions())
            {
                for(IndicatorOption option: indicatorQuestion.getOptions())
                {
                    Uri imageUri = Uri.parse(option.getImageUrl());

                    if(!option.getImageUrl().equals(NO_IMAGE) && !Fresco.getImagePipeline().isInDiskCacheSync(imageUri)) {
                        ImageRequest imageRequest = ImageRequestBuilder
                                .newBuilderWithSource(Uri.parse(option.getImageUrl()))
                                .setCacheChoice(ImageRequest.CacheChoice.SMALL) // cache choice = small just allows us
                                .build();                                       //to isolate survey pictures in their own cache

                        Log.d(TAG, "Downloading Picture: " + option.getImageUrl());

                        DataSource<Void> prefetchDataSource = Fresco.getImagePipeline().prefetchToDiskCache(imageRequest, CallerThreadExecutor.getInstance());

                        try {
                            DataSources.waitForFinalResult(prefetchDataSource);
                        } catch (Throwable throwable) {
                            result = false; //error downloading
                        }
                    }
                }
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
