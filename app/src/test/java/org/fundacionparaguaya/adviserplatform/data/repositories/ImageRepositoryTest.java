package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.net.Uri;
import android.test.suitebuilder.annotation.SmallTest;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.fundacionparaguaya.adviserplatform.data.model.ModelUtils.surveyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the <code>ImageRepository</code>.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataSources.class, Fresco.class, Uri.class})
@SmallTest
public class ImageRepositoryTest {

    @Mock
    ImagePipeline imagePipeline;

    @Mock
    DataSource<Void> mPrefetchImageSource;

    @Mock
    SurveyRepository mSurveyRepository;

    @Mock
    FamilyRepository mFamilyRepository;

    @Mock
    Uri uri;

    /**
     * Verifies that images not currently in the cache are downloaded when the image repository is synced.
     * @throws Throwable
     */
    @Test
    public void ShouldSyncSurveyPhotos() throws Throwable {
        setUp();
        when(imagePipeline.isInDiskCacheSync(any(Uri.class))).thenReturn(false);

        List<Survey> surveys = surveyList();

        when(mSurveyRepository.getSurveysNow()).thenReturn(surveys);

        imageRepository().sync(null);

        int numOfPictures = 0;

        for(Survey s: surveys) {
            numOfPictures += s.getIndicatorQuestions().size() * 3;
        }

        verify(imagePipeline, times(numOfPictures)).prefetchToDiskCache(any(ImageRequest.class), any());
    }

    /**
     * Verifies that images in the disk cache are not redownloaded
     */
    @Test
    public void ShouldNotResyncSurveyPhotos() throws Throwable {

        setUp();

        when(imagePipeline.isInDiskCacheSync(any(Uri.class))).thenReturn(true);

        List<Survey> surveys = surveyList();

        when(mSurveyRepository.getSurveysNow()).thenReturn(surveys);
        imageRepository().sync(null);
        verify(imagePipeline, never()).prefetchToDiskCache(any(), any());
    }

    private void setUp() throws Throwable {
        PowerMockito.mockStatic(DataSources.class);
        PowerMockito.mockStatic(Fresco.class);
        PowerMockito.mockStatic(Uri.class);

        when((Uri.parse(anyString()))).thenReturn(uri);
        when(DataSources.waitForFinalResult(mPrefetchImageSource)).thenReturn(null);
        when(Fresco.getImagePipeline()).thenReturn(imagePipeline);

        when(imagePipeline.prefetchToDiskCache(any(ImageRequest.class), any())).thenReturn(mPrefetchImageSource);
    }

    public ImageRepository imageRepository()
    {
        return new ImageRepository(mFamilyRepository, mSurveyRepository);
    }
}