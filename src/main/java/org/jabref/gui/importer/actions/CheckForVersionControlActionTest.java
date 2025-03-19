package org.jabref.gui.importer.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jabref.gui.DialogService;
import org.jabref.logic.importer.ParserResult;
import org.jabref.logic.preferences.CliPreferences;
import org.jabref.model.database.BibDatabaseContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckForVersionControlActionTest {

    @Mock
    private ParserResult parserResult;
    @Mock
    private DialogService dialogService;
    @Mock
    private CliPreferences cliPreferences;
    @Mock
    private BibDatabaseContext databaseContext;

    private CheckForVersionControlAction action;

    @BeforeEach
    void setUp() {
        action = new CheckForVersionControlAction();
        when(parserResult.getDatabaseContext()).thenReturn(databaseContext);
    }

    // Test cases for isActionNecessary()

    @Test
    void isActionNecessary_WhenDatabasePathIsEmpty_ShouldReturnFalse() {
        when(databaseContext.getDatabasePath()).thenReturn(Optional.empty());

        boolean result = action.isActionNecessary(parserResult, dialogService, cliPreferences);

        assertFalse(result, "Expected isActionNecessary to return false when no database path exists.");
    }

    @Test
    void isActionNecessary_WhenDatabasePathExistsButNotAGitRepo_ShouldReturnFalse() {
        Path mockPath = Path.of("/path/to/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));

        boolean result = action.isActionNecessary(parserResult, dialogService, cliPreferences);

        assertFalse(result, "Expected isActionNecessary to return false for a non-Git repository.");
    }

    @Test
    void isActionNecessary_WhenDatabasePathExistsAndIsAGitRepo_ShouldReturnTrue() {
        Path mockPath = Path.of("/path/to/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));

        boolean result = action.isActionNecessary(parserResult, dialogService, cliPreferences);

        assertTrue(result, "Expected isActionNecessary to return true for a valid Git repository.");
    }

    // Test cases for performAction()

    @Test
    void performAction_WhenGitPullSucceeds_ShouldNotThrowException() throws IOException {
        Path mockPath = Path.of("/path/to/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));
        when(cliPreferences.isGitAutoPullEnabled()).thenReturn(true);

        action.performAction(parserResult, dialogService, cliPreferences);
    }

    @Test
    void performAction_WhenGitPullFails_ShouldHandleException() throws IOException {
        Path mockPath = Path.of("/path/to/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));
        when(cliPreferences.isGitAutoPullEnabled()).thenReturn(true);

        action.performAction(parserResult, dialogService, cliPreferences);

        verify(dialogService, times(1)).showErrorDialogAndWait(anyString(), anyString());
    }

    @Test
    void performAction_WhenDatabasePathIsEmpty_ShouldDoNothing() {
        when(databaseContext.getDatabasePath()).thenReturn(Optional.empty());

        action.performAction(parserResult, dialogService, cliPreferences);

        verifyNoInteractions(dialogService);
    }

    @Test
    void performAction_WhenPreferenceDisablesAutoPull_ShouldNotPull() {
        Path mockPath = Path.of("/path/to/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));
        when(cliPreferences.isGitAutoPullEnabled()).thenReturn(false);

        action.performAction(parserResult, dialogService, cliPreferences);

        verify(dialogService, never()).notify(anyString());
    }

    @Test
    void isActionNecessary_BasedOnStaticMethod_ShouldReturnCorrectResult() {
        Path mockPath = Path.of("/path/to/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));

        boolean result = action.isActionNecessary(parserResult, dialogService, cliPreferences);

        assertFalse(result, "Expected isActionNecessary to return false for a non-Git repository.");
    }

    @Test
    void performAction_HandlesDifferentGitErrors_Appropriately() throws IOException {
        Path mockPath = Path.of("/path/to/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));
        when(cliPreferences.isGitAutoPullEnabled()).thenReturn(true);

        action.performAction(parserResult, dialogService, cliPreferences);

        verify(dialogService, times(1)).showErrorDialogAndWait(anyString(), anyString());
    }

    @Test
    void performAction_WithActualGitRepo_ShouldSuccessfullyPull(@TempDir Path tempDir) throws IOException {
        Path bibFilePath = tempDir.resolve("library.bib");
        Files.createFile(bibFilePath);

        Path gitDir = tempDir.resolve(".git");
        Files.createDirectory(gitDir);

        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(bibFilePath));
        when(cliPreferences.isGitAutoPullEnabled()).thenReturn(true);

        action.performAction(parserResult, dialogService, cliPreferences);

        assertTrue(Files.exists(gitDir), "Git directory should still exist after operation");
    }

    @Test
    void isActionNecessary_FileNotUnderGitControl_ShouldReturnFalse() {
        Path mockPath = Path.of("/path/to/non-git/database.bib");
        when(databaseContext.getDatabasePath()).thenReturn(Optional.of(mockPath));

        boolean result = action.isActionNecessary(parserResult, dialogService, cliPreferences);

        assertFalse(result, "Expected isActionNecessary to return false for a file not under Git control.");
    }
}
