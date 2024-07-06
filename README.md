Transit_Simulator 

Created by Mitchell Seitz, July 2023.

Copyright July 2023 - Present

Android Studio Project Folder: FinalProject

.APK File for testing: Transit_Simulator.apk
__________________________________________________________________________
App Description: 

This app is intended to function as a simulated bus tracker. This app was
my final project for COMP2161, and I'm sharing it here with the permission 
of my course professor, Musfiq Rahman.

The busses are simulated by the activity of the foreground BusService, and 
this is where the majority of the app’s processing takes place. The movement 
of busses is incremented on each receipt of the ACTION_TIME_TICK broadcast, 
and busses may experience simulated lateness or earlyness. 

Launching this app first sends the user through a terms and conditions screen 
with copyright info and terms to agree to. Once the user accepts, they are 
sent to the main screen, which shows all busses currently running. 

This app needs a foreground service permission to simulate bus movement, as 
well as notification permission. The notification permission is so that the 
app can carry out the essential transit app function of notifying users of 
their bus approaching, and the foreground service is so the busses can be 
simulated and notifications scanned for. 

From here, the user can access the menu, which can allow them to access: 
- the search screen to search for specific busses or stops,
- the notifications screen which shows users their shared notifications,
- the settings screen where users can change program settings like light/dark
  and silent mode.

The user can elect to receive notifications for when busses are approaching a 
stop, and can enter silent mode to prevent the notifications from showing. 

This app relies heavily on saving and retrieving data to and from files and 
shared preferences. I avoided anything that could result in any two program 
components saving to the same data location within a short period of time, 
as having to add resource holds would increase complexity. 

My code includes some methods that are  copy-pasted from section to section, 
like the loadBusses method, which is present in nearly every part of my program. 
If I had more time, I would make this a static method somewhere. I made frequent 
use of handlers to run loops that update the UI and BusService, to ensure that 
there is never more than a second latency on information updates. 

BusService responds to a receiver that processes ACTION_TIME_TICK, and updates and 
saves data on receipt of this broadcast. The data that is saved is then picked up by
other activities. 

The main activity, search activity, notifications activity, and schedule activities 
work by extracting either all busses or select busses from the saved bus data, and 
either displaying them as strings directly or displaying information on the bus and 
stops extracted based on need. 

Notifications are saved and shown in the notifications activity and schedule activities, 
and are indicated by boolean variables saved to the notoSP shared preferences. This 
notification data is simple, and takes the form of a boolean with id xxxyyy, where xxx 
is bus number and yyy is the stop number, and has a value of true if that bus/stop combo 
requires notification. Bus objects will send their bus/stop id for their next stop to the 
BusService, which then checks if there is a notification required for that bus, and 
dispenses one if there is. If no notification is required, the id is deleted from the 
queue, and if a notification request is saved but silent mode is on, then a message is 
written to log. 
__________________________________________________________________________________________

Classes, Receivers, Threading, Files, Etc.

Services: 

  BusService 
  
  This foreground service simulates the movement of the buses and handles 
  notifications, data updating, and more. This is the largest part of the program, and 
  most complex code. Notifications are dispensed from here, and the bus data is updated 
  from here, even when the app is closed.

Receiver: 

  MyReceiver - This receiver is set up by BusService, and responds to ACTION_TIME_TICK 
  to spur program updates. 

Threading: 

  Handlers are used to create running loops that update program data as needed. 
  They are disengaged when BusService or activities are closed.

Activities: 

  MainActivity, MenuActivity, NotificationsActivity, SearchActivity, SeeScheduleActivity, 
  SettingsActivity, TermsActivity 

  These activities are used to interact with the user and provide/request the required data 
  for program function. They retrieve bus data deposited by BusService and save notification 
  and settings data from the user. 

Classes: 

  Bus, BusStop 
  
  The classes representing buses and stops. Contain Id numbers, toString, etc. These mostly 
  exist as places to store data. The Bus class can have its minuteTick method called to have 
  it update for another minute of bus operation. Bus class sends its notification ID to the 
  bus service periodically, triggering notification release if needed. 

Saved Data: 

  BusFile, SPBusData, notoSP
  
  BusFile is the file that the bus data is serialized to and retrieved from, while the SPBusData 
  shared preferences handles miscellaneous program saved primitives, and the notoSP shared 
  preferences handle notification-related primitives. 


