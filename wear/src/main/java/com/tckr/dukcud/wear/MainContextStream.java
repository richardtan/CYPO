package com.tckr.dukcud.wear;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.util.Log;

import com.tckr.dukcud.R;
import com.tckr.dukcud.wear.data.DataStore;
import com.tckr.dukcud.wear.data.MilliSecondsToHoursMinutesSeconds;
import com.tckr.dukcud.wear.service.LaunchPhoneAppService;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class has all the UI elements for the Card Stream for the app
 */
public class MainContextStream {

    Context context;
    private static final int NOTIFICATION_ID = 10101;
    private static final int PENDING_INTENT_REQ_CODE = 102;
    private static final String TAG = "wear.MainContextStream";

    public MainContextStream(Context context) {
        this.context = context;
    }

    /**
     * Used to remove the notification card from the watch, so you don't get a peak card if you are
     * having the card as a MAX position in the context stream, i.e. having the card at the top
     * of the context stream.
     */
    public void removeNotification() {

        NotificationManager nManager =
                (NotificationManager) context.getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(NOTIFICATION_ID);

    }

    /**
     * The main notification for the Android Wear context stream
     */
    public void startContextStream() {

        DataStore dataStore = new DataStore(context);

        // Get all the times from the data store to populate the cards
        long lastCheckTime = DataStore.todayTimestamp() - dataStore.getDataLong(DataStore.KEY_LAST_OFF);
        MilliSecondsToHoursMinutesSeconds m = new MilliSecondsToHoursMinutesSeconds(lastCheckTime);
        String lastCheckString = m.translateToReadableFormatLoose(context);

        // Get the priority for the card from the data store
        String notificationPriority = dataStore.getDataString(DataStore.KEY_WEAR_CARD_PRIORITY);
        int notificationPriorityNum = NotificationCompat.PRIORITY_DEFAULT;

        // Set the notification priority based on what is stored
        if (DataStore.KEY_WEAR_CARD_PRIORITY_TOP.equals(notificationPriority)) {
            notificationPriorityNum = NotificationCompat.PRIORITY_MAX;
        } else if (DataStore.KEY_WEAR_CARD_PRIORITY_BOTTOM.equals(notificationPriority)) {
            notificationPriorityNum = NotificationCompat.PRIORITY_MIN;
        } else if (DataStore.KEY_WEAR_CARD_PRIORITY_DEFAULT.equals(notificationPriority)) {
            notificationPriorityNum = NotificationCompat.PRIORITY_DEFAULT;
        }

        // Create a Notification builder
        Notification nb = new NotificationCompat.Builder(context)
                .setContentTitle(Html.fromHtml(MainContextStream.setColourBlue(lastCheckString)))
                .setContentText(Html.fromHtml(context.getString(R.string.last_check_watch, lastCheckString)))
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(notificationPriorityNum)
                .extend(addPagesToContextStream()) // Add some pages :)
                .build();

        // Create the notification manager
        NotificationManagerCompat nManager = NotificationManagerCompat.from(context);

        // Run the notification.
        nManager.notify(NOTIFICATION_ID, nb);

    }

    /**
     * This method is used to populate the other pages in the context stream
     * Page1 - shows today's stats so far
     * Page2 - shows the last 7 days
     * Page3 - shows the all time stats
     * Page4 - Action to open the app on the phone/tablet
     * @return Notification
     */
    public NotificationCompat.WearableExtender addPagesToContextStream() {

        DataStore dataStore = new DataStore(context);

        // Get today's data to populate on the card
        String[] todayKey = dataStore.getTodayKeys(0);
        MilliSecondsToHoursMinutesSeconds m = new MilliSecondsToHoursMinutesSeconds(
                dataStore.getDataLong(todayKey[DataStore.ARRAY_KEY_DURATION]));


        /* Page ONE */
        String page1Text = context.getString(R.string.page1_text,
                new DecimalFormat("#,###,###").format(dataStore.getDataLong(todayKey[DataStore.ARRAY_KEY_COUNT])),
                m.translateToReadableFormat(context));

        Notification page1 = new NotificationCompat.Builder(context)
                .setContentTitle(Html.fromHtml(MainContextStream.setColourBlue(context.getString(R.string.today___))))
                .setContentText(Html.fromHtml(page1Text))
                .setSmallIcon(R.drawable.ic_launcher)
                .build();


        /* Page TWO */
        String page2Text = "";

        // This will loop through the pass 7 days to get the data and print it out
        for(int i = 0; i <=6; i++) {
            try {
                String[] keys = dataStore.getTodayKeys(i);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                Date date = inputFormat.parse(dataStore.getDataString(keys[DataStore.ARRAY_KEY_DATE]));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE d MMM", Locale.UK);

                MilliSecondsToHoursMinutesSeconds m2 = new MilliSecondsToHoursMinutesSeconds(
                        dataStore.getDataLong(keys[DataStore.ARRAY_KEY_DURATION]));

                // If you are the first loop, do not add return carriages
                if (i != 0) page2Text += "<br /><br />";

                // Start populating the string
                page2Text += "<strong>" + simpleDateFormat.format(date) + "</strong><br />";
                page2Text += "<strong>" + context.getString(R.string.on) + ":</strong> " +
                        dataStore.getDataLong(keys[DataStore.ARRAY_KEY_COUNT]) + "<br />";
                page2Text += "<strong>" + context.getString(R.string.duration) + ":</strong> " +
                        m2.translateToReadableFormat(context);

            } catch (ParseException pe) {
                Log.e(TAG, "Error printing 7 days in page 2 text: " + pe);
            }
        }

        Notification page2 = new NotificationCompat.Builder(context)
                .setContentTitle(Html.fromHtml(MainContextStream.setColourBlue(context.getString(R.string.last_7_days))))
                .setContentText(Html.fromHtml(page2Text))
                .setSmallIcon(R.drawable.ic_launcher)
                .build();


        /* ALL TIME */
        MilliSecondsToHoursMinutesSeconds m3 = new MilliSecondsToHoursMinutesSeconds(
                dataStore.getDataLong(DataStore.KEY_DEFAULT_DURATION));

        String page3Text = context.getString(R.string.page3_text,
                new DecimalFormat("#,###,###").format(dataStore.getDataLong(DataStore.KEY_DEFAULT_COUNT)),
                m3.translateToReadableFormat(context));

        Notification page3 = new NotificationCompat.Builder(context)
                .setContentTitle(Html.fromHtml(MainContextStream.setColourBlue(context.getString(R.string.all_time))))
                .setContentText(Html.fromHtml(page3Text))
                .setSmallIcon(R.drawable.ic_launcher)
                .build();


        /* OPEN APP */
        // Create my intent to call a service
        Intent intent = new Intent(context, LaunchPhoneAppService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, PENDING_INTENT_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create my action
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.common_full_open_on_phone,
                context.getString(R.string.open_on_phone), pendingIntent)
                .build();

        // Build the notification
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        wearableExtender.setHintHideIcon(false);
        wearableExtender.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.cypo_wear_bg));
        wearableExtender.addPage(page1);
        wearableExtender.addPage(page2);
        wearableExtender.addPage(page3);
        wearableExtender.addAction(action);

        return wearableExtender;

    }

    /**
     * This method is a temporary fix.
     * I cannot find how to style the text in the notification, so I have decided to work around it
     * by using HTML
     * @param value - The string to encapsulate around
     * @return the string with the HTML <font color>value</font>
     */
    public static String setColourBlue(String value) {
        return "<font color=\"#303F9F\"><strong>" + value + "</strong></font>";
    }

}
