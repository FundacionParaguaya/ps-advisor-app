package org.fundacionparaguaya.adviserplatform.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.remote.ServerManager;
import org.fundacionparaguaya.adviserplatform.data.repositories.FamilyRepository;
import org.fundacionparaguaya.adviserplatform.data.repositories.SnapshotRepository;
import org.fundacionparaguaya.adviserplatform.data.repositories.SurveyRepository;
import org.fundacionparaguaya.adviserplatform.ui.families.AllFamiliesViewModel;
import org.fundacionparaguaya.adviserplatform.ui.families.detail.FamilyDetailViewModel;
import org.fundacionparaguaya.adviserplatform.ui.survey.SharedSurveyViewModel;
import org.fundacionparaguaya.adviserplatform.ui.survey.priorities.EditPriorityViewModel;
import org.fundacionparaguaya.adviserplatform.ui.survey.resume.PendingSnapshotViewModel;
import org.fundacionparaguaya.adviserplatform.ui.login.LoginViewModel;
import org.fundacionparaguaya.adviserplatform.ui.settings.SettingsViewModel;

/**
 * A custom view model factory which will inject view models with the correct dependencies.
 */

public class InjectionViewModelFactory implements ViewModelProvider.Factory {
    private final ServerManager serverManager;
    private final AuthenticationManager authManager;
    private final FamilyRepository familyRepository;
    private final SurveyRepository surveyRepository;
    private final SnapshotRepository snapshotRepository;

    public InjectionViewModelFactory(ServerManager serverManager,
                                     AuthenticationManager authManager,
                                     FamilyRepository familyRepository,
                                     SurveyRepository surveyRepository,
                                     SnapshotRepository snapshotRepository) {
        this.serverManager = serverManager;
        this.authManager = authManager;
        this.familyRepository = familyRepository;
        this.surveyRepository = surveyRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AllFamiliesViewModel.class))
            return (T) new AllFamiliesViewModel(familyRepository);
        else if (modelClass.isAssignableFrom(LoginViewModel.class))
            return (T) new LoginViewModel(serverManager, authManager);
        else if (modelClass.isAssignableFrom(FamilyDetailViewModel.class))
        {
            return (T) new FamilyDetailViewModel(familyRepository, snapshotRepository);
        }
        else if (modelClass.isAssignableFrom(SharedSurveyViewModel.class)) {
            return (T) new SharedSurveyViewModel(snapshotRepository, surveyRepository, familyRepository);
        }
        else if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(authManager);
        } else if (modelClass.isAssignableFrom(PendingSnapshotViewModel.class)) {
            return (T) new PendingSnapshotViewModel(surveyRepository, familyRepository);
        } else if(modelClass.isAssignableFrom(EditPriorityViewModel.class)){
            return (T) new EditPriorityViewModel(surveyRepository);
        }
	else
            throw new IllegalArgumentException("The view model was not found for " + modelClass.toString());
    }
}
