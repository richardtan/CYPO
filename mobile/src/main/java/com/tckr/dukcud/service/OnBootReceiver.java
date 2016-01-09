package com.tckr.dukcud.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.data.DateTimeHandler;

/**
 * Used to start the application if the device has been rebooted or if the application was updated.
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Start screen service
        Intent startServiceIntent = new Intent(context, ScreenService.class);

        // If this was received during a boot cycle then...
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(context);

            // Check to see if we have shutdown properly
            if (!sharedPreferencesDAO.getDataBoolean(DataSharedPreferencesDAO.KEY_SUCCESS_SHUTDOWN)) {

                // Reset the On and Off time for the charging status if we have not shutdown properly
                sharedPreferencesDAO.resetAfterHardReBoot();
            }

            // Reset shutdown preference
            sharedPreferencesDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_SUCCESS_SHUTDOWN, false);

            // Set the screen state to on by default.
            //startServiceIntent.putExtra("screen_state", "ACTION_ON");

            // TODO - Will probably never get to this but will need to really analyse this logic
            // There seems to be some issues with this, so for the time being I am setting this always reset hard!
            sharedPreferencesDAO.resetAfterHardReBoot();
        }

        context.startService(startServiceIntent);

        // Start notification receiver.
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, DateTimeHandler.getNotificationDate().getTimeInMillis(), pendingIntent);

    }
}
