package org.jabref.logic.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.jabref.logic.l10n.Localization;
import org.jabref.logic.preferences.CliPreferences;
import org.jabref.logic.service.NotificationService;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitClientHandler extends GitHandler {
   private final static String GENERAL_ERROR_MESSAGE = Localization.lang("This Git operation failed") + "\n\n" +
        Localization.lang("MOST LIKELY CAUSE: Missing Git credentials.") + "\n" +
        Localization.lang("Please set your credentials by entering your username and a personal access token generated for your account") + "\n" + "\n" +
        Localization.lang("Other possible causes:") + "\n" +
        "- " + Localization.lang("Network connectivity issues") + "\n" +
        "- " + Localization.lang("Remote repository rejecting the operation") +
        "- " + Localization.lang("No changes are made");
    private final NotificationService notificationService;
    private final CliPreferences preferences;

    public GitClientHandler(Path repositoryPath,
                            NotificationService notificationService,
                            CliPreferences preferences) {
        super(repositoryPath, false);
        this.notificationService = notificationService;
        this.preferences = preferences;

        if (preferences != null && preferences.getGitPreferences() != null) {
            this.credentialsProvider = new UsernamePasswordCredentialsProvider(
                    preferences.getGitPreferences().getGitHubUsername(),
                    preferences.getGitPreferences().getGitHubPasskey()
            );
        }
    }

    /**
     * Contains logic for commiting and pushing after a database is saved locally,
     * if the relevant preferences are present.<p>
     * A git commit is created, a pull is executed and then changes are pushed. In the case of
     * an error, the repository is reverted to the commit and a regular pull is executed.
     *
     */
    public void postSaveDatabaseAction() {
        if (isGitRepository() &&
                preferences.getGitPreferences().getAutoPushEnabled() &&
                preferences.getGitPreferences().getAutoPushEnabled()) {
            RevCommit localCommit = getLatestCommit();
            try {
                createCommitOnCurrentBranch("Automatic update via JabRef", false);
            } catch (GitAPIException | IOException e) {
                return;
            }

            try {
                pull();
                if (this.getRepository().getRepositoryState() == RepositoryState.MERGING) {
                    resetRepository();
                    revertToCommit(localCommit.getName());
                    notificationService.notify("Failed to pull from remote repository");
                    return;
                }
            } catch (IOException | GitAPIException e) {
                revertToCommit(localCommit.getName());
                notificationService.notify("Failed to pull from remote repository");
                return;
            }

            try {
                pushCommitsToRemoteRepository();
            } catch (IOException e) {
                LOGGER.error("Failed to push");
                showGeneralErrorDialog();
            }
        }
    }

    public void revertToCommit(String ref) {
        try {
            Git git = Git.open(this.repositoryPathAsFile);
            git.reset()
               .setMode(ResetCommand.ResetType.SOFT)
               .setRef(ref)
               .call();
            notificationService.notify("Reverted to previous commit");
        } catch (IOException | GitAPIException e) {
            LOGGER.error("Failed to revert to commit");
            notificationService.notify("Failed to revert to commit");
        }
    }

    private void resetRepository() {
        try {
            Git git = Git.open(this.repositoryPathAsFile);
            git.reset()
               .setMode(ResetCommand.ResetType.HARD)
               .call();
        } catch (IOException | GitAPIException e) {
            LOGGER.error("Failed to reset repository");
        }
    }

    private void pull() throws IOException, GitAPIException {
        Git git = Git.open(this.repositoryPathAsFile);
        git.pull()
           .setCredentialsProvider(this.credentialsProvider)
           .call();
    }

    private RevCommit getLatestCommit() {
        try {
            Repository repository = new FileRepositoryBuilder()
                    .findGitDir(new File(this.repositoryPath.toString() + "/.git"))
                    .setMustExist(true)
                    .build();
            RevWalk revWalk = new RevWalk(repository);

            ObjectId head = repository.resolve("HEAD");
            return revWalk.parseCommit(head);
        } catch (IOException e) {
            LOGGER.error("Failed to get latest commit");
        }
        return null;
    }

    public void showGeneralErrorDialog() {
        notificationService.showErrorDialog(GENERAL_ERROR_MESSAGE);
    }

    public void checkGitRepoAndPullAndDisplayMsg() throws IOException {

        if (!isGitRepository()) {
            handleNonGitRepoOperation();
        } else if (pullOnCurrentBranch()) {
            notificationService.notify(Localization.lang("Successfully pulled from remote repository"));
        } else {
           showGeneralErrorDialog();
        }
    }

    public void checkGitRepoThenCommitAndPushAndDisplayMsg()throws IOException, GitAPIException {
        if (!isGitRepository()) {
            handleNonGitRepoOperation();
            return;
        }
        boolean commitCreated = this.createCommitOnCurrentBranch(Localization.lang("Automatic update via JabRef"), false);
        if (!commitCreated) {
            showGeneralErrorDialog();
            return;
        }
        boolean successPush = pushCommitsToRemoteRepository();
        if (successPush) {
            notificationService.notify(Localization.lang("Successfully Pushed changes to remote repository"));
        } else {
            showGeneralErrorDialog();
        }
    }

    public void handleNonGitRepoOperation() {
        LOGGER.info("Not a git repository at path: {}", repositoryPath);
        notificationService.notify(Localization.lang("This is not a Git repository"));
    }
}
