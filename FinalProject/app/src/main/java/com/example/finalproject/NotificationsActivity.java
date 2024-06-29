package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    //Declare UI varaibles
    Button menuButton, silentButton;
    ListView notificationsListView;
    TextView titleTextView , countTextView, infoTextView;
    ScrollView notificationsBackgroundScroll;

    //program data
    ArrayList<Bus> busList; //list of busses loaded in
    ArrayList<Bus> notoBussesList; //list of busses with notifications
    ArrayList<String> outputStrings = new ArrayList<String>(); //output strings for display
    boolean silent = false; //silentmode toggle boolean
    int notificationsCount = 0; //noto count

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
            Log.w("NotificationsActivity", "LOAD FAIL");
        }
    }

    //color switch method
    void switchColor(){
        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        boolean darkMode= SPBusData.getBoolean("darkMode", false);
        //color mode
        if(darkMode == false){
            //setting text color
            menuButton.setTextColor(Color.BLACK);
            silentButton.setTextColor(Color.BLACK);
            titleTextView.setTextColor(Color.BLACK);
            countTextView.setTextColor(Color.BLACK);
            infoTextView.setTextColor(Color.BLACK);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.CYAN);
            silentButton.setBackgroundColor(Color.CYAN);
            notificationsListView.setBackgroundColor(Color.CYAN);
            notificationsBackgroundScroll.setBackgroundColor(Color.WHITE);

        }else{
            //setting text color
            menuButton.setTextColor(Color.YELLOW);
            silentButton.setTextColor(Color.YELLOW);
            titleTextView.setTextColor(Color.YELLOW);
            countTextView.setTextColor(Color.YELLOW);
            infoTextView.setTextColor(Color.YELLOW);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.DKGRAY);
            silentButton.setBackgroundColor(Color.DKGRAY);
            notificationsListView.setBackgroundColor(Color.DKGRAY);
            notificationsBackgroundScroll.setBackgroundColor(Color.BLACK);
        }
    }

    //method that loads busses into
    void notoBusses(){
        //clearing the lists
        notoBussesList = new ArrayList<Bus>();
        outputStrings = new ArrayList<String>();
        notificationsCount = 0;
        //using shared preferences to save and access the notification data
        SharedPreferences notoSP = getSharedPreferences("notoSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = notoSP.edit();
        //checking each bus for stop notifications and adding them to the list if they have any
        for(int i = 0; i < busList.size(); i++){
            //preparing data
            Bus thisBus = busList.get(i);
            boolean saveThis = false;
            String outputString = "Bus: "+ thisBus.busID  + " - Stops:";
            //finding if the bus has notifications, and which stops.
            for(int c = 0; c < thisBus.stopsList.size(); c++){
                if(notoSP.getBoolean(thisBus.busID + thisBus.stopsList.get(c).toString(), false)){
                    //if a notification is found, we add it to the output and
                    saveThis = true;
                    outputString += " " + thisBus.stopsList.get(c).toString();
                    notificationsCount += 1;
                }
            }
            //saving output string and busses with notifications at equal index in their lists.
            if(saveThis == true){
                notoBussesList.add(thisBus);
                outputStrings.add(outputString);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //defining UI elements
        menuButton = (Button) findViewById(R.id.menuButton);
        silentButton = (Button) findViewById(R.id.silentNotosButton);
        notificationsListView = (ListView) findViewById(R.id.notificationsListView);
        countTextView = (TextView) findViewById(R.id.savedNotosTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextSchedule);
        infoTextView = (TextView) findViewById(R.id.infoTextNoto);
        notificationsBackgroundScroll = (ScrollView) findViewById(R.id.notificationsBackgroundScrollView);


        //using shared preferences to save and access the bus data
        SharedPreferences notoSP = getSharedPreferences("notoSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = notoSP.edit();

        //updating color prefs
        //switchColor();

        loadBusses(); //loading busses in
        notoBusses(); //updating bus info
        switchColor(); //switching color

        //loading busses to the listview
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getApplicationContext(), android.R.layout.simple_list_item_1, outputStrings);
        notificationsListView.setAdapter(arrayAdapter);
        countTextView.setText("Saved Notifications: " + notificationsCount);

        //silentmode boolean
        silent = notoSP.getBoolean("silentMode", false);
        if(silent == true){
            silentButton.setText("Silent Mode: On");
        }else{
            silentButton.setText("Silent Mode: Off");
        }

        //Listener for menu button
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //return to main screen
                startActivity(new Intent(NotificationsActivity.this, MenuActivity.class));
            }
        });
        silentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //toggling silent mode
                if(silent == true){//switching silentmode variables
                    editor.putBoolean("silentMode", false);
                    silentButton.setText("Silent Mode: Off");
                    silent = false;
                    //Toast.makeText(getApplicationContext(), "Silent Mode Off", Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean("silentMode", true);
                    silentButton.setText("Silent Mode: On");
                    silent = true;
                    //Toast.makeText(getApplicationContext(), "Silent Mode On", Toast.LENGTH_SHORT).show();
                }
                editor.commit();//saving
            }
        });
        //launch into selected bus schedule when clicked
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //getting bus to launch into
                Bus launchThis = notoBussesList.get(position);
                //using shared preferences to save and access the bus data
                SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
                SharedPreferences.Editor editor = SPBusData.edit();
                editor.putString("viewThisBus", launchThis.busID);//saving the ID of  the bus we want to see
                editor.commit();

                //launching into view schedule activity
                startActivity(new Intent(NotificationsActivity.this, SeeScheduleActivity.class));
            }
        });

    }
}