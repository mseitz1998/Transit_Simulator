package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class SeeScheduleActivity extends AppCompatActivity {

    //update handler variables
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000; //delay of 1000 aka 1 second

    //UI elements
    Button menuButton, seeNotificationsButton, addNotificationButton;
    ListView busScheduleListView;
    TextView busInfoTextView, titleTextView;
    ScrollView backgroundScroll;

    //program data
    String viewThisBus;//id of current bus
    Bus viewBus; //current bus object
    ArrayList<Bus> busList; //list of busses loaded in
    ArrayList<BusStop> viewBusStopsList = new ArrayList<BusStop>(); //list of current bus's stops
    ArrayList<String> outputStrings = new ArrayList<String>(); //output strings for display
    boolean toggleMode = false; //boolean for notification toggle mode

    //color switch method
    void switchColor(){
        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        boolean darkMode= SPBusData.getBoolean("darkMode", false);

        if(darkMode == false){
            //setting text color
            menuButton.setTextColor(Color.BLACK);
            seeNotificationsButton.setTextColor(Color.BLACK);
            addNotificationButton.setTextColor(Color.BLACK);
            titleTextView.setTextColor(Color.BLACK);
            busInfoTextView.setTextColor(Color.BLACK);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.CYAN);
            seeNotificationsButton.setBackgroundColor(Color.CYAN);
            addNotificationButton.setBackgroundColor(Color.CYAN);
            busScheduleListView.setBackgroundColor(Color.CYAN);
            backgroundScroll.setBackgroundColor(Color.WHITE);

        }else{
            //setting text color
            menuButton.setTextColor(Color.YELLOW);
            seeNotificationsButton.setTextColor(Color.YELLOW);
            addNotificationButton.setTextColor(Color.YELLOW);
            titleTextView.setTextColor(Color.YELLOW);
            busInfoTextView.setTextColor(Color.YELLOW);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.DKGRAY);
            seeNotificationsButton.setBackgroundColor(Color.DKGRAY);
            addNotificationButton.setBackgroundColor(Color.DKGRAY);
            busScheduleListView.setBackgroundColor(Color.GRAY);
            backgroundScroll.setBackgroundColor(Color.BLACK);
        }
    }

    //method to load our busses in from storage
    void loadBusses(){
        //loading busses froms storage
        try{
            //loading busses in from save
            FileInputStream fis = getApplicationContext().openFileInput("BusFile");
            //Log.w("Main Activity", "File Opening");
            ObjectInputStream is = new ObjectInputStream(fis);
            //Log.w("Main Activity", "Object input stream success");
            busList = (ArrayList<Bus>) is.readObject();
            is.close();
            fis.close();
            //log write to confirm load
            //Log.w("Main Activity", "Busses Loaded into See Schedule"); //testing code
        }catch(Exception e){
            //warning if not loaded
            Toast.makeText(getApplicationContext(), "WARNING: LOAD FAIL!", Toast.LENGTH_SHORT).show();
            Log.w("MainActivity", "LOAD FAIL");
        }
    }

    void findBus(){//method to find our desired bus in the list.
        for(int i = 0; i < busList.size(); i++ ){
            if(busList.get(i).busID.equals(viewThisBus)){
                viewBus = busList.get(i);
                viewBusStopsList = viewBus.stopsList;
                //Log.w("See schedule:", "Found Bus: " + viewBus.toString()); test code
            }
        }
    }

    void displaySchedule(){
        //displays the bus info
        //accessing notifications shared prefs
        SharedPreferences notoSP = getSharedPreferences("notoSP", MODE_PRIVATE);

        //creating output strings
        outputStrings = new ArrayList<String>();
        //adding notifications on to stops that have notifications on
        for(int i = 0; i < viewBusStopsList.size(); i++){
            String output = viewBusStopsList.get(i).toString();
            if(notoSP.getBoolean(viewBus.busID + viewBusStopsList.get(i).toString(), false)){
                output += " - Notifications On";
            }
            outputStrings.add(output);
        }

        //displaying our bus stops
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getApplicationContext(), android.R.layout.simple_list_item_1, outputStrings);
        busScheduleListView.setAdapter(arrayAdapter);
        //Displaying bus data
        int timeTo = viewBus.minutesBetweenStops + viewBus.latenessFactor;
        String busInfoText = viewBus.toString() + " - " + timeTo + " Min between stops";
        busInfoTextView.setText(busInfoText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_schedule);

        //define UI variables
        menuButton = (Button) findViewById(R.id.menuButtonSettings);
        seeNotificationsButton = (Button) findViewById(R.id.goToNotificationsButton);
        addNotificationButton = (Button) findViewById(R.id.addNotificationButton);
        busScheduleListView = (ListView) findViewById(R.id.busScheduleListView);
        busInfoTextView = (TextView) findViewById(R.id.busInfoTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextSchedule);
        backgroundScroll = (ScrollView) findViewById(R.id.backgroundScrollSchedule);

        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        SharedPreferences.Editor editor = SPBusData.edit();
        viewThisBus = SPBusData.getString("viewThisBus", "ERR" );
        Log.w("SeeSchedule", "Loading bus: " + viewThisBus + " schedule");

        switchColor();//updating color

        //Listeners for buttons
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //return to main screen
                startActivity(new Intent(SeeScheduleActivity.this, MenuActivity.class));
            }
        });
        seeNotificationsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //go to notifications screen
                startActivity(new Intent(SeeScheduleActivity.this, NotificationsActivity.class));
            }
        });
        //allows user to toggle notifications
        addNotificationButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //toggling toggle mode on and off, updating text
                if(toggleMode == false){
                    toggleMode = true;
                    addNotificationButton.setText("End Toggle");
                    Toast.makeText(getApplicationContext(), "Toggle by clicking bus stop!", Toast.LENGTH_SHORT).show();
                }else{
                    toggleMode = false;
                    addNotificationButton.setText("Toggle Notifications");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //handling countdown and updating the bus list
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                //loading, finding, and displaying the bus schedule
                loadBusses();
                findBus();
                displaySchedule();
            }
        }, delay);
        super.onResume();

        //bus stop list view listener, can be used to toggle notifications.
        busScheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(toggleMode == true){
                    //getting selected stop
                    BusStop selectedBusStop = viewBusStopsList.get(position);

                    //test code
                    //Toast.makeText(getApplicationContext(), selectedBus.toString(), Toast.LENGTH_SHORT).show();

                    //using shared preferences to save and access the bus data
                    SharedPreferences notoSP = getSharedPreferences("notoSP", MODE_PRIVATE);
                    SharedPreferences.Editor editor = notoSP.edit();
                    //switching boolean over to save notification setting
                    if(notoSP.getBoolean(viewBus.busID + selectedBusStop.toString(), false)){
                        editor.putBoolean(viewBus.busID + selectedBusStop.toString(), false);
                    }else{
                        editor.putBoolean(viewBus.busID + selectedBusStop.toString(), true);
                    }
                    editor.commit();
                    //updating the output
                    loadBusses();
                    findBus();
                    displaySchedule();
                }else{
                    Toast.makeText(getApplicationContext(), "Toggle notifications with toggle button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable); //stop handler when activity not visible
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //saving program data
        outState.putBoolean("toggleMode", toggleMode);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //retrieving program data
        toggleMode = savedInstanceState.getBoolean("toggleMode");
        if(toggleMode == false){
            addNotificationButton.setText("Toggle Notifications");
        }else{
            addNotificationButton.setText("End Toggle");
        }
    }
}