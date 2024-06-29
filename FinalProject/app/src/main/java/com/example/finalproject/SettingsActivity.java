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
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    //UI variables defined
    Button menuButton, silentButton, lightDarkButton, resetButton;

    TextView title, copyrightTV, emailTV;

    ScrollView backgroundScroll;

    boolean silent = false; //siletn mode boolean

    //updates app color
    void switchColor(){
        //using shared preferences to save and access the bus data
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        boolean darkMode= SPBusData.getBoolean("darkMode", false);

        if(darkMode == false){
            //setting text color
            menuButton.setTextColor(Color.BLACK);
            silentButton.setTextColor(Color.BLACK);
            lightDarkButton.setTextColor(Color.BLACK);
            resetButton.setTextColor(Color.BLACK);
            title.setTextColor(Color.BLACK);
            copyrightTV.setTextColor(Color.BLACK);
            emailTV.setTextColor(Color.BLACK);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.CYAN);
            silentButton.setBackgroundColor(Color.CYAN);
            lightDarkButton.setBackgroundColor(Color.CYAN);
            resetButton.setBackgroundColor(Color.CYAN);
            backgroundScroll.setBackgroundColor(Color.WHITE);

        }else{
            //setting text color
            menuButton.setTextColor(Color.YELLOW);
            silentButton.setTextColor(Color.YELLOW);
            lightDarkButton.setTextColor(Color.YELLOW);
            resetButton.setTextColor(Color.YELLOW);
            title.setTextColor(Color.YELLOW);
            copyrightTV.setTextColor(Color.YELLOW);
            emailTV.setTextColor(Color.YELLOW);
            //setting background and button colors
            menuButton.setBackgroundColor(Color.DKGRAY);
            silentButton.setBackgroundColor(Color.DKGRAY);
            lightDarkButton.setBackgroundColor(Color.DKGRAY);
            resetButton.setBackgroundColor(Color.DKGRAY);
            backgroundScroll.setBackgroundColor(Color.BLACK);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Defining UI variables
        menuButton = (Button) findViewById(R.id.menuButtonSettings);
        silentButton = (Button) findViewById(R.id.silentModeButton);
        resetButton = (Button) findViewById(R.id.resetAppButton);
        lightDarkButton = (Button) findViewById(R.id.lightDarkButton);
        backgroundScroll = (ScrollView) findViewById(R.id.backgroundScrollView);
        title = (TextView) findViewById(R.id.titleTextSchedule);
        copyrightTV = (TextView) findViewById(R.id.CopyrightTextView);
        emailTV = (TextView) findViewById(R.id.EmailTextView);

        //shared prefs for bus data extraction and wipe if needed
        SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
        SharedPreferences.Editor editor = SPBusData.edit();

        //using shared preferences to save and access the bus data
        SharedPreferences notoSP = getSharedPreferences("notoSP", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = notoSP.edit();
        //updating color prefs
        switchColor();

        //updating silent mode and UI output
        silent = notoSP.getBoolean("silentMode", false);
        if(silent == true){
            silentButton.setText("Silent Mode: On");
        }else{
            silentButton.setText("Silent Mode: Off");
        }

        //Listeners for buttons
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //return to main screen
                startActivity(new Intent(SettingsActivity.this, MenuActivity.class));
            }
        });
        //color mode button, updates color mode and color
        lightDarkButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //change color mode
                boolean darkMode= SPBusData.getBoolean("darkMode", false);
                if(darkMode == false){
                    editor.putBoolean("darkMode", true);
                }else{
                    editor.putBoolean("darkMode", false);
                }
                editor.commit();
                switchColor();//update color
            }
        });
        //silent mode button listener - Silences the notifications
        silentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if(silent == true){//switching silentmode variables
                    editor2.putBoolean("silentMode", false);
                    silentButton.setText("Silent Mode: Off");
                    silent = false;
                    //Toast.makeText(getApplicationContext(), "Silent Mode Off", Toast.LENGTH_SHORT).show();
                }else{
                    editor2.putBoolean("silentMode", true);
                    silentButton.setText("Silent Mode: On");
                    silent = true;
                    //Toast.makeText(getApplicationContext(), "Silent Mode On", Toast.LENGTH_SHORT).show();
                }
                editor2.commit();//saving
            }
        });
        //reset button, resets app data and sends user back to terms and conditions
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                BusService.resetNeeded = true; //resetting busses using the bus service
                //wiping the shared prefs
                editor.clear();
                editor.commit();
                editor2.clear();
                editor2.commit();
                switchColor();//resetting color
                Toast.makeText(getApplicationContext(), "App Reset!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, TermsActivity.class));// sending user back to terms
            }
        });

    }
}