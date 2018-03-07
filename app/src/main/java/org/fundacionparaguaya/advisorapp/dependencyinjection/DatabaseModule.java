package org.fundacionparaguaya.advisorapp.dependencyinjection;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;

import org.fundacionparaguaya.advisorapp.data.local.FamilyDao;
import org.fundacionparaguaya.advisorapp.data.local.LocalDatabase;
import org.fundacionparaguaya.advisorapp.data.local.SnapshotDao;
import org.fundacionparaguaya.advisorapp.data.local.SurveyDao;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationInterceptor;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationService;
import org.fundacionparaguaya.advisorapp.data.remote.ConnectivityWatcher;
import org.fundacionparaguaya.advisorapp.data.remote.FamilyService;
import org.fundacionparaguaya.advisorapp.data.remote.RemoteDatabase;
import org.fundacionparaguaya.advisorapp.data.remote.ServerInterceptor;
import org.fundacionparaguaya.advisorapp.data.remote.ServerManager;
import org.fundacionparaguaya.advisorapp.data.remote.SnapshotService;
import org.fundacionparaguaya.advisorapp.data.remote.SurveyService;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SnapshotRepository;
import org.fundacionparaguaya.advisorapp.repositories.SurveyRepository;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The module responsible for creating and satisfying dependencies relating to the local and remote
 * databases.
 */

@Module
public class DatabaseModule {
    private static final String URL_API = "http://povertystoplightiqp.org:8080/";
    private static final String URL_API_ENDPOINT = URL_API + "api/v1/";
    private static final String URL_AUTH_ENDPOINT = URL_API + "oauth/";

    private final LocalDatabase local;

    public DatabaseModule(Application application) {
        this.local = Room.databaseBuilder(
                application,
                LocalDatabase.class,
                "Advisor.db"
        )
                .addMigrations(LocalDatabase.MIGRATIONS)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    Merlin provideMerlin(Application application) {
        return new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks()
                .build(application);
    }

    @Provides
    @Singleton
    MerlinsBeard provideMerlinsBeard(Application application) {
        return MerlinsBeard.from(application);
    }

    @Provides
    @Singleton
    ServerManager provideServerManager(Application application,
                                       SharedPreferences sharedPreferences) {
        return new ServerManager(application, sharedPreferences);
    }

    @Provides
    @Singleton
    ServerInterceptor provideServerInterceptor(ServerManager serverManager) {
        return new ServerInterceptor(serverManager);
    }

    @Provides
    @Singleton
    ConnectivityWatcher provideConnectivityWatcher(Merlin merlin, MerlinsBeard merlinsBeard) {
        return new ConnectivityWatcher(merlin, merlinsBeard);
    }

    @Provides
    @Singleton
    AuthenticationManager provideAuthManager(SharedPreferences sharedPreferences,
                                             ServerInterceptor serverInterceptor,
                                             ConnectivityWatcher connectivityWatcher) {
        Retrofit authRetrofit = new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .addInterceptor(serverInterceptor)
                        .build())
                .baseUrl(URL_AUTH_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return new AuthenticationManager(authRetrofit.create(AuthenticationService.class),
                sharedPreferences, connectivityWatcher);
    }

    @Provides
    @Singleton
    AuthenticationInterceptor provideAuthInterceptor(AuthenticationManager authManager) {
        return new AuthenticationInterceptor(authManager);
    }

    @Provides
    @Singleton
    OkHttpClient provideHttpClient(AuthenticationInterceptor authInterceptor,
                                   ServerInterceptor serverInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(serverInterceptor)
                .addInterceptor(authInterceptor)
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(URL_API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    RemoteDatabase provideRemoteDatabase(Retrofit retrofit) {
        return new RemoteDatabase(retrofit);
    }

    @Provides
    @Singleton
    LocalDatabase provideLocalDatabase() {
        return this.local;
    }

    @Provides
    @Singleton
    FamilyRepository provideFamilyRepository(FamilyDao familyDao, FamilyService familyService) {
        return new FamilyRepository(familyDao, familyService);
    }

    @Provides
    @Singleton
    SurveyRepository provideSurveyRepository(SurveyDao surveyDao, SurveyService surveyService) {
        return new SurveyRepository(surveyDao, surveyService);
    }

    @Provides
    @Singleton
    SnapshotRepository provideSnapshotRepository(SnapshotDao snapshotDao,
                                                 SnapshotService snapshotService,
                                                 FamilyRepository familyRepository,
                                                 SurveyRepository surveyRepository) {
        return new SnapshotRepository(snapshotDao, snapshotService, familyRepository, surveyRepository);
    }

    @Provides
    @Singleton
    FamilyDao provideFamilyDao(LocalDatabase local) {
        return local.familyDao();
    }

    @Provides
    @Singleton
    FamilyService provideFamilyService(RemoteDatabase remote) {
        return remote.familyService();
    }

    @Provides
    @Singleton
    SurveyDao provideSurveyDao(LocalDatabase local) {
        return local.surveyDao();
    }

    @Provides
    @Singleton
    SurveyService provideSurveyService(RemoteDatabase remote) {
        return remote.surveyService();
    }

    @Provides
    @Singleton
    SnapshotDao provideSnapshotDao(LocalDatabase local) {
        return local.snapshotDao();
    }

    @Provides
    @Singleton
    SnapshotService provideSnapshotService(RemoteDatabase remote) {
        return remote.snapshotService();
    }

    @Provides
    @Singleton
    InjectionViewModelFactory provideInjectionViewModelFactory(ServerManager serverManager,
                                                               AuthenticationManager authManager,
                                                               FamilyRepository familyRepository,
                                                               SurveyRepository surveyRepository,
                                                               SnapshotRepository snapshotRepository) {
        return new InjectionViewModelFactory(
                serverManager, authManager, familyRepository, surveyRepository, snapshotRepository);
    }
}
