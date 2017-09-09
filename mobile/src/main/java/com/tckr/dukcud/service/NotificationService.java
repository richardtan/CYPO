package com.tckr.dukcud.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tckr.dukcud.MainActivity;
import com.tckr.dukcud.R;
import com.tckr.dukcud.data.CounterTable;
import com.tckr.dukcud.data.DatabaseDAO;
import com.tckr.dukcud.data.DateTimeHandler;

/**
 * Gets fired up when we are launching a new notification every day around 4AM.
 * Note: On marshmallow it will fire up as soon as the user turn on the phone. This is because of
 * Doze ZzZzZzZzZzZz
 */
public class NotificationService extends Service {

    private static final String TAG = "NotificationService";
    private static final int NOTIFICATION_ID = 1;
    private DatabaseDAO dao;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the database connection
        dao = new DatabaseDAO(this);
        dao.open();

        // cleanup
        dao.removeZeroValueCounterSOT();

        // Get the notification that we are going to return
        String[] notificationText = this.notificationText();

        // Close connection
        dao.close();

        // If the notification text is null, then don't show an notification
        if (notificationText != null) {

            // Create the Intent and then the pending intent for the notification so we have
            // somewhere to go once the user clicks on the notification
            Intent i = new Intent(this.getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("yesterday", true);

            // Pending intent
            PendingIntent pendingNotificationIntent =
                    PendingIntent.getActivity(this.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create a Notification builder
            Notification.Builder nb = new Notification.Builder(this.getApplicationContext())
                    .setContentTitle(notificationText[0])
                    .setContentText(notificationText[1])
                    .setSmallIcon(R.drawable.ic_stat_icon_default)
                    .setContentIntent(pendingNotificationIntent);

            // Set priority to LOW and create a big text for notification
            nb.setPriority(Notification.PRIORITY_LOW);
            nb.setStyle(new Notification.BigTextStyle()
                    .bigText(notificationText[1]));

            // Set visibility to public as it isn't data sensitive plus allows to be dismiss on lock screen API 21 above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nb.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            }

            // Set the notification and build
            Notification notification = nb.build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Create the notification manager
            NotificationManager nManager = (NotificationManager) this.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            // Run the notification.
            nManager.notify(NOTIFICATION_ID, notification);
        }

        // restart and destroy
        this.restartNotification();
        this.stopSelf();

        // release the wake lock
        NotificationReceiver.completeWakefulIntent(intent);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroy Service");
        super.onDestroy();
    }

    /**
     * This will restart the notification and set a new time for it, usually for the next day.
     */
    public void restartNotification() {

        Intent notificationIntent = new Intent(this.getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, notificationIntent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, DateTimeHandler.getNotificationDate().getTimeInMillis(), pendingIntent);

    }

    /**
     * This will be used to set the notification for the end user which will be presented to the
     * user each day.
     * @return a notification text that will be presented to the user
     *      Array Index 0 = The title of the Notification
     *      Array Index 1 = The text of the Notification
     */
    public String[] notificationText() {

        try {
            // Get the count from yesterday
            int count = dao.getCounterCountOn(DateTimeHandler.yesterdayDate());

            // Get all time count
            CounterTable iCount = dao.getInsightAllTimeCount(DatabaseDAO.COUNTER_TYPE_ON);

            // If the all time count matches the date yesterday, that means we have the most onscreen
            // time yesterday, so lets show that in the notification.
            if (iCount != null) {
                if (iCount.getDate() != null) {
                    if (iCount.getDate().equals(DateTimeHandler.yesterdayDate())) {
                        return new String[] {
                                getString(R.string.notification_all_time_record),
                                getString(R.string.notification_all_time_record_text, iCount.getCount())
                        };
                    }
                }
            }

            // If for some weird reason we get no one turning the device on then show the message below
            if (count <= 0) {
                return new String[] {
                        getString(R.string.sad_notification_title),
                        getString(R.string.sad_notification_text)
                };
            }

            // Return normal notification
            return new String[] {
                    getString(R.string.normal_notification_title, count),
                    getString(R.string.normal_notification_text)
            };

        } catch (Exception e) {
            Log.e(TAG, "notificationText() - Error when trying to create an notification: " + e);
            return null;
        }
    }

}
