package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    //UI Variables
    Button  mainButton, searchButton, notificationsButton, settingsButton;
    TextView title;
    ScrollView backgroundScroll;

    //color update method for UI
    void switchColor(){
        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        boolean darkMode= SPBusData.getBoolean("darkMode", false);

        if(darkMode == false){
            //setting text color
            mainButton.setTextColor(Color.BLACK);
            searchButton.setTextColor(Color.BLACK);
            notificationsButton.setTextColor(Color.BLACK);
            settingsButton.setTextColor(Color.BLACK);
            title.setTextColor(Color.BLACK);
            //setting background and button colors
            mainButton.setBackgroundColor(Color.CYAN);
            searchButton.setBackgroundColor(Color.CYAN);
            notificationsButton.setBackgroundColor(Color.CYAN);
            settingsButton.setBackgroundColor(Color.CYAN);
            backgroundScroll.setBackgroundColor(Color.WHITE);


        }else{
            //setting text color
            mainButton.setTextColor(Color.YELLOW);
            searchButton.setTextColor(Color.YELLOW);
            notificationsButton.setTextColor(Color.YELLOW);
            settingsButton.setTextColor(Color.YELLOW);
            title.setTextColor(Color.YELLOW);
            //setting background and button colors
            mainButton.setBackgroundColor(Color.DKGRAY);
            searchButton.setBackgroundColor(Color.DKGRAY);
            notificationsButton.setBackgroundColor(Color.DKGRAY);
            settingsButton.setBackgroundColor(Color.DKGRAY);
            backgroundScroll.setBackgroundColor(Color.BLACK);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //defining UI variables
        mainButton = (Button) findViewById(R.id.mainButton);
        searchButton = (Button) findViewById(R.id.searchScreenButton);
        notificationsButton = (Button) findViewById(R.id.notificationsScreenButton);
        settingsButton = (Button) findViewById(R.id.settingsScreenButton);
        backgroundScroll = (ScrollView) findViewById(R.id.backgroundScrollMenu);
        title = (TextView) findViewById(R.id.titleTextSchedule);

        switchColor();//updating color

        //Listeners for buttons
        mainButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //return to main screen
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //go to search screen
                startActivity(new Intent(MenuActivity.this, SearchActivity.class));
            }
        });
        notificationsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //go to notifications screen
                startActivity(new Intent(MenuActivity.this, NotificationsActivity.class));
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //return to settings screen
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
            }
        });
    }
}