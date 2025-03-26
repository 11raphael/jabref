package org.jabref.gui.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.preferences.GuiPreferences;
import org.jabref.logic.git.GitClientHandler;
import org.jabref.model.database.BibDatabaseContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GitPullActionTest {
    @TempDir
    Path tempDir;

    private DialogService dialogService;
    private StateManager stateManager;
    private BibDatabaseContext databaseContext;
    private GitPullAction gitPullAction;

    @BeforeEach
    void setUp() {
        GuiPreferences preferences = mock(GuiPreferences.class);
        dialogService = mock(DialogService.class);
        stateManager = mock(StateManager.class);
        databaseContext = mock(BibDatabaseContext.class);

        gitPullAction = new GitPullAction(preferences, dialogService, stateManager);
    }

    @Test
    void execute_shouldDoNothing_whenNoActiveDatabase() {
        when(stateManager.getActiveDatabase()).thenReturn(Optional.empty());

        gitPullAction.execute();

        verifyNoInteractions(dialogService);
    }

    @Test
    void execute_shouldDoNothing_whenDatabasePathIsEmpty() {
        when(stateManager.getActiveDatabase()).thenReturn(Optional.of(databaseContext));
        when(databaseContext.getDatabasePath()).thenReturn(Optional.empty());

        gitPullAction.execute();

        verifyNoInteractions(dialogService);
    }

    @Test
    void execute_shouldCallGitClientHandler_whenPathIsValid() throws IOException {
        Path dbPath = tempDir.resolve("test.bib");
        Files.createFile(dbPath);

        when(stateManager.getActiveDatabase()).thenReturn(Optional.of(databaseContext));
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(dbPath));

        gitPullAction.execute();
    }

    @Test
    void execute_shouldShowErrorDialog_whenIOExceptionOccurs() throws IOException {
        Path dbPath = tempDir.resolve("test.bib");
        Files.createFile(dbPath);

        when(stateManager.getActiveDatabase()).thenReturn(Optional.of(databaseContext));
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(dbPath));

        GitClientHandler handler = mock(GitClientHandler.class);
        doThrow(new IOException("Simulated error")).when(handler).checkGitRepoAndPullAndDisplayMsg();

        gitPullAction.execute();
    }
}
