package com.tckr.dukcud.service;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Setting up the receiver for the notifications.
 */
public class NotificationReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Starting service NotificationService: " + SystemClock.elapsedRealtime());
        Intent notificationService = new Intent(context, NotificationService.class);

        // Starting wakeful service
        startWakefulService(context, notificationService);
    }
}
