package org.jabref.gui.shared;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.preferences.GuiPreferences;
import org.jabref.gui.service.DialogNotificationService;
import org.jabref.logic.git.GitClientHandler;
import org.jabref.model.database.BibDatabaseContext;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GitPushActionTest {

    private GuiPreferences preferences;
    private DialogService dialogService;
    private StateManager stateManager;
    private BibDatabaseContext databaseContext;

    private GitPushAction gitPushAction;

    @BeforeEach
    void setUp() {
        preferences = mock(GuiPreferences.class);
        dialogService = mock(DialogService.class);
        stateManager = mock(StateManager.class);
        databaseContext = mock(BibDatabaseContext.class);

        gitPushAction = new GitPushAction(preferences, dialogService, stateManager);
    }

    @Test
    void execute_WhenNoActiveDatabase_DoesNothing() {
        when(stateManager.getActiveDatabase()).thenReturn(Optional.empty());

        gitPushAction.execute();

        verifyNoInteractions(dialogService);
    }

    @Test
    void execute_WhenDatabasePathIsEmpty_DoesNothing() {
        when(stateManager.getActiveDatabase()).thenReturn(Optional.of(databaseContext));
        when(databaseContext.getDatabasePath()).thenReturn(Optional.empty());

        gitPushAction.execute();

        verifyNoInteractions(dialogService);
    }

    @Test
    void execute_WhenPushFails_ShowsErrorDialog() throws IOException, GitAPIException {
        Path mockPath = Path.of("some/fake/path");
        when(stateManager.getActiveDatabase()).thenReturn(Optional.of(databaseContext));
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));

        GitClientHandler gitClientHandlerSpy = spy(new GitClientHandler(mockPath.getParent(),
                new DialogNotificationService(dialogService),
                preferences));

        doThrow(new GitAPIException("Push failed") { }).when(gitClientHandlerSpy)
                                                      .checkGitRepoThenCommitAndPushAndDisplayMsg();
    }
}
