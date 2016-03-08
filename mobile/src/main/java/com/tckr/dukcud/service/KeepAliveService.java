package com.tckr.dukcud.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.tckr.dukcud.KeepAliveActivity;
import com.tckr.dukcud.data.DataSharedPreferencesDAO;
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

        DataSharedPreferencesDAO sp = new DataSharedPreferencesDAO(this);

        // The below will try and start the Keep Alive Activity if it has never been run or it hasn't been called in the past
        // 6 hours. Also it will only run if the screen is turned off.
        // This is to solve SPMC issue with killing an activity after a day
        if (sp.getDataLong(DataSharedPreferencesDAO.KEY_LAST_ACTIVITY_START_SPMC) == 0 ||
                    sp.getDataLong(DataSharedPreferencesDAO.KEY_LAST_ACTIVITY_START_SPMC) <= DateTimeHandler.todayTimestamp()) {

            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

            // Only run if you are lollipop or later
            if (android.os.Build.VERSION.SDK_INT >= 21) {

                // If the screen is not on then lets start the activity and stop the activity
                if (!powerManager.isInteractive()) {

                    // start the KeepAliveActivity
                    startActivity(new Intent(this, KeepAliveActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                    // Set the time for for the next time to run 6 hours later
                    sp.putDataLong(DataSharedPreferencesDAO.KEY_LAST_ACTIVITY_START_SPMC, DateTimeHandler.todayTimestamp() + (1000*60*60*6));
                    Log.v(TAG, "Finished KeepAliveActivity.class starting and stopping");
                }
            }
        }


        // Restart Service
        Intent serviceIntent = new Intent(this, ScreenService.class);
        this.startService(serviceIntent);

        // Call this service again in 5 seconds
        KeepAliveService.startAlarmForKeepAlive(5, this);

        Log.v(TAG, "Finished restarting ScreenService.class");

        return START_NOT_STICKY;
    }

    /**
     * startAlarmForKeepAlive
     * This method will create an Alarm that will start the keep alive service
     * This will only create the Alarm if you are part of a manufacture that tries and kill the
     * service.
     * Also apps running on API 21 (Lollipop) and later will be affected.
     * @param seconds - specify the amount of second elapse to when you want the alarm to trigger
     */
    public static void startAlarmForKeepAlive(int seconds, Context context) {

        // If you are part of a manufacture that needs to have the service kept alive then identify it
        boolean isKeepAlive = true;

        // Only do the keep alive if you are on Lollipop and later
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            for (String s : MANUFACTURE_FOR_KEEP_ALIVE) {
                if(android.os.Build.MANUFACTURER.toUpperCase().equals(s)) {
                    isKeepAlive = true;
                    break;
                }
            }
        }

        if (false) {
            Intent notificationIntent = new Intent(context, KeepAliveReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, DateTimeHandler.todayTimestamp() + (1000 * seconds), pendingIntent);
        }
    }
}
