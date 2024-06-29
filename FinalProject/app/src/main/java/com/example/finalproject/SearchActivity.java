package com.example.finalproject;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    //declaring UI elements
    Button menuButton, searchButton;
    EditText searchEditText;
    ListView searchOutput;
    ScrollView backgroundSearchScroll;
    ListView outputListView;
    TextView titleText;

    //program data
    ArrayList<Bus> busList = new ArrayList<Bus>();
    ArrayList<Bus> searchedBusses = new ArrayList<Bus>();//busses foud by search
    ArrayList<String> busStrings = new ArrayList<String>();//display strings for above busses

    //color switch method
    void switchColor(){
        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        boolean darkMode= SPBusData.getBoolean("darkMode", false);
        if(darkMode == false){
            //setting text color
            menuButton.setTextColor(Color.BLACK);
            searchButton.setTextColor(Color.BLACK);
            titleText.setTextColor(Color.BLACK);
            searchEditText.setTextColor(Color.BLACK);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.CYAN);
            searchButton.setBackgroundColor(Color.CYAN);
            searchOutput.setBackgroundColor(Color.CYAN);
            backgroundSearchScroll.setBackgroundColor(Color.WHITE);

        }else{
            //setting text color
            menuButton.setTextColor(Color.YELLOW);
            searchButton.setTextColor(Color.YELLOW);
            titleText.setTextColor(Color.YELLOW);
            searchEditText.setTextColor(Color.YELLOW);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.DKGRAY);
            searchButton.setBackgroundColor(Color.DKGRAY);
            searchOutput.setBackgroundColor(Color.DKGRAY);
            backgroundSearchScroll.setBackgroundColor(Color.BLACK);
            //setting search editText text so it's visible
            searchEditText.setText("Search");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //UI element definitions
        menuButton = (Button) findViewById(R.id.menuButton);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchOutput = (ListView) findViewById(R.id.searchOutput);
        backgroundSearchScroll = (ScrollView) findViewById(R.id.backgroundScrollSearchActivity);
        titleText = (TextView) findViewById(R.id.titleTextSchedule);

        switchColor();// updating color mode

        //Listeners for buttons
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //return to main screen
                startActivity(new Intent(SearchActivity.this, MenuActivity.class));
            }
        });
        //listener for search button
        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //getting search text input
                String searchText = searchEditText.getText().toString();

                //loading busses
                loadBusses();

                //resetting arrays for info
                searchedBusses = new ArrayList<Bus>();
                busStrings = new ArrayList<String>();

                //identifying busses with id or stop id numbers containing the search text
                for(int i = 0; i < busList.size(); i++){
                    //checking if bus id contains search text
                    if(busList.get(i).busID.toString().contains(searchText)){
                        searchedBusses.add(busList.get(i)); //adding bus to list
                    }else{ //if id does not contain search text, check for stops containing search text
                        for(int c = 0; c < busList.get(i).stopsList.size(); c++){//checking each stop
                            if(busList.get(i).stopsList.get(c).toString().contains(searchText)){
                                searchedBusses.add(busList.get(i));//adding bus to list
                            }
                        }
                    }
                }

                //now we construct a string for each bus in the found busses list
                for(int i = 0; i < searchedBusses.size(); i++){
                    //starting generation of output string
                    String outputString = "Bus: " + searchedBusses.get(i).busID; //bus ID
                    boolean foundStopMatch = false;
                    for(int c = 0; c < searchedBusses.get(i).stopsList.size(); c++){ //searching stops for text match
                        if(searchedBusses.get(i).stopsList.get(c).toString().contains(searchText)){
                            if(foundStopMatch == false){ //if matching stops are found, they are added to the string
                                outputString += " Stops:";
                                foundStopMatch = true;
                            }
                            outputString += " " +  searchedBusses.get(i).stopsList.get(c).toString();
                        }
                    }
                    busStrings.add(outputString);//adding to string arraylist
                }

                //updating output to hold relevant strings
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (getApplicationContext(), android.R.layout.simple_list_item_1, busStrings);
                searchOutput.setAdapter(arrayAdapter);
            }
        });
        //creating a listener for the listView
        searchOutput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //getting selected bus
                Bus selectedBus = searchedBusses.get(position);

                //using shared preferences to save and access the bus data
                SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
                SharedPreferences.Editor editor = SPBusData.edit();
                editor.putString("viewThisBus", selectedBus.busID);//saving the ID of  the bus we want to see
                editor.commit();

                //launching into view schedule activity for selected bus
                startActivity(new Intent(SearchActivity.this, SeeScheduleActivity.class));
            }
        });
    }

    //saving program data
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //saving program data
        outState.putSerializable("busList", busList);
        outState.putSerializable("searchedBusses", searchedBusses);
        outState.putSerializable("busStrings", busStrings);

    }

    //restoring program data
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //retrieving program data
        busList = (ArrayList<Bus>) savedInstanceState.getSerializable("busList");
        searchedBusses = (ArrayList<Bus>) savedInstanceState.getSerializable("searchedBusses");
        busStrings = (ArrayList<String>) savedInstanceState.getSerializable("busStrings");

        //updating output to hold relevant strings
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getApplicationContext(), android.R.layout.simple_list_item_1, busStrings);
        searchOutput.setAdapter(arrayAdapter);

        switchColor(); //updating color
    }
}