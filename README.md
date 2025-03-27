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

### GitHandler.java  (*modified*)
- Added optional parameter to constructor (createRepo) so that a new repository does not have to be initialized at constructor call
    - This was done to not disturb the existing usage of GitHandler in JabRef (namely the SLR feature)
- Added getRepository()
  - Returns a Repository object, of this instance's file directory, from a builder class 

### GitClientHandler.java (*added*)
  - Handles git client functionality
  - Usage of git http authentication

### SaveDatabaseAction.java (*modified*)
- Added method call to GitClientHandler.postSaveDatabaseAction() to handle git functionality on database save

### OpenDatabaseAction.java (*modified*)
- Added CheckForVersionControlAction to post-open actions

### GitPullAction.java (*added*)
- Logic for pulling from a remote repository

### GitPushAction.java (*added*)
- Logic for pushing to a remote repository

### CheckForVersionControlAction.java (*added*)
- Action which checks whether a library is under version control
  - If git tracked, the library context is tagged as versioned
  - If git tracked, the library is updated from the remote repository

### GitPreferences.java (*added*)
- Stores preferences for git functionality
  - Stores GitHub username for http authentication
  - Stores GitHub personal access token for http authentication
  - Stores boolean for whether a library should update its remote repository when library is saved

### CliPreferences.java (*modified*)
- Added GitPreferences

### JabRefCliPreferences.java (*modified*)
- Added initialisation of GitPreferences


## Jiewu's Files
### LibraryTab.java
- *Automatic Git Monitoring: Periodically checks Git status every 10 seconds and updates the UI without manual intervention.
Visual Status Indicators: Displays "[Modified]" or "[Committed]" directly in tab titles, providing immediate visual feedback on file status.
Real-time Updates: Refreshes status when database changes occur, ensuring users always see the current Git state.*

### DialogNotificationService.java
- *Implements the NotificationService interface, This class allows the logic layer to send messages to the user through the GUI without directly depending on GUI components.*

### NotificationService.java
- *An interface has 2 methods notify and showErrorDialogThrough this interface, JabRef's core logic can communicate information to users without needing to know how these messages are displayed, making the code more maintainable and testable.*

## Sihas's Files
### MainMenu.java (*modified*)
- Added new code to this file, used existing formatting for menu item code in the same file, and edited accordingly.

### GeneralTab.fxml (*modified*)
- Added front-end code for preferences, used existing code as a standard to create HBox instances, and changed it appropriately.

### GeneralTab.java (*modified*)
- Added code using existing code as a template for setup and localisation for the preference checkbox and prompts.

### GeneralTabViewModel.java (*modified*)
- Added simple boolean/ string properties to store preferences.

### JabRef_en.properties (*modified*)
- Added all necessary localisation.

## Tests
Note:- JabRef has stated that they do not expect testing to be done for GUI components.
- GitHandlerTest.java
- GitPullActionTest.java
- GitPushActionTest.java
- GitPreferencesTest.java
- CheckForVersionControlActionEndToEndTest.java

