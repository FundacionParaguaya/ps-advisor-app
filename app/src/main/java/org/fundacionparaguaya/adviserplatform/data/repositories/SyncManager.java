package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.remote.ConnectivityWatcher;
import org.fundacionparaguaya.adviserplatform.jobs.SyncJob;
import org.fundacionparaguaya.adviserplatform.ui.dashboard.DashActivity;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.fundacionparaguaya.adviserplatform.util.Utilities;
import org.perf4j.StopWatch;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.HttpException;
import timber.log.Timber;

import static org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager.SyncState.*;
import static org.fundacionparaguaya.adviserplatform.util.AppConstants.KEY_LAST_SYNC_TIME;


/**
 * A utility that manages the synchronization of the local databases.
 */

@Singleton
public class SyncManager implements AuthenticationManager.AuthStateChangeHandler {
    public static final String TAG = "SyncManager";
    static final long LAST_SYNC_ERROR_MARGIN // a margin which will always be resynced to
            = TimeUnit.DAYS.toMillis(1); // avoid problems where models are never synced
    private final AuthenticationManager mAuthenticationManager;
    private final String unknownError = "Unknown error syncing data";

    private FamilyRepository mFamilyRepository;
    private SurveyRepository mSurveyRepository;
    private SnapshotRepository mSnapshotRepository;
    private ImageRepository mImageRepository;

    private SharedPreferences mPreferences;
    private boolean isOnline;

    private MutableLiveData<SyncProgress> mProgress;
    private DashActivity mDashActivity;

    @Inject
    public SyncManager(FamilyRepository familyRepository,
                       SurveyRepository surveyRepository,
                       SnapshotRepository snapshotRepository,
                       ImageRepository imageRepository,
                       SharedPreferences preferences,
                       ConnectivityWatcher connectivityWatcher,
                       AuthenticationManager authenticationManager) {

        mFamilyRepository = familyRepository;
        mSurveyRepository = surveyRepository;
        mSnapshotRepository = snapshotRepository;
        mImageRepository = imageRepository;
        mAuthenticationManager = authenticationManager;

        mProgress = new MutableLiveData<>();

        mPreferences = preferences;
        long lastSyncTime = mPreferences.getLong(KEY_LAST_SYNC_TIME, -1);
        mProgress.setValue(new SyncProgress(lastSyncTime != -1 ? SYNCED : NEVER, lastSyncTime));

        connectivityWatcher.status().observeForever(this::setIsOnline);
        for(BaseRepository repo: getBaseRepositories()) {
            repo.setSharedPreferences(preferences);
        }
    }

    public LiveData<SyncProgress> getProgress() {
        return mProgress;
    }

    private void setIsOnline(boolean online) {
        if (online) {
            long lastSyncTime = mProgress.getValue().getLastSyncedTime();
            isOnline = true;
            updateProgress(lastSyncTime != -1 ? SYNCED : NEVER);
        } else {
            isOnline = false;
            updateProgress(ERROR_NO_INTERNET);
        }
    }

