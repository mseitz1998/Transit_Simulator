package com.example.finalproject;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Bus implements Serializable {
    String busID; //bus id
    //these ints are for monitoring bus progress
    int untilNextStop = 0; //minutes until bus is at next stop
    int minutesBetweenStops; //minutes between bus stops
    int stopsVisited = 0; //number of stops the bus has visited so far
    int latenessFactor =0; //how many extra minutes each bus will need to hit each stop.

    ArrayList<BusStop> stopsList; //list of stops the bus visits
    boolean running = true; // whether or not the bus is running

    //bus constructor
    Bus(int timing, ArrayList<BusStop> stops, String ID){
        stopsList = stops;
        minutesBetweenStops = timing;
        busID = ID;

        //assigning lateness factor to a random int between -1 and 1.
        Random random = new Random();
        latenessFactor = random.nextInt( (3+0) + 0) - 1;

        //time remaining till next stop
        untilNextStop = minutesBetweenStops + latenessFactor;

        notifyUser();//checking if notifications are needed and deploying them if they are
    }

    //generating bus string
    public String toString(){
        String busInfo;
        //try-catch in case the toString gets called when all stops have been visited before reset
        try{
            //generating string representation of bus stop
            String nextStop = stopsList.get(stopsVisited).toString();
            String onTime;
            if(latenessFactor == 1){
                onTime = "- Late";
            }else if (latenessFactor == -1){
                onTime = "- Early";
            }else{
                onTime = "- On Time";
            }
            busInfo = "Bus " + busID + ": Next Stop: " + nextStop + " In " + untilNextStop + " Min " + onTime;
        }catch(Exception e){
            busInfo = "Please Wait";
        }

        return busInfo;
    }

    //method to update bus each minute
    public void minuteTick(){
        if(running == true){
            //counting down until the bus reaches it's stop
            untilNextStop -=1;
            if(untilNextStop == 0){
                //updating variables and stops, writing visit to log.
                untilNextStop = minutesBetweenStops + latenessFactor;
                Log.w("Bus", "BUSID: " + busID + " Visited Stop " + stopsList.get(stopsVisited).toString());//writing to log
                stopsVisited += 1;
                notifyUser(); //sending notification if needed.
            }
            //if the bus has reached it's last stop, reset it.
            if(stopsVisited == stopsList.size()){
                //resetting variables
                untilNextStop = minutesBetweenStops + latenessFactor;
                stopsVisited = 0;

                //assigning lateness factor to a random int between -1 and 1.
                Random random = new Random();
                latenessFactor = random.nextInt( (3+0) + 0) - 1;
            }
        }
    }

    //notify user if bus is coming - calls the bus service notify user static void
    public void notifyUser(){
        try{
            BusService.notifyUser(busID + stopsList.get(stopsVisited), untilNextStop);
        }catch(Exception e){
            Log.w("Bus", "No notification possible");
        }
    }
}
