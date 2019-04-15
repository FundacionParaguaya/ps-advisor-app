package org.fundacionparaguaya.adviserplatform.ui.dashboard;

import android.arch.lifecycle.ViewModel;
import org.fundacionparaguaya.adviserplatform.data.repositories.SnapshotRepository;

public class DashActivityViewModel extends ViewModel {
    private SnapshotRepository snapshotRepository;

    public DashActivityViewModel(SnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    public SnapshotRepository getSnapshotRepository() {
        return snapshotRepository;
    }
}