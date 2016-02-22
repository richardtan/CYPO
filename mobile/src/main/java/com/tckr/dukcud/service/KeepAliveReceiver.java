package com.tckr.dukcud.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Due to Samsung SPCM this Keep Alive Receiver will stop and restart the service
 */
public class KeepAliveReceiver extends BroadcastReceiver {

    private static final String TAG = "KeepAliveReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Starting service KeepAliveReceiver");
        Intent keepAliveService = new Intent(context, KeepAliveService.class);

        // Starting wakeful service
        context.startService(keepAliveService);

    }
}