    /**
     * Synchronizes the local database with the remote one.
     * @return Whether the sync was successful.
     */
    public boolean sync(AtomicBoolean isAlive) {
        StopWatch watch = new StopWatch("SyncManager:sync");
        long lastSyncDate = -1;

        watch.start();
        if (!isOnline) return false;

        Log.d(TAG, "sync: Synchronizing the database...");
        updateProgress(SYNCING);
        boolean result = true;

        //TODO Sodep: We need to save sync status for each repository individually
        //TODO Sodep: _result_ as a single boolean is not enough
        BaseRepository[] repositoriesToSync = getBaseRepositories();

        for(BaseRepository repo: repositoriesToSync)
        {
            int i = 0;
            if(!isAlive.get()) {
                return false;
            }
            repo.setDashActivity(getDashActivity());
            try {
                //TODO Sodep: bring data from server: data -> BD, images -> cache (fresco)
                //Ensure a valid session is active
                if(mAuthenticationManager.isTokenExpired(mPreferences)) {
                    AuthenticationManager.AuthenticationStatus status =
                            mAuthenticationManager.refreshLogin();
                    if(!AuthenticationManager.AuthenticationStatus.AUTHENTICATED.equals(status)){
                        fallBackToLogin();
                    }
                }
                if(repo.needsSync()) {
                    result = resyncWithOneAuthentication(isAlive, result, repo);
                    Log.d(TAG, watch.lap(String.format(" Synced: %s",
                            repo.getClass().getSimpleName())));
                    if(result) {
                        updateProgress(SYNCED, new Date().getTime());
                        repo.updateSyncDate();
                        lastSyncDate = repo.getLastSyncDate();
                    } else {
                        Log.d(TAG, String.format("Problem syncing repo %s",
                                repo.getClass().getName()));
                        repo.clearSyncDate();
                        lastSyncDate = -1;
                    }
                } else {
                    lastSyncDate = repo.getLastSyncDate();
                    updateProgress(SYNCED, lastSyncDate);
                    setLastSyncDate(lastSyncDate);
                    result = true;
                    Log.d(TAG, String.format("Not syncing. Waiting for %s seconds passed %s",
                            SyncJob.SYNC_INTERVAL_MS, new Date(repo.getLastSyncDate())));
                }
            } catch (HttpException e) {
                if(!result) {
                    Timber.e(TAG, unknownError, e);
                    stopSyncProcess();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "sync: Error while syncing!", e);
                result = false;
                Log.d(TAG,watch.lap(String.format("Error syncing: %s", e.getMessage())));
            }
        }
        Log.d(TAG,watch.stop("Sync completed"));
        if (result) {
            updateProgress(SYNCED, lastSyncDate);
        }
        else {
            updateProgress(ERROR_OTHER);
        }

        Log.d(TAG, String.format("sync: Finished the synchronization %s.",
                result ? "successfully" : "with errors"));

        return result;
    }

    //TODO Sodep: This should be tied to a Interceptor, that handles re-authentication transparently
    private boolean resyncWithOneAuthentication(AtomicBoolean isAlive, boolean result,
                                                BaseRepository repo) {
        try {
            updateProgress(SYNCING);
            repo.setDashActivity(getDashActivity());
            result &= repo.sync(isAlive);
        } catch (HttpException httpException) {
            if(AppConstants.HTTP_SC_UNAUTHORIZED == httpException.code()) {
                Timber.d(TAG, String.format("HTTP UNAUTHORIZED %s. " +
                        "Trying to refresh token one time", httpException));
                mAuthenticationManager.refreshLogin();
                updateProgress(SYNCING);
                result &= repo.sync(isAlive);
            } else if(AppConstants.HTTP_SC_BAD_REQUEST == httpException.code()) {
                updateProgress(ERROR_OTHER);
                Timber.e(TAG, String.format("BAD_REQUEST HTTP error %s", httpException));
            } else {
                Toast.makeText(getDashActivity(), unknownError,
                        Toast.LENGTH_LONG);
                Timber.d(TAG, String.format("HTTP error %s", httpException));
                fallBackToLogin();
            }
        } catch (NullPointerException nullPE) {
            Log.e(TAG, String.format("Exception syncing: %s", repo), nullPE);
        }
        return result;
    }

