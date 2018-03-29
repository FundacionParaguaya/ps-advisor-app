package org.fundacionparaguaya.advisorapp.data.repositories;

import android.test.suitebuilder.annotation.SmallTest;

import org.fundacionparaguaya.advisorapp.data.local.SnapshotDao;
import org.fundacionparaguaya.advisorapp.data.remote.SnapshotService;
import org.fundacionparaguaya.advisorapp.data.model.Snapshot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.fundacionparaguaya.advisorapp.data.model.ModelUtils.snapshot;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the <code>SnapshotRepository</code>.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class SnapshotRepositoryTest {
    @Mock
    SnapshotDao snapshotDao;
    @Mock
    SnapshotService snapshotService;
    @Mock
    SurveyRepository surveyRepository;
    @Mock
    FamilyRepository familyRepository;

    @Test
    public void pending_ShouldDiscardExistingPending_new() {
        when(snapshotDao.queryInProgressSnapshotForFamilyNow(1)).thenReturn(null);
        when(snapshotDao.updateSnapshot(any())).thenReturn(0);

        SnapshotRepository snapshotRepository = snapshotRepository();
        Snapshot pending = pendingSnapshot();
        snapshotRepository.saveSnapshot(pending);

        verify(snapshotDao, never()).deleteInProgressSnapshot(anyInt());
        verify(snapshotDao).insertSnapshot(pending);
    }

    @Test
    public void pending_ShouldDiscardExistingPending_update() {
        Snapshot oldPending = pendingSnapshot();
        when(snapshotDao.queryInProgressSnapshotForFamilyNow(1)).thenReturn(oldPending);
        when(snapshotDao.updateSnapshot(any())).thenReturn(1);

        SnapshotRepository snapshotRepository = snapshotRepository();
        Snapshot newPending = pendingSnapshot();
        newPending.setRemoteId(1L);
        snapshotRepository.saveSnapshot(newPending);

        verify(snapshotDao, never()).deleteInProgressSnapshot(1);
        verify(snapshotDao, never()).insertSnapshot(newPending);
        verify(snapshotDao).updateSnapshot(newPending);
    }

    @Test
    public void pending_ShouldDiscardExistingPending_replace() {
        Snapshot oldPending = pendingSnapshot();
        when(snapshotDao.queryInProgressSnapshotForFamilyNow(1)).thenReturn(oldPending);

        SnapshotRepository snapshotRepository = snapshotRepository();
        Snapshot newPending = pendingSnapshot();
        newPending.setId(2);
        snapshotRepository.saveSnapshot(newPending);

        verify(snapshotDao).deleteInProgressSnapshot(1);
        verify(snapshotDao).insertSnapshot(newPending);
    }

    private Snapshot pendingSnapshot() {
        Snapshot snapshot = snapshot();
        snapshot.setInProgress(true);
        return snapshot;
    }

    private SnapshotRepository snapshotRepository() {
        return new SnapshotRepository(snapshotDao, snapshotService, familyRepository, surveyRepository);
    }
}