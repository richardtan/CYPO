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
    private static final String[] MANUFACTURE_FOR_KEEP_ALIVE = {"SAMSUNG"};

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Call this service again in 10 seconds
        KeepAliveService.startAlarmForKeepAlive(5, this);

        // Restart Service
        Intent serviceIntent = new Intent(this, ScreenService.class);
        this.startService(serviceIntent);

        Log.v(TAG, "Finished restarting ScreenService.class");

        return START_NOT_STICKY;
    }

    /**
     * startAlarmForKeepAlive
     * This method will create an Alarm that will start the keep alive service
     * This will only create the Alarm if you are part of a manufacture that tries and kill the
     * service.
     * @param seconds - specify the amount of second elapse to when you want the alarm to trigger
     */
    public static void startAlarmForKeepAlive(int seconds, Context context) {

        // If you are part of a manufacture that needs to have the service kept alive then identify it
        boolean isKeepAlive = false;
        for (String s : MANUFACTURE_FOR_KEEP_ALIVE) {
            if(android.os.Build.MANUFACTURER.toUpperCase().equals(s)) {
                isKeepAlive = true;
                break;
            }
        }

        if (isKeepAlive) {
            Intent notificationIntent = new Intent(context, KeepAliveReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, DateTimeHandler.todayTimestamp() + (1000 * seconds), pendingIntent);
        }
    }
}
