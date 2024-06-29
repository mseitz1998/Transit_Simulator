package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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

public class MainActivity extends AppCompatActivity {

    //update handler variables
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000; //delay of 1000 aka 1 second

    //list of our busses
    ArrayList<Bus> busList = new ArrayList<Bus>();
    //UI variables
    ListView busListView;
    Button menuButton;
    TextView titleTextView;
    ScrollView backgroundScroll;

    //color switch method
    void switchColor(){
        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        boolean darkMode= SPBusData.getBoolean("darkMode", false);

        if(darkMode == false){
            //setting text color
            menuButton.setTextColor(Color.BLACK);
            titleTextView.setTextColor(Color.BLACK);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.CYAN);
            busListView.setBackgroundColor(Color.CYAN);
            backgroundScroll.setBackgroundColor(Color.WHITE);

        }else{
            //setting text color
            menuButton.setTextColor(Color.YELLOW);
            titleTextView.setTextColor(Color.YELLOW);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.DKGRAY);
            busListView.setBackgroundColor(Color.GRAY);
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
            //Log.w("Main Activity", "Busses Loaded into Main Activity"); //testing code
        }catch(Exception e){
            //warning if not loaded
            Toast.makeText(getApplicationContext(), "WARNING: LOAD FAIL!", Toast.LENGTH_SHORT).show();
            Log.w("MainActivity", "LOAD FAIL");
        }
    }

    //on the first time running the program, please accept terms and conditions.
    void firstTime(){
        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        if(SPBusData.getBoolean("firstTime", true)){
            startActivity(new Intent(MainActivity.this, TermsActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //starting the service
        Intent busServiceIntent = new Intent(this, BusService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(busServiceIntent);
        }else{
            startService(busServiceIntent);
        }

        //Define UI Vars
        busListView = (ListView) findViewById(R.id.mainActivityListView);
        menuButton = (Button) findViewById(R.id.menuButton);
        titleTextView = (TextView) findViewById(R.id.titleTextSchedule);
        backgroundScroll = (ScrollView) findViewById(R.id.bkgScroll);

        switchColor();//adjusting color
        firstTime();//launching into terms page if it is first time using program.


        //Listener for menu button
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //return to main screen
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        //handling countdown and updating the bus list
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);

                loadBusses(); //calling load method to load busses to activity

                //loading busses to the listview
                ArrayAdapter<Bus> arrayAdapter = new ArrayAdapter<Bus>
                        (getApplicationContext(), android.R.layout.simple_list_item_1, busList);
                busListView.setAdapter(arrayAdapter);

            }
        }, delay);
        super.onResume();

        //creating a listener for the listView
        busListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //getting selected bus
                Bus selectedBus = busList.get(position);

                //test code
                //Toast.makeText(getApplicationContext(), selectedBus.toString(), Toast.LENGTH_SHORT).show();

                //using shared preferences to save and access the bus data
                SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
                SharedPreferences.Editor editor = SPBusData.edit();
                editor.putString("viewThisBus", selectedBus.busID);//saving the ID of  the bus we want to see
                editor.commit();

                //launching into view schedule activity
                startActivity(new Intent(MainActivity.this, SeeScheduleActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable); //stop handler when activity not visible
    }
}