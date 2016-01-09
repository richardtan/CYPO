package com.tckr.dukcud.wear.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tckr.dukcud.wear.data.SendDataToPhone;

/**
 * Used to send a message to the phone to open the app on the phone or connected device.
 */
public class LaunchPhoneAppService extends Service {

    public static final String TAG = "LaunchPhoneAppService";

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBIND");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "CREATE SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Send a message to the phone
        SendDataToPhone sendDataToPhone = new SendDataToPhone(this);
        sendDataToPhone.openPhoneApp();

        Log.v(TAG, "Finish service!");

        return START_NOT_STICKY;

    }

}
