# Adding simple git integration to JabRef

## JabRef

Authors:
- [All JabRef Contributors](https://github.com/JabRef/jabref/graphs/contributors)
- [Raphael Ahiable](https://github.com/11raphael)
- [Sihas Abeywickrama](https://github.com/SihasA) (1 commit under [DimplyMuffin](https://github.com/DimplyMuffin))
- [Jiewu Deng](https://github.com/wtfjjking)
- [Arjan Bedi](https://github.com/rapidshotzz) 
- [Argya Pramusakti](https://github.com/arp-23)
- [Aryaman Amit Mehta](https://github.com/Ary006)
- [Yash Shukla](https://github.com/yshukla01)

## Our approach to referencing all leveraged code
JabRef is open-source software; hence, many files were changed and not created directly by us. In most cases, the challenge was to review the existing implementation and produce a solution that does not conflict with what is already there. As a result, we have leveraged and changed many functions and files.

With this in mind, we have opted to include in this README the segments of code in the files that we changed and added (*marked respectively*), along with a list of modified and added code for each file.

## Raphael's Files
### GitHandler.java 
- *change to be added*

### GitClientHandler.java (*added*)
- *change to be added*

### SaveDatabaseAction.java
- *change to be added*

### OpenDatabaseAction.java
- *change to be added*

### CheckForVersionControlAction.java
- *change to be added*

### GitPullAction.java
- *change to be added*

### GitPushAction.java
- *change to be added*

### GitPreferences.java
- *change to be added*

### CliPreferences.java
- *change to be added*

### JabRefCliPreferences.java
- *change to be added*


## Jiewu's Files
### LibraryTab.java
- *change to be added*

### StandardActions.java
- *change to be added*

### DialogNotificationService.java
- *change to be added*

### NotificationService.java
- *change to be added*

### BibDatabaseContext.java
- *change to be added*


## Sihas's Files
### MainMenu.java (*modified*)
- Added new code to this file, used existing formatting for menu item code in the same file, and edited accordingly.

### GeneralTab.fxml (*modified*)
- Added front-end code for preferences, used existing code as a standard to create HBox instances, and changed it appropriately.

### GeneralTab.java
- Simply c

### GeneralTabViewModel.java
- *change to be added*

### JabRef_en.properties
- *change to be added*



## To Remove
### ConflictDetector.java
- *change to be added*

### ConflictResolution.java
- *change to be added*

### GitBibDatabaseDiff.java
- *change to be added*

### GitBibEntryDiff.java
- *change to be added*

### ConflictDetectorTest.java
- *change to be added*

### ConflictResolutionTest.java
- *change to be added*

### GitBibDatabaseDiffTest.java
- *change to be added*

### GitBibEntryDiffTest.java
- *change to be added*

### CheckForVersionControlActionEndToEndTest.java
- *change to be added*






## Tests

### GitHandlerTest.java
- *change to be added*

### GitPreferencesTest.java
- *change to be added*

### GitStatusTesterTest.java
- *change to be added*