    private void setLastSyncDate(long lastSyncDate) {
        if(getDashActivity() != null) {
            getDashActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgress.setValue(new SyncProgress(lastSyncDate != -1
                            ? SYNCED : NEVER, lastSyncDate));
                }
            });
        }
    }

    @NonNull
    private BaseRepository[] getBaseRepositories() {
        return new BaseRepository[]{mFamilyRepository, mSurveyRepository,
                    mSnapshotRepository, mImageRepository};
    }

    private void fallBackToLogin() {
        mAuthenticationManager.logout();
        getDashActivity().showLogin();
    }

    private boolean needsResync(Date lastSync) {
        return Utilities.hasBeenXSeconds(lastSync, SyncJob.SYNC_INTERVAL_MS);
    }


    /**
      * Cleans all of the repositories, removing all entries. Useful for when a user logs out.
      */
    public void clean() {
        Log.d(TAG, "clean: Cleaning the database...");
        BaseRepository[] repositoriesToSync = getBaseRepositories();
        updateProgress(SYNCING);
        for(BaseRepository repo: repositoriesToSync) {
            repo.clean();
            clearSyncDates(repo);
        }
        updateProgress(NEVER, -1);
        Log.d(TAG, "clean: Finished the database clean");
    }

    private void clearSyncDates(BaseRepository repo) {
        repo.clearSyncDate();
        mPreferences.edit().remove(KEY_LAST_SYNC_TIME);
        mPreferences.edit().commit();
    }

    public CleanTask makeCleanTask()
    {
        return new CleanTask(this);
    }

    private void updateProgress(SyncState state) {
        SyncProgress progress = mProgress.getValue();
        if (progress == null) {
            progress = new SyncProgress(state);
        } else {
            progress = new SyncProgress(state, progress.getLastSyncedTime());
        }
        mProgress.postValue(progress);
    }

    private void updateProgress(SyncState state, long lastSyncTime) {
        SharedPreferences.Editor editor = mPreferences.edit();
        long lastStoredSyncTime = mPreferences.getLong(KEY_LAST_SYNC_TIME, -1);
        long lastTimestampForLabel = lastSyncTime;
        if(lastStoredSyncTime > lastSyncTime) {
            lastTimestampForLabel = lastStoredSyncTime;
        }
        mProgress.postValue(new SyncProgress(state, lastTimestampForLabel));

        if (lastSyncTime > -1L) {
            editor.putLong(KEY_LAST_SYNC_TIME, lastTimestampForLabel);
        } else {
            editor.remove(KEY_LAST_SYNC_TIME);
        }
        editor.apply();
    }

    @Override
    public void onAuthStateChange(AuthenticationManager.AuthenticationStatus status) {
        switch (status)
        {
            case UNAUTHENTICATED:

                //TODO Sodep: Cleaning always is too agressive. To review - 2018-06-15
                if(StringUtils.isBlank(mPreferences.getString(AppConstants.KEY_USERNAME,
                        null))) {
                    makeCleanTask().execute();
                }
                SyncJob.cancelAll();
                break;

            case AUTHENTICATED:
                SyncJob.sync();
                break;

            default:
                //not relevant to sync manager
        }
    }

    /**
     * The states that a sync can be in.
     */
    public enum SyncState {
        NEVER,
        SYNCING,
        SYNCED,
        ERROR_NO_INTERNET,
        ERROR_OTHER
    }

    /**
     * The progress of a sync.
     */
    public class SyncProgress {
        private SyncState mState;
        private long mLastSyncedTime;

        SyncProgress(SyncState syncState) {
            this(syncState, -1);
        }

        SyncProgress(SyncState syncState, long lastSyncedTime) {
            mState = syncState;
            mLastSyncedTime = lastSyncedTime;
        }

        public SyncState getSyncState() {
            return mState;
        }

        public long getLastSyncedTime() {
            return mLastSyncedTime;
        }
    }

    public static class CleanTask extends AsyncTask<Void, Void, Void>
    {
        private WeakReference<SyncManager> mSyncManager;

        CleanTask(SyncManager manager)
        {
            mSyncManager = new WeakReference<>(manager);
        }
        @Override
        protected Void doInBackground(Void... voids) {

            if(mSyncManager.get()!=null)
            {
                mSyncManager.get().clean();
            }

            return null;
        }
    }

    public DashActivity getDashActivity() {
        return mDashActivity;
    }

    public void setDashActivity(DashActivity mDashActivity) {
        this.mDashActivity = mDashActivity;
    }

    public void stopSyncProcess() {
        Log.d(TAG, "Stopping ongoing sync process...");
        BaseRepository[] repositoriesToSync = getBaseRepositories();
        updateProgress(NEVER, -1);
        for(BaseRepository repo: repositoriesToSync) {
            repo.setmIsAlive(new AtomicBoolean(false));
        }
    }
}
