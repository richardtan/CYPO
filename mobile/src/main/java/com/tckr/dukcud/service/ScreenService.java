package com.tckr.dukcud.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.tckr.dukcud.MainActivity;
import com.tckr.dukcud.R;
import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.data.DatabaseDAO;
import com.tckr.dukcud.data.DateTimeHandler;

/**
 * Handles all the services when you turn on or off your device
 */
public class ScreenService extends Service {

    private static final String TAG = "ScreenService";
    private static final int NOTIFICATION_ID = 1337;
    private static final String[] MANUFACTURE_FOR_KEEP_ALIVE = {"SAMSUNG"};

    // Due to bug in some samsung device, some of the action is called twice. So this static
    // variable is used to stop this. We will update this at the end of the call
    private static String lastScreenState = "";

    private BroadcastReceiver mReceiver;

    /**
     * We don't need this, Return null
     * @param intent
     * @return null
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Method used to start the foreground service when the service is called.
     */
    private void startTheForegroundService() {

        // Setup new database connection
        DatabaseDAO dao = new DatabaseDAO(this);
        dao.open();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("yesterday", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_icon_default)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_foregroud_service, dao.getCounterCountOn(DateTimeHandler.todayDate())))
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MIN).build();
        startForeground(NOTIFICATION_ID, notification);

        dao.close();
    }

    /**
     * Use to find if the foreground service has been enable
     * @return
     *      true if it has been set by the user
     *      true if it has not been set by the user but is part of the Manufacture list.
     *      false on anything else
     */
    public static boolean isForegroundServiceEnable(Context context) {

        DataSharedPreferencesDAO dspDAO = new DataSharedPreferencesDAO(context);

        boolean foregroundOn = dspDAO.getDataBoolean(DataSharedPreferencesDAO.KEY_FOREGROUND_SERVICE);
        boolean foregroundBeenSet = dspDAO.getDataBoolean(DataSharedPreferencesDAO.KEY_FOREGROUND_SERVICE_SET_ONCE);

        if (foregroundOn) {
            return true;
        } else {
            if (!foregroundBeenSet) {
                for (String s : MANUFACTURE_FOR_KEEP_ALIVE) {
                    if(android.os.Build.MANUFACTURER.toUpperCase().equals(s)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        // Check if the battery is charging
        this.checkBatteryIsCharging();

        Log.v("TAG", "onCreate() method has finished executing");

    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroy Service");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * This is the service that will be run over and over again. It will handle
     * the event when the screen has been turn off, on or the device has been shutdown.
     * @param intent
     * @param flags
     * @param startId
     * @return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v("TAG", "onStartCommand() method is starting...");

        // Setup new database connection
        DatabaseDAO dao = new DatabaseDAO(this);
        dao.open();

        // Open a SharedPref object
        DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(this);

        // Set foreground service
        boolean enableForeground = ScreenService.isForegroundServiceEnable(this);
        if (enableForeground) {
            this.startTheForegroundService();
        }

        // Get the intent from the broadcast receiver
        String screenState = null;
        try {
            screenState = intent.getStringExtra("screen_state");
        } catch (Exception e) {
            Log.e(TAG, "Screen State Exception was caught: " + e);
        }

        // If screenState is null then set it to some value so we can use it without any exception
        if(screenState == null) {
            screenState = "NULL";
        }

        Log.v(TAG, "The screen state is: " + screenState);

        if(screenState.equals("ACTION_ON")) {

            // Increment the screen on counters
            int timezoneChange = dao.insertInToTZDReturnCount();
            dao.incrementCounterOnDefault();
            dao.incrementCounterOnToday(timezoneChange);
            dao.insertInToScreenLog(DatabaseDAO.SCREENLOG_INTENT_ON);
            sharedPreferencesDAO.incrementScreenOn();

        } else if(screenState.equals("ACTION_OFF")) {

            // Increment the screen off counters
            dao.insertInToScreenLog(DatabaseDAO.SCREENLOG_INTENT_OFF);
            dao.increaseCounterOnDefaultTime();
            dao.increaseCounterOnTodayTime(dao.insertInToTZDReturnCount());
            sharedPreferencesDAO.incrementScreenOff();

        } else if(screenState.equals("ACTION_SHUTDOWN")) {

            // Increment the screen shutdown counters
            dao.insertInToScreenLog(DatabaseDAO.SCREENLOG_INTENT_SHUTDOWN);
            dao.increaseCounterOnDefaultTime();
            dao.increaseCounterOnTodayTime(dao.insertInToTZDReturnCount());
            sharedPreferencesDAO.incrementScreenOff();
            sharedPreferencesDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_SUCCESS_SHUTDOWN, true);

        } else if(screenState.equals("ACTION_USER_PRESENT") && !screenState.equals(lastScreenState)) {

            // Increment the screen shutdown counters
            int timezoneChange = dao.insertInToTZDReturnCount();
            dao.incrementCounterULDefault();
            dao.incrementCounterULToday(timezoneChange);
            dao.insertInToScreenLog(DatabaseDAO.SCREENLOG_INTENT_USER_PRESENT);

        } else if(screenState.equals("ACTION_POWER_CONNECTED")) {

            sharedPreferencesDAO.putDataLong(DataSharedPreferencesDAO.KEY_LAST_CHARGE_TIME,
                    DateTimeHandler.todayTimestamp());

            // If the last screen log intent was a screen on we need to tell the system that we
            // ae now charging and add up the new total for the last charge screen on time.
            if (dao.isLastScreenIntentOn()) {
                sharedPreferencesDAO.incrementScreenOff();
            }

            // Insert the total time of on screen time from the last charge to the system for storage.
            dao.insertLastChargeSOT(
                    DateTimeHandler.getDateByTimeStampLongFormat(
                            sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_LAST_OFF_CHARGE_TIME)),
                    sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_TOTAL_TIME_RECORD));

            // Reset all the counters when power is connected
            sharedPreferencesDAO.resetCounterOnPowerOn();

        } else if(screenState.equals("ACTION_POWER_DISCONNECTED")) {

            sharedPreferencesDAO.putDataLong(DataSharedPreferencesDAO.KEY_LAST_OFF_CHARGE_TIME,
                    DateTimeHandler.todayTimestamp());

            // If the last screen log intent was a screen on we need to start counting the charging
            // screen on time
            if (dao.isLastScreenIntentOn()) {
                sharedPreferencesDAO.incrementScreenOn();
            }

        }

        /*
          NOTES

          When power is disconnected, we need to start counting the on screen time.

          1. Need to reset the counter. Counter will be stored in share pref?
          2. Check the last intent was an ON or an OFF, if it was an ON then we need to start counting.
             If not then we need to do nothing and just reset the counter. (Should reset counters when Charging happens).
             a. solution. set up three key value pairs, ontime, offtime and total time.
                in scenario above, set on time to the time the charger was disconnected.
                when screen is offtime, then set the offtime, offtime - ontime and then store in
                total time.
                Only run above if and only if lastOffChargeTime > lastChargeTime.
                When charger connected is received, then write total time to counter table and then
                reset offtime, ontime and total time to 0.
                Also consider system reboot as offtime.

                On on boot received, we need to check if we shutdown properly. The last event should
                be a shutdown event. If not then we basically need to reset the ontime and offtime to 0.
                Also when working out offtime we need to check that ontime is not 0. If you are do nothing.

         */

        // Close the database connection
        dao.close();

        // Set the new screenState
        lastScreenState = screenState;

        // If foreground not enable then remove the notification
        if (!enableForeground) {
            stopForeground(true);
        }

        // Will try to recreate if killed
        return START_STICKY;
    }

    /**
     * checkBatteryIsCharging()
     * This method will check if the battery is already on charge. This method will be called up
     * when the service is started. Because we have not register the broadcast receiver to the
     * service, we have not been able to listen for a charge event. We need to make sure that the
     * charge event matches to the status of the phone before shutdown or force reboot, and the
     * below method will sort that out.
     */
    private void checkBatteryIsCharging() {

        boolean charging = false;

        final Intent batteryIntent = getApplicationContext().registerReceiver(
                null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;

        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // Only available from Jelly Bean MR1
        boolean wiCharge = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wiCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }

        if (batteryCharge) charging = true;
        if (usbCharge) charging = true;
        if (acCharge) charging = true;
        if (wiCharge) charging = true;

        // Get the timestamp for the last charge times
        DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(getApplicationContext());
        long lastChargeTime = sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_LAST_CHARGE_TIME);
        long lastOffChargeTime = sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_LAST_OFF_CHARGE_TIME);

        // Now we got to check the latest
        if(charging) {

            // If the last action was the phone not charging then update the charge time to now as
            // we are now charging the phone
            if(lastOffChargeTime >= lastChargeTime) {
                sharedPreferencesDAO.putDataLong(DataSharedPreferencesDAO.KEY_LAST_CHARGE_TIME,
                        DateTimeHandler.todayTimestamp());

                DatabaseDAO dao = new DatabaseDAO(this);
                dao.open();

                // If the last screen log intent was a screen on we need to tell the system that we
                // are now charging and add up the new total for the last charge screen on time.
                if (dao.isLastScreenIntentOn()) {
                    sharedPreferencesDAO.incrementScreenOff();
                }

                // Insert the total time of on screen time from the last charge to the system for storage.
                dao.insertLastChargeSOT(
                        DateTimeHandler.getDateByTimeStampLongFormat(
                                sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_LAST_OFF_CHARGE_TIME)),
                        sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_TOTAL_TIME_RECORD));

                dao.close();

                // Reset all the counters when power is connected
                sharedPreferencesDAO.resetCounterOnPowerOn();
            }

        } else {

            // If the last action was the phone charging then we need to tell the application that
            // you are no longer charging
            if(lastChargeTime >= lastOffChargeTime) {
                sharedPreferencesDAO.putDataLong(DataSharedPreferencesDAO.KEY_LAST_OFF_CHARGE_TIME,
                        DateTimeHandler.todayTimestamp());

                DatabaseDAO dao = new DatabaseDAO(this);
                dao.open();

                // If the last screen log intent was a screen on we need to start counting the charging
                // screen on time
                if (dao.isLastScreenIntentOn()) {
                    sharedPreferencesDAO.incrementScreenOn();
                }

                dao.close();
            }

        }

        Log.v(TAG, "checkBatteryIsCharging() has finished: Are you charging? " + charging);

    }
}
