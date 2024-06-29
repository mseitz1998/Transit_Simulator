package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TermsActivity extends AppCompatActivity {

    Button acceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        //accept button ui
        acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setBackgroundColor(Color.CYAN);
        acceptButton.setTextColor(Color.BLACK);

        //Listener for menu button
        acceptButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //using shared preferences to save and access the bus data
                SharedPreferences SPBusData = getSharedPreferences("SPBusData", MODE_PRIVATE);
                SharedPreferences.Editor editor = SPBusData.edit();
                editor.putBoolean("firstTime", false);//saving that terms are accepted
                editor.commit();
                //return to main screen
                startActivity(new Intent(TermsActivity.this, MainActivity.class));
            }
        });
    }
}