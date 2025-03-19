package org.jabref.gui.git;

import java.io.IOException;

import javax.swing.JLabel;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class GitSyncStatusPanelTest {
    private GitSyncStatusPanel syncStatusPanel;

    @BeforeEach
    void setUp() {
        syncStatusPanel = new GitSyncStatusPanel();
    }

    @Test
    void testNoGitDirectory() {
        try (MockedStatic<System> systemMock = mockStatic(System.class)) {
            systemMock.when(() -> System.getProperty("user.dir")).thenReturn("/invalid/path");

            syncStatusPanel.updateSyncStatus();
            JLabel label = (JLabel) syncStatusPanel.getComponent(0);
            assertEquals("❌ No .git directory found", label.getText());
        }
    }

    @Test
    void testSyncStatusSynchronized() throws IOException, GitAPIException {
        Git gitMock = mock(Git.class);
        Repository repoMock = mock(Repository.class);
        ObjectId commitIdMock = mock(ObjectId.class);

        when(repoMock.getBranch()).thenReturn("main");
        when(repoMock.resolve("refs/heads/main")).thenReturn(commitIdMock);
        when(repoMock.resolve("refs/remotes/origin/main")).thenReturn(commitIdMock);
        when(gitMock.getRepository()).thenReturn(repoMock);

        String status = GitSyncStatusPanel.checkSyncStatus(gitMock);
        assertEquals("✅ Synchronized", status);
    }

    @Test
    void testSyncStatusAhead() throws IOException, GitAPIException {
        Git gitMock = mock(Git.class);
        Repository repoMock = mock(Repository.class);
        ObjectId localCommit = mock(ObjectId.class);
        ObjectId remoteCommit = mock(ObjectId.class);
        RevWalk revWalk = mock(RevWalk.class);
        RevCommit revCommit = mock(RevCommit.class);

        when(repoMock.getBranch()).thenReturn("main");
        when(repoMock.resolve("refs/heads/main")).thenReturn(localCommit);
        when(repoMock.resolve("refs/remotes/origin/main")).thenReturn(remoteCommit);
        when(gitMock.getRepository()).thenReturn(repoMock);
        when(revWalk.parseCommit(any())).thenReturn(revCommit);
        when(revWalk.next()).thenReturn(revCommit, null);

        try (MockedStatic<RevWalk> revWalkMock = mockStatic(RevWalk.class)) {
            revWalkMock.when(() -> new RevWalk(repoMock)).thenReturn(revWalk);
            String status = GitSyncStatusPanel.checkSyncStatus(gitMock);
            assertEquals("🔼 Ahead: Local branch has un-pushed commits.", status);
        }
    }

    @Test
    void testSyncStatusBehind() throws IOException, GitAPIException {
        Git gitMock = mock(Git.class);
        Repository repoMock = mock(Repository.class);
        ObjectId localCommit = mock(ObjectId.class);
        ObjectId remoteCommit = mock(ObjectId.class);
        RevWalk revWalk = mock(RevWalk.class);
        RevCommit revCommit = mock(RevCommit.class);

        when(repoMock.getBranch()).thenReturn("main");
        when(repoMock.resolve("refs/heads/main")).thenReturn(localCommit);
        when(repoMock.resolve("refs/remotes/origin/main")).thenReturn(remoteCommit);
        when(gitMock.getRepository()).thenReturn(repoMock);
        when(revWalk.parseCommit(any())).thenReturn(revCommit);
        when(revWalk.next()).thenReturn(null, revCommit);

        try (MockedStatic<RevWalk> revWalkMock = mockStatic(RevWalk.class)) {
            revWalkMock.when(() -> new RevWalk(repoMock)).thenReturn(revWalk);
            String status = GitSyncStatusPanel.checkSyncStatus(gitMock);
            assertEquals("🔽 Behind: Local branch is missing remote commits.", status);
        }
    }

    @Test
    void testSyncStatusDiverged() throws IOException, GitAPIException {
        Git gitMock = mock(Git.class);
        Repository repoMock = mock(Repository.class);
        ObjectId localCommit = mock(ObjectId.class);
        ObjectId remoteCommit = mock(ObjectId.class);
        RevWalk revWalk = mock(RevWalk.class);
        RevCommit revCommit = mock(RevCommit.class);

        when(repoMock.getBranch()).thenReturn("main");
        when(repoMock.resolve("refs/heads/main")).thenReturn(localCommit);
        when(repoMock.resolve("refs/remotes/origin/main")).thenReturn(remoteCommit);
        when(gitMock.getRepository()).thenReturn(repoMock);
        when(revWalk.parseCommit(any())).thenReturn(revCommit);
        when(revWalk.next()).thenReturn(revCommit, revCommit, null);

        try (MockedStatic<RevWalk> revWalkMock = mockStatic(RevWalk.class)) {
            revWalkMock.when(() -> new RevWalk(repoMock)).thenReturn(revWalk);
            String status = GitSyncStatusPanel.checkSyncStatus(gitMock);
            assertEquals("⚠ Diverged: Local and remote branches have different changes.", status);
        }
    }

    @Test
    void testErrorHandling() {
        try (MockedStatic<Git> gitMock = mockStatic(Git.class)) {
            gitMock.when(() -> Git.open(any())).thenThrow(new IOException("Test Exception"));
            syncStatusPanel.updateSyncStatus();
            JLabel label = (JLabel) syncStatusPanel.getComponent(0);
            assertEquals("❌ Error checking sync state", label.getText());
        }
    }
}
