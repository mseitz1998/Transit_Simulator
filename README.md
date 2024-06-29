Transit_Simulator 

Created by Mitchell Seitz, July 2023.
Copyright July 2023 - Present. 

Android Studio Project Folder: FinalProject

.APK File for testing: Transit_Simulator.apk
__________________________________________________________________________
App Description: 

This app is intended to function as a simulated bus tracker. 

The busses are simulated by the activity of the foreground BusService, and 
this is where the majority of the app’s processing takes place. The movement 
of busses is incremented on each receipt of the ACTION_TIME_TICK broadcast, 
and busses may experience simulated lateness or earlyness. 

Launching this app first sends the user through a terms and conditions screen 
with copyright info and terms to agree to. Once the user accepts, they are 
sent to the main screen, which shows all busses currently running. 

From here, the user can access the menu, which can allow them to access: 
- the search screen to search for specific busses or stops,
- the notifications screen which shows users their shared notifications,
- the settings screen where users can change program settings like light/dark
  and silent mode.

The user can elect to receive notifications for when busses are approaching a 
stop, and can enter silent mode to prevent the notifications from showing. 
