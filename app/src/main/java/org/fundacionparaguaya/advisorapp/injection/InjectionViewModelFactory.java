package org.fundacionparaguaya.advisorapp.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.remote.ServerManager;
import org.fundacionparaguaya.advisorapp.data.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.data.repositories.SnapshotRepository;
import org.fundacionparaguaya.advisorapp.data.repositories.SurveyRepository;
import org.fundacionparaguaya.advisorapp.ui.families.AllFamiliesViewModel;
import org.fundacionparaguaya.advisorapp.ui.families.detail.FamilyDetailViewModel;
import org.fundacionparaguaya.advisorapp.ui.survey.SharedSurveyViewModel;
import org.fundacionparaguaya.advisorapp.ui.survey.priorities.EditPriorityViewModel;
import org.fundacionparaguaya.advisorapp.ui.survey.resume.PendingSnapshotViewModel;
import org.fundacionparaguaya.advisorapp.ui.login.LoginViewModel;
import org.fundacionparaguaya.advisorapp.ui.settings.SettingsViewModel;

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
