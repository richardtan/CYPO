package com.tckr.dukcud.wear.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Listen to the screen actions
 */
public class ScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "wear.ScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String screenState = "NULL";

        // Find if the intent is any of the three and set an attribute to it to be passed
        // to the service
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenState = "ACTION_OFF";
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenState = "ACTION_ON";
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            screenState = "ACTION_SHUTDOWN";
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            screenState = "ACTION_USER_PRESENT";
        }

        Log.v(TAG, "We have received a system intent :" + screenState);

        Intent i = new Intent(context, ScreenService.class);
        i.putExtra("screen_state", screenState);
        context.startService(i);

    }

}
