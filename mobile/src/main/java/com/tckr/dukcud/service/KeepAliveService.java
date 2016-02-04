package com.tckr.dukcud.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tckr.dukcud.data.DateTimeHandler;

/**
 * Due to Samsung SPCM this Keep Alive Receiver will stop and restart the service
 */
public class KeepAliveService extends Service {

    private static final String TAG = "KeepAliveService";

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.v(TAG, "Create Service");
        super.onCreate();
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroy Service");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Stop the service
        Intent serviceIntent = new Intent(this, ScreenService.class);
        stopService(serviceIntent);

        // Call this service again in 10 minutes
        Intent notificationIntent = new Intent(this, KeepAliveReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent,0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, DateTimeHandler.todayTimestamp() + (1000 * 60 * 10), pendingIntent);

        // Restart Service
        this.startService(serviceIntent);

        return START_NOT_STICKY;
    }

}
