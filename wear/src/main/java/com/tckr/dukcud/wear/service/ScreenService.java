package com.tckr.dukcud.wear.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.tckr.dukcud.wear.MainContextStream;
import com.tckr.dukcud.wear.data.DataStore;
import com.tckr.dukcud.wear.data.SendDataToPhone;

/**
 * Handles when we receive a screen action and what we should do with it
 */
public class ScreenService extends Service {

    private static final String TAG = "wear.ScreenService";
    public static long timestampOfLastSendDataToPhone = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when the service is first started
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Register receiver that handles screen on and screen off and shutdown logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        Log.v("TAG", "onCreate() method has finished executing");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v("TAG", "onStartCommand() method is starting...");

        // Create a DataStore object to start saving data.
        DataStore ds = new DataStore(this);

        // Get the intent from the broadcast receiver
        String screenState = null;
        try {
            screenState = intent.getStringExtra("screen_state");
        } catch (Exception e) {
            Log.e(TAG,"Screen State Exception was caught: " + e);
        }

        // If screenState is null then set it to some value so we can use it without any exception
        if(screenState == null) {
            screenState = "NULL";
        }

        // Get today date
        String today = DataStore.todayDate();

        // find today's keys
        String[] todayKey = ds.getTodayKeys(0);

        // Handle the state that we have been listening to.
        if(screenState.equals("ACTION_ON")) {

            /**************************
             * HANDLE TODAY ACTIVITIES
             **************************/

            // find and store the values that are currently in the system.
            String dateValueStored = ds.getDataString(todayKey[DataStore.ARRAY_KEY_DATE]);
            long countValueStored = ds.getDataLong(todayKey[DataStore.ARRAY_KEY_COUNT]);

            // If the value we are updating is today, then we can update, else we got to reset
            if(dateValueStored.equals(today)) {

                // Increment the count value for today by 1.
                ds.putDataLong(todayKey[DataStore.ARRAY_KEY_COUNT], countValueStored + 1);
            } else {

                // Reset the stored content on the device.
                ds.putDataString(todayKey[DataStore.ARRAY_KEY_DATE], today);
                ds.putDataLong(todayKey[DataStore.ARRAY_KEY_DURATION], 0);
                ds.putDataLong(todayKey[DataStore.ARRAY_KEY_COUNT], 1);
            }

            // Put in timestamp for last on.
            ds.putDataLong(DataStore.KEY_LAST_ON, DataStore.todayTimestamp());

            /*****************************
             * HANDLE ON GOING ACTIVITIES
             *****************************/

            // Increment the on going count by 1.
            long countDefaultStored = ds.getDataLong(DataStore.KEY_DEFAULT_COUNT);
            ds.putDataLong(DataStore.KEY_DEFAULT_COUNT, countDefaultStored + 1);

            // Start/Update Context Stream
            MainContextStream mainContextStream = new MainContextStream(this);
            mainContextStream.startContextStream();

            /*****************************
             * SEND DATA TO PHONE
             *****************************/

            // Check if we have sync the data to the phone. If we have not sent a message for the past
            // 5 minutes then lets send the data
            if ((DataStore.todayTimestamp() - 1000 * 60 * 5) > timestampOfLastSendDataToPhone) {
                SendDataToPhone sendDataToPhone = new SendDataToPhone(this);
                sendDataToPhone.sendDataToHandheld();

                // Set the new timestamp
                timestampOfLastSendDataToPhone = DataStore.todayTimestamp();
            }

        } else if(screenState.equals("ACTION_OFF") || screenState.equals("ACTION_SHUTDOWN")) {

            // Remove notification from the context stream
            MainContextStream mainContextStream = new MainContextStream(this);
            mainContextStream.removeNotification();

            // Get timestamp when device is turning off.
            long lastOffTimestamp = DataStore.todayTimestamp();

            // Get the timestamp of the last on
            long lastOnTimestamp = ds.getDataLong(DataStore.KEY_LAST_ON);

            /**************************
             * HANDLE TODAY ACTIVITIES
             **************************/

            // make sure we have a last screen on time. if not no point updating.......
            if (lastOnTimestamp != 0) {

                // Get the date that is stored for today, and the duration time.
                String dateValueStored = ds.getDataString(todayKey[DataStore.ARRAY_KEY_DATE]);
                long durationValueStored = ds.getDataLong(todayKey[DataStore.ARRAY_KEY_DURATION]);

                // If the date that we are doing to update is the same as the current date then we can just
                // update the values. If not then we have had our screen on passed midnight, so we have
                // some clean up work to do.
                if(dateValueStored.equals(today)) {

                    // Add the duration onto to the value.
                    ds.putDataLong(todayKey[DataStore.ARRAY_KEY_DURATION],
                            durationValueStored + (lastOffTimestamp - lastOnTimestamp));
                } else {

                    /**
                     * We are at this section because the device was detected to be turned off after midnight.
                     * We need to therefore increment the duration to both yesterday and today.
                     */

                    // Get the timestamp at midnight, plus the duration for yesterday.
                    long timestampAtMidnight = DataStore.timeStampAtMidnightToday();
                    String[] yesterdayKey = ds.getTodayKeys(1);
                    long yesterdayDurationValueStored = ds.getDataLong(yesterdayKey[DataStore.ARRAY_KEY_DURATION]);

                    // Add the milliseconds from last on time to midnight to yesterday's count
                    ds.putDataLong(yesterdayKey[DataStore.ARRAY_KEY_DURATION],
                            yesterdayDurationValueStored + (timestampAtMidnight - lastOnTimestamp));

                    // Add the milliseconds from midnight to last off time
                    ds.putDataLong(todayKey[DataStore.ARRAY_KEY_DURATION],
                            (lastOffTimestamp - timestampAtMidnight));

                    // Reset the counter for today.
                    ds.putDataString(todayKey[DataStore.ARRAY_KEY_DATE], today);
                    ds.putDataLong(todayKey[DataStore.ARRAY_KEY_COUNT], 0);
                }

            }

            /*****************************
             * HANDLE ON GOING ACTIVITIES
             *****************************/

            // make sure we have a last screen on time. if not no point updating.......
            if (lastOnTimestamp != 0) {

                // Increment the default duration by the amount of screen on time
                long durationDefaultStored = ds.getDataLong(DataStore.KEY_DEFAULT_DURATION);
                ds.putDataLong(DataStore.KEY_DEFAULT_DURATION,
                        durationDefaultStored + (lastOffTimestamp - lastOnTimestamp));

            }

            // Update the lastOffTimestamp
            ds.putDataLong(DataStore.KEY_LAST_OFF, lastOffTimestamp);

            // Reset Last On
            ds.putDataLong(DataStore.KEY_LAST_ON, 0);

        } else if(screenState.equals("ACTION_USER_PRESENT")) {

            // Currently not used.

        }

        // Will try to recreate if killed
        return START_STICKY;
    }

}
