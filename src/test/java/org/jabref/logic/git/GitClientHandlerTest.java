package org.jabref.logic.git;

import java.io.IOException;
import java.nio.file.Path;

import org.jabref.logic.l10n.Localization;
import org.jabref.logic.preferences.CliPreferences;
import org.jabref.logic.service.NotificationService;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GitClientHandlerTest {

    private GitClientHandler gitClientHandler;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        CliPreferences cliPreferences = mock(CliPreferences.class);
        GitPreferences gitPreferences = mock(GitPreferences.class);

        when(cliPreferences.getGitPreferences()).thenReturn(gitPreferences);
        when(gitPreferences.getGitHubUsername()).thenReturn("user");
        when(gitPreferences.getGitHubPasskey()).thenReturn("token");

        // Provide a dummy path that won't be a git repo
        Path nonGitPath = Path.of(System.getProperty("java.io.tmpdir"));
        gitClientHandler = spy(new GitClientHandler(nonGitPath, notificationService, cliPreferences));
    }

    @Test
    void testHandleNonGitRepoNotifiesUser() {
        gitClientHandler.handleNonGitRepoOperation();
        verify(notificationService).notify(Localization.lang("This is not a Git repository"));
    }

    @Test
    void testShowGeneralErrorDialog() {
        gitClientHandler.showGeneralErrorDialog();
        verify(notificationService).showErrorDialog(anyString());
    }

    @Test
    void testCheckGitRepoAndPullAndDisplayMsg_NotAGitRepo_ShowsNotification() throws IOException {
        doReturn(false).when(gitClientHandler).isGitRepository();

        gitClientHandler.checkGitRepoAndPullAndDisplayMsg();

        verify(notificationService).notify(Localization.lang("This is not a Git repository"));
    }

    @Test
    void testCheckGitRepoAndPullAndDisplayMsg_Success() throws IOException {
        doReturn(true).when(gitClientHandler).isGitRepository();
        doReturn(true).when(gitClientHandler).pullOnCurrentBranch();

        gitClientHandler.checkGitRepoAndPullAndDisplayMsg();

        verify(notificationService).notify(Localization.lang("Successfully pulled from remote repository"));
    }

    @Test
    void testCheckGitRepoAndPullAndDisplayMsg_Failure() throws IOException {
        doReturn(true).when(gitClientHandler).isGitRepository();
        doReturn(false).when(gitClientHandler).pullOnCurrentBranch();

        gitClientHandler.checkGitRepoAndPullAndDisplayMsg();

        verify(notificationService).showErrorDialog(anyString());
    }

    @Test
    void testCheckGitRepoThenCommitAndPushAndDisplayMsg_Success() throws IOException, GitAPIException {
        doReturn(true).when(gitClientHandler).isGitRepository();
        doReturn(true).when(gitClientHandler).createCommitOnCurrentBranch(anyString(), eq(false));
        doReturn(true).when(gitClientHandler).pushCommitsToRemoteRepository();

        gitClientHandler.checkGitRepoThenCommitAndPushAndDisplayMsg();

        verify(notificationService).notify(Localization.lang("Successfully Pushed changes to remote repository"));
    }

    @Test
    void testCheckGitRepoThenCommitAndPushAndDisplayMsg_FailedCommit() throws IOException, GitAPIException {
        doReturn(true).when(gitClientHandler).isGitRepository();
        doReturn(false).when(gitClientHandler).createCommitOnCurrentBranch(anyString(), eq(false));

        gitClientHandler.checkGitRepoThenCommitAndPushAndDisplayMsg();

        verify(notificationService).showErrorDialog(anyString());
    }

    @Test
    void testCheckGitRepoThenCommitAndPushAndDisplayMsg_FailedPush() throws IOException, GitAPIException {
        doReturn(true).when(gitClientHandler).isGitRepository();
        doReturn(true).when(gitClientHandler).createCommitOnCurrentBranch(anyString(), eq(false));
        doReturn(false).when(gitClientHandler).pushCommitsToRemoteRepository();

        gitClientHandler.checkGitRepoThenCommitAndPushAndDisplayMsg();

        verify(notificationService).showErrorDialog(anyString());
    }
}
