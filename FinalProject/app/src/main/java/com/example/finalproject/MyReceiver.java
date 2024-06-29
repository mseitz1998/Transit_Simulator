package com.example.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //update the bus service to need updating
        BusService.needsUpdate = true;

        //log write for testing.
        Log.w("MyReceiver", "ACTION_TIME_TICK Received");
    }
}