package org.fundacionparaguaya.advisorapp.repositories;

import android.net.Uri;
import android.util.Log;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.models.Survey;

import javax.inject.Inject;

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

        //todo: add timeout once it is added to fresco https://github.com/facebook/fresco/pull/2068
        for(Survey survey: mSurveyRepository.getSurveysNow())
        {
            for(IndicatorQuestion indicatorQuestion: survey.getIndicatorQuestions())
            {
                for(IndicatorOption option: indicatorQuestion.getOptions())
                {
                    result &= downloadImage(Uri.parse(option.getImageUrl()));
                }
            }
        }

        return result;
    }

    private boolean downloadImage(Uri imageUri)
    {
        boolean result = true;

        if(!imageUri.toString().contains(NO_IMAGE) && !Fresco.getImagePipeline().isInDiskCacheSync(imageUri)) {
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
