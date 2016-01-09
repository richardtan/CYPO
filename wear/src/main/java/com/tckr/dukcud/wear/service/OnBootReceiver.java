package com.tckr.dukcud.wear.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Used to start the service to make sure we can monitor when the watch is being switch on and off
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Start screen service
        Intent startServiceIntent = new Intent(context, ScreenService.class);

        // If this was received during a boot cycle then...
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            startServiceIntent.putExtra("screen_state", "ACTION_ON");
        }

        context.startService(startServiceIntent);

    }
}
