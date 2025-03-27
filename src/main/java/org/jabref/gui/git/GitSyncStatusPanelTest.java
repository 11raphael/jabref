package org.jabref.gui.git;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GitSyncStatusPanelTest {

    private Git mockGit;
    private Repository mockRepo;
    private RevWalk mockWalk;

    @BeforeEach
    void setUp() throws Exception {
        mockGit = mock(Git.class);
        mockRepo = mock(Repository.class);
        mockWalk = mock(RevWalk.class);

        when(mockGit.getRepository()).thenReturn(mockRepo);
    }

    @Test
    @DisplayName("Returns synchronized when local and remote commits are equal")
    void testSynchronizedStatus() throws Exception {
        ObjectId commitId = ObjectId.fromString("0123456789012345678901234567890123456789");

        when(mockRepo.getBranch()).thenReturn("main");
        when(mockRepo.resolve("refs/heads/main")).thenReturn(commitId);
        when(mockRepo.resolve("refs/remotes/origin/main")).thenReturn(commitId);

        mockFetch();

        String result = invokeCheckSyncStatus(mockGit);
        assertEquals("✅ Synchronized", result);
    }

    @Test
    @DisplayName("Returns ahead when local is ahead of remote")
    void testAheadStatus() throws Exception {
        ObjectId local = ObjectId.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ObjectId remote = ObjectId.fromString("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        RevCommit localCommit = mock(RevCommit.class);
        RevCommit remoteCommit = mock(RevCommit.class);

        when(mockRepo.getBranch()).thenReturn("main");
        when(mockRepo.resolve("refs/heads/main")).thenReturn(local);
        when(mockRepo.resolve("refs/remotes/origin/main")).thenReturn(remote);

        mockFetch();
        whenNewRevWalk(local, remote, localCommit, remoteCommit, 2, 0);

        String result = invokeCheckSyncStatus(mockGit);
        assertEquals("🔼 Ahead: Local branch has un-pushed commits.", result);
    }

    @Test
    @DisplayName("Returns behind when local is behind remote")
    void testBehindStatus() throws Exception {
        ObjectId local = ObjectId.fromString("cccccccccccccccccccccccccccccccccccccccc");
        ObjectId remote = ObjectId.fromString("dddddddddddddddddddddddddddddddddddddddd");

        RevCommit localCommit = mock(RevCommit.class);
        RevCommit remoteCommit = mock(RevCommit.class);

        when(mockRepo.getBranch()).thenReturn("main");
        when(mockRepo.resolve("refs/heads/main")).thenReturn(local);
        when(mockRepo.resolve("refs/remotes/origin/main")).thenReturn(remote);

        mockFetch();
        whenNewRevWalk(local, remote, localCommit, remoteCommit, 0, 3);

        String result = invokeCheckSyncStatus(mockGit);
        assertEquals("🔽 Behind: Local branch is missing remote commits.", result);
    }

    @Test
    @DisplayName("Returns diverged when both ahead and behind")
    void testDivergedStatus() throws Exception {
        ObjectId local = ObjectId.fromString("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        ObjectId remote = ObjectId.fromString("ffffffffffffffffffffffffffffffffffffffff");

        RevCommit localCommit = mock(RevCommit.class);
        RevCommit remoteCommit = mock(RevCommit.class);

        when(mockRepo.getBranch()).thenReturn("main");
        when(mockRepo.resolve("refs/heads/main")).thenReturn(local);
        when(mockRepo.resolve("refs/remotes/origin/main")).thenReturn(remote);

        mockFetch();
        whenNewRevWalk(local, remote, localCommit, remoteCommit, 2, 2);

        String result = invokeCheckSyncStatus(mockGit);
        assertEquals("⚠️ Diverged: Local and remote branches have different changes.", result);
    }

    @Test
    @DisplayName("Returns error if commits can't be resolved")
    void testErrorStatusIfCannotResolve() throws Exception {
        when(mockRepo.getBranch()).thenReturn("main");
        when(mockRepo.resolve(anyString())).thenReturn(null);

        mockFetch();

        String result = invokeCheckSyncStatus(mockGit);
        assertEquals("⚠️ Error: Could not resolve branch commits.", result);
    }

    // ---------- Helper Methods ----------

    private void mockFetch() throws Exception {
        FetchCommand mockFetch = mock(FetchCommand.class);
        when(mockGit.fetch()).thenReturn(mockFetch);
        when(mockFetch.call()).thenReturn(null);
    }

    private void whenNewRevWalk(ObjectId local, ObjectId remote, RevCommit localCommit, RevCommit remoteCommit,
                                int aheadCount, int behindCount) throws IOException {
        when(mockRepo.newObjectReader()).thenReturn(null);
        RevWalk revWalk = spy(new RevWalk(mockRepo));
        when(mockRepo.newObjectReader()).thenReturn(revWalk.getObjectReader());

        // ahead
        when(mockWalk.parseCommit(local)).thenReturn(localCommit);
        when(mockWalk.parseCommit(remote)).thenReturn(remoteCommit);
        when(mockWalk.next()).thenReturn(mock(RevCommit.class)).thenReturn(null);

        // Simulate different numbers of commits in each direction
        doReturn(localCommit).when(mockWalk).parseCommit(local);
        doReturn(remoteCommit).when(mockWalk).parseCommit(remote);
    }

    private String invokeCheckSyncStatus(Git git) throws Exception {
        var method = GitSyncStatusPanel.class.getDeclaredMethod("checkSyncStatus", Git.class);
        method.setAccessible(true);
        return (String) method.invoke(null, git);
    }
}
