package com.example.finalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class BusService extends Service {

    //update booleans
    static boolean needsUpdate = false; //
    static boolean resetNeeded = false;

    //receiver for ACTION_TIME_TICK
    MyReceiver myReceiver = new MyReceiver();

    //list of our busses
    ArrayList<Bus> busList = new ArrayList<Bus>();

    //update handler variables
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000; //delay of 1000 aka 1 second

    //lists and int for arrival notifications
    static ArrayList<String> notoCodesList = new ArrayList<String>();
    static ArrayList<Integer> notoTimeList = new ArrayList<Integer>();
    int notificationsSent = 0;

    static void notifyUser(String notoString, int notoTime) { //method for receiving notification codes.
        notoCodesList.add(notoString);//adding noto code to code list
        notoTimeList.add(notoTime);
    }

    void saveBusses() {
        //writing busses to storage
        try {
            //saving bus list to file
            FileOutputStream fos = getApplicationContext().openFileOutput("BusFile", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(busList);
            os.close();
            fos.close();
            //saving bool to shared prefs to confirm successful save
            SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
            SharedPreferences.Editor editor = SPBusData.edit();
            editor.putBoolean("savedBusses", true);
            editor.commit();
            //log write to keep track
            Log.w("BusService", "Busses Saved");
        } catch (Exception e) {
            //warning if not oreo
            Toast.makeText(getApplicationContext(), "WARNING: WRITE FAIL!", Toast.LENGTH_SHORT).show();
            Log.w("BusService", "WRITE FAIL");
        }
    }

    void loadBusses() {
        //loading busses from storage
        try {
            //loading busses in from save
            FileInputStream fis = getApplicationContext().openFileInput("BusFile");
            ObjectInputStream is = new ObjectInputStream(fis);
            busList = (ArrayList<Bus>) is.readObject();
            is.close();
            fis.close();
            //log write to confirm load
            Log.w("BusService", "Busses Loaded");
        } catch (Exception e) {
            //warning if not loaded
            Toast.makeText(getApplicationContext(), "WARNING: LOAD FAIL!", Toast.LENGTH_SHORT).show();
            Log.w("BusService", "LOAD FAIL");
        }
    }

    //this method generates the busses for the program.
    void generateBusses() {
        Log.w("BusService", "Generating Busses");//log write
        //clearing our list for new busses
        busList = new ArrayList<Bus>();

        //generating and saving new busses
        for (int i = 1; i < 4; i++) { //the first batch are on 5 min schedules
            String newBusID = i + "0" + i;
            ArrayList<BusStop> newStopsList = new ArrayList<BusStop>();
            for (int c = 1; c < 4; c++) {
                newStopsList.add(new BusStop(i + "0" + c));
            }
            busList.add(new Bus(5, newStopsList, newBusID));
        }
        for (int i = 4; i < 7; i++) {//the next three are on ten min schedules
            String newBusID = i + "0" + i;
            ArrayList<BusStop> newStopsList = new ArrayList<BusStop>();
            for (int c = 1; c < 4; c++) {
                newStopsList.add(new BusStop(i + "0" + c));
            }
            busList.add(new Bus(10, newStopsList, newBusID));
        }
        for (int i = 7; i < 10; i++) {//the next three are on 15 min schedules
            String newBusID = i + "0" + i;
            ArrayList<BusStop> newStopsList = new ArrayList<BusStop>();
            for (int c = 1; c < 4; c++) {
                newStopsList.add(new BusStop(i + "0" + c));
            }
            busList.add(new Bus(15, newStopsList, newBusID));
        }
        for (int i = 1; i < 4; i++) {// the last batch are on 20 min schedules
            String newBusID = "12" + i;
            ArrayList<BusStop> newStopsList = new ArrayList<BusStop>();
            for (int c = 1; c < 4; c++) {
                newStopsList.add(new BusStop(i + "1" + c));
            }
            busList.add(new Bus(20, newStopsList, newBusID));
        }

        //printing busses
        for (int i = 0; i < busList.size(); i++) {
            Log.w("generateBusses", busList.get(i).toString());
        }

        saveBusses();//save busses method
    }

    public BusService() {
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Bus service starting", Toast.LENGTH_SHORT).show();

        //creating notification to run as foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //creating channel and manager for notifications
            NotificationChannel channel = new NotificationChannel("BusNotificationChannel", "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Bus notification channel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            Log.w("BusService", "Bus Service Start"); //log write
        } else {
            //warning if not oreo
            Toast.makeText(getApplicationContext(), "WARNING: ANDROID VER MUST BE OREO OR LATER!", Toast.LENGTH_SHORT).show();
            Log.w("BusService", "Android must be oreo or greater!");
        }

        //creating intents to run foreground service
        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE);

        //creating notification for foreground service
        Notification trackNoto = new NotificationCompat.Builder(this.getApplicationContext(), "BusNotificationChannel")
                .setContentTitle("Bus Tracker")
                .setContentText("Bus Tracking Active")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();


        //requesting that the service run in foreground
        startForeground(5, trackNoto);

        //check shared preferences for previous save
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        SharedPreferences.Editor editor = SPBusData.edit();
        boolean previousBusses = SPBusData.getBoolean("savedBusses", false);

        //context for notos below
        Context thisCon = this.getApplicationContext();

        //loading busses if they are present, creating them if not
        if (previousBusses == true) {
            loadBusses();
            Log.w("BusService", "Previous Busses Loaded");
        } else { //if no busses are present, we create them
            generateBusses();//calling bus generator
            Log.w("BusService", "Created Busses");
        }

        //handling countdown and updating the bus list
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);

                //updating the busses if needed
                if (needsUpdate == true) {
                    for (int i = 0; i < busList.size(); i++) {
                        busList.get(i).minuteTick();
                    }
                    needsUpdate = false;
                    saveBusses(); //saving busses
                }

                //checking if app should be reset
                if (resetNeeded == true) {
                    generateBusses();
                    resetNeeded = false;
                }

                //checking for notifications
                SharedPreferences notoSP = getSharedPreferences("notoSP", MODE_PRIVATE);
                SharedPreferences.Editor editorNoto = notoSP.edit();
                if (notoCodesList.size() > 0) {
                    //checking if notification needed and dispensing if it is.
                    if (notoSP.getBoolean(notoCodesList.get(0), false) && !(notoSP.getBoolean("silentMode", false))) {
                        //if a notification code is found, the user is sent a notification
                        //first we generate notificaton text.
                        String busCode = notoCodesList.get(0).substring(0, 3);
                        String stopCode = notoCodesList.get(0).substring(3);
                        int timeTillStop = notoTimeList.get(0);
                        String outputString = "Bus " + busCode + " Arriving at Stop " + stopCode + " In " + timeTillStop + " Min!";
                        //dispensing notifications
                        Log.w("NotifierPending", outputString);//writing output string to log
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                //creating channel and manager
                                NotificationChannel channel2 = new NotificationChannel("ArrivalNotoChan", "ArrivalNotos", NotificationManager.IMPORTANCE_DEFAULT);
                                channel2.setDescription("Bus Arrival Noto Channel");
                                NotificationManager manager = getSystemService(NotificationManager.class);
                                manager.createNotificationChannel(channel2);
                                //creating notification
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(thisCon, "ArrivalNotoChan")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("Bus " + busCode)
                                        .setContentText(outputString)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                //creating manager
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(thisCon);

                                //android studio generated this, I need to ask permission to post notifications.
                                if (ActivityCompat.checkSelfPermission(thisCon, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }

                                //sending notification
                                notificationManager.notify(notificationsSent, builder.build());
                                notificationsSent += 1;
                                Log.w("NotifierCompleted", outputString);//writing output string to log
                            }else{
                                Log.w("Notifier", "Notification Error! Current SDK too low!");//writing error to log
                            }
                        }catch(Exception e){
                            Log.w("Notifier", "Notification Error!");//writing error to log
                        }
                    }else if(notoSP.getBoolean("silentMode", false)){
                        //saying that silent mode is on in the log
                        Log.w("Notifier", "Silent Mode On");
                    }
                    //removing the first item from each noto value list, clearing our queue
                    notoCodesList.remove(0);
                    notoTimeList.remove(0);
                }
            }
        }, delay);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart

        //intent filter for our receiver, setting up ACTION_TIME_TICK
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        this.registerReceiver(myReceiver, intentFilter);
        //Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();//testing code

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //No binding needed
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Bus Service Killed", Toast.LENGTH_SHORT).show();
        Log.w("BusService", "Bus Service Killed");

        //removing receiver once done.
        unregisterReceiver(myReceiver);
        //stop handler when not needed
        handler.removeCallbacks(runnable);
    }
}