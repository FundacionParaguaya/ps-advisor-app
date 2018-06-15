package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.net.Uri;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorQuestion;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;

import timber.log.Timber;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The utility for the storage of snapshots.
 */

public class ImageRepository extends BaseRepository {
    private static final String TAG = "ImageRepository";

    private static final String NO_IMAGE= "NONE";

    private final FamilyRepository mFamilyRepository;
    private final SurveyRepository mSurveyRepository;

    @Inject
    public ImageRepository(FamilyRepository familyRepository,
                           SurveyRepository surveyRepository) {
        this.mFamilyRepository = familyRepository;
        this.mSurveyRepository = surveyRepository;
        setPreferenceKey(String.format("%s-%s", AppConstants.KEY_LAST_SYNC_TIME, TAG));
    }

    /**
     * Synchronizes the local snapshots with the remote database.
     * @return Whether the sync was successful.
     */
    public boolean sync(@Nullable Date lastSync) {
        long loopCount = 0;
        //TODO Sodep: How many times does images really change to make this sync as frequent as others?
        boolean result = true;

        List<Uri> imagesDownloaded = new ArrayList<>();

        //todo: add timeout once it is added to fresco https://github.com/facebook/fresco/pull/2068
        final List<Survey> surveysNow = mSurveyRepository.getSurveysNow();
        for(Survey survey: surveysNow)
        {
            addRecordsCount(survey.getIndicatorQuestions().size()
                    * AppConstants.TOTAL_TYPES_OF_INDICATORS);
            for(IndicatorQuestion indicatorQuestion: survey.getIndicatorQuestions())
            {
                for(IndicatorOption option: indicatorQuestion.getOptions())
                {
                    //TODO Sodep: Time complexity: n^3
                    if(shouldAbortSync()) return false;

                    if (!option.getImageUrl().contains(NO_IMAGE)) {
                        Uri uri = Uri.parse(option.getImageUrl());
                        imagesDownloaded.add(uri);
                        result &= downloadImage(uri);
                        getDashActivity().setSyncLabel(R.string.syncing_images, ++loopCount,
                                getRecordsCount());

                    }
                }
            }
        }

        clearSyncStatus();

        result &= verifyCacheResults(imagesDownloaded);

        return result;
    }

    private void addRecordsCount(int i) {
        setRecordsCount(getRecordsCount() + i);
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
            Timber.tag(TAG);
            Timber.e( "ERROR: " + notSaved + " out of " + uris.size() + " pictures not saved to cache.");
        }
        else
        {
            Timber.tag(TAG);
            Timber.d( "Successfully synced indicator images: " + uris.size() + " pictures saved to cache.");
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

                if(prefetchDataSource.isFinished()) Timber.d("Downloaded Picture: " + imageUri.toString());

            } catch (Throwable throwable) {
                result = false; //error downloading
                Timber.tag(TAG);
                Timber.d(TAG, "Downloaded Failed: " + imageUri.toString());
            }
        }

        return result;
    }

    public void clean() {
        Fresco.getImagePipeline().clearCaches();
        Fresco.getImagePipeline().clearDiskCaches();
        setRecordsCount(0);
    }
}
