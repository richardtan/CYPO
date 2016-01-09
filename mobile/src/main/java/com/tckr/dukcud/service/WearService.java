package com.tckr.dukcud.service;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.tckr.dukcud.MainActivity;
import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.data.DatabaseDAO;
import com.tckr.dukcud.data.SendDataToWear;

/**
 * This class handles all the connection to the wearable device.
 */
public class WearService extends WearableListenerService {

    public static final String TAG = "WearableListenerService";

    private static final String MESSAGE_PATH_DATA = "/cypo_wear_data";
    private static final String MESSAGE_PATH_OPEN_APP = "/cypo_wear_open_app";

    // Keys for the week.
    public static final String KEY_MON_COUNT = "monday_count";
    public static final String KEY_MON_DURATION = "monday_duration";
    public static final String KEY_MON_DATE = "monday_date";
    public static final String KEY_TUE_COUNT = "tuesday_count";
    public static final String KEY_TUE_DURATION = "tuesday_duration";
    public static final String KEY_TUE_DATE = "tuesday_date";
    public static final String KEY_WED_COUNT = "wednesday_count";
    public static final String KEY_WED_DURATION = "wednesday_duration";
    public static final String KEY_WED_DATE = "wednesday_date";
    public static final String KEY_THU_COUNT = "thursday_count";
    public static final String KEY_THU_DURATION = "thursday_duration";
    public static final String KEY_THU_DATE = "thursday_date";
    public static final String KEY_FRI_COUNT = "friday_count";
    public static final String KEY_FRI_DURATION = "friday_duration";
    public static final String KEY_FRI_DATE = "friday_date";
    public static final String KEY_SAT_COUNT = "saturday_count";
    public static final String KEY_SAT_DURATION = "saturday_duration";
    public static final String KEY_SAT_DATE = "saturday_date";
    public static final String KEY_SUN_COUNT = "sunday_count";
    public static final String KEY_SUN_DURATION = "sunday_duration";
    public static final String KEY_SUN_DATE = "sunday_date";

    // Default key to store data from the start of time
    public static final String KEY_DEFAULT_COUNT = "default_count";
    public static final String KEY_DEFAULT_DURATION = "default_duration";

    // Key store to store the device model and serial to be sent to the phone/tablet.
    public static final String KEY_BUILD_MODEL = "build_model";
    public static final String KEY_BUILD_SERIAL = "build_serial";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        // Get the path
        String path = messageEvent.getPath();

        if (path.equals(MESSAGE_PATH_DATA)) {

            // Syncing data
            dataSync(messageEvent);

        } else if (path.equals(MESSAGE_PATH_OPEN_APP)) {

            // Open phone app.
            openPhoneApp();
        }
    }

    /**
     * This method will handle the data coming from the wear device. It will then store it to our
     * database on the main applications.
     * @param messageEvent - message from wear device
     */
    private void dataSync(MessageEvent messageEvent) {

        // get the data from wear and put it in a DataMap object
        byte[] messageFromWear = messageEvent.getData();
        DataMap dataMap = DataMap.fromByteArray(messageFromWear);

        // Get a database object
        DatabaseDAO dao = new DatabaseDAO(this);
        dao.open();

        // find out if this wear device is current. If it is then start updating the system with the values
        boolean isCurrent = dao.isCurrentWearDevice(dataMap.getString(KEY_BUILD_MODEL), dataMap.getString(KEY_BUILD_SERIAL));

        // Monday's Data. Update if the date is != empty
        if (!dataMap.getString(KEY_MON_DATE).equals("")) {

            // Update the count
            dao.updateWearData(dataMap.getString(KEY_MON_DATE),
                    dataMap.getLong(KEY_MON_COUNT),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                    dataMap.getString(KEY_BUILD_SERIAL));

            // Update the duration
            dao.updateWearData(dataMap.getString(KEY_MON_DATE),
                    dataMap.getLong(KEY_MON_DURATION),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                    dataMap.getString(KEY_BUILD_SERIAL));
        }

        // Tuesday's Data. Update if the date is != empty
        if (!dataMap.getString(KEY_TUE_DATE).equals("")) {

            // Update the count
            dao.updateWearData(dataMap.getString(KEY_TUE_DATE),
                    dataMap.getLong(KEY_TUE_COUNT),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                    dataMap.getString(KEY_BUILD_SERIAL));

            // Update the duration
            dao.updateWearData(dataMap.getString(KEY_TUE_DATE),
                    dataMap.getLong(KEY_TUE_DURATION),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                    dataMap.getString(KEY_BUILD_SERIAL));
        }

        // Wednesday's Data. Update if the date is != empty
        if (!dataMap.getString(KEY_WED_DATE).equals("")) {

            // Update the count
            dao.updateWearData(dataMap.getString(KEY_WED_DATE),
                    dataMap.getLong(KEY_WED_COUNT),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                    dataMap.getString(KEY_BUILD_SERIAL));

            // Update the duration
            dao.updateWearData(dataMap.getString(KEY_WED_DATE),
                    dataMap.getLong(KEY_WED_DURATION),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                    dataMap.getString(KEY_BUILD_SERIAL));
        }

        // Thursday's Data. Update if the date is != empty
        if (!dataMap.getString(KEY_THU_DATE).equals("")) {

            // Update the count
            dao.updateWearData(dataMap.getString(KEY_THU_DATE),
                    dataMap.getLong(KEY_THU_COUNT),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                    dataMap.getString(KEY_BUILD_SERIAL));

            // Update the duration
            dao.updateWearData(dataMap.getString(KEY_THU_DATE),
                    dataMap.getLong(KEY_THU_DURATION),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                    dataMap.getString(KEY_BUILD_SERIAL));
        }

        // Friday's Data. Update if the date is != empty
        if (!dataMap.getString(KEY_FRI_DATE).equals("")) {

            // Update the count
            dao.updateWearData(dataMap.getString(KEY_FRI_DATE),
                    dataMap.getLong(KEY_FRI_COUNT),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                    dataMap.getString(KEY_BUILD_SERIAL));

            // Update the duration
            dao.updateWearData(dataMap.getString(KEY_FRI_DATE),
                    dataMap.getLong(KEY_FRI_DURATION),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                    dataMap.getString(KEY_BUILD_SERIAL));
        }

        // Saturday's Data. Update if the date is != empty
        if (!dataMap.getString(KEY_SAT_DATE).equals("")) {

            // Update the count
            dao.updateWearData(dataMap.getString(KEY_SAT_DATE),
                    dataMap.getLong(KEY_SAT_COUNT),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                    dataMap.getString(KEY_BUILD_SERIAL));

            // Update the duration
            dao.updateWearData(dataMap.getString(KEY_SAT_DATE),
                    dataMap.getLong(KEY_SAT_DURATION),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                    dataMap.getString(KEY_BUILD_SERIAL));
        }

        // Sunday's Data. Update if the date is != empty
        if (!dataMap.getString(KEY_SUN_DATE).equals("")) {

            // Update the count
            dao.updateWearData(dataMap.getString(KEY_SUN_DATE),
                    dataMap.getLong(KEY_SUN_COUNT),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                    dataMap.getString(KEY_BUILD_SERIAL));

            // Update the duration
            dao.updateWearData(dataMap.getString(KEY_SUN_DATE),
                    dataMap.getLong(KEY_SUN_DURATION),
                    DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                    dataMap.getString(KEY_BUILD_SERIAL));
        }

        // Update the count for the default
        boolean isCurrentDefaultCount = dao.updateWearData(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT,
                dataMap.getLong(KEY_DEFAULT_COUNT),
                DatabaseDAO.COUNTER_TYPE_WEAR_ON,
                dataMap.getString(KEY_BUILD_SERIAL));

        // Update the duration for the default
        boolean isCurrentDefaultDuration = dao.updateWearData(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT,
                dataMap.getLong(KEY_DEFAULT_DURATION),
                DatabaseDAO.COUNTER_TYPE_WEAR_ON_TIME,
                dataMap.getString(KEY_BUILD_SERIAL));

        // If you are the current device, update the last sync value.
        if (isCurrent) {
            dao.updateSettingsWearLastSync();
        }

        // If the default count or duration from Android wear is out of sync then we need
        // to use the data on the phone app and sync it.
        if (!isCurrentDefaultCount || !isCurrentDefaultDuration) {

            // Set up new object and add the serial number of the wear device we received the message
            // from. This means that we can target the device that should recieve this message
            SendDataToWear sendDataToWear = new SendDataToWear(this);
            sendDataToWear.addToDataMap(KEY_BUILD_SERIAL, dataMap.getString(KEY_BUILD_SERIAL));

            // If the default count needs to be updated then...
            if (!isCurrentDefaultCount) {
                sendDataToWear.addToDataMap(KEY_DEFAULT_COUNT,
                        dao.getCounterCountOnWear(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT));
            }

            // If the default duration needs to be updated then...
            if (!isCurrentDefaultDuration) {
                sendDataToWear.addToDataMap(KEY_DEFAULT_DURATION,
                        dao.getCounterCountOnTimeWear(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT));
            }

            // Send it to wear
            sendDataToWear.sendDataToWear();
        }

        //showToast("CYPO received data from Wear from " + dataMap.getString(KEY_BUILD_MODEL) +
        //        " with serial " + dataMap.getString(KEY_BUILD_SERIAL) +
        //        " and are you default: " + isCurrent);

        // The below code is used to send the wear card priority back to the android wear app.
        // When the user changes the priority of the card using WearManagementActivityFragment
        // the app does submit this back to android wear. But if the wear device is offline then
        // the wear device will not get the update. The decision is taken to keep sending the data
        // back to the wear device as it is easier...

        // Get the value stored on the system. If it is blank then don't do antyhing else sending it to wear
        String defaultCardPriority = new DataSharedPreferencesDAO(this).getDataString(DataSharedPreferencesDAO.KEY_WEAR_CARD_PRIORITY);
        if (!"".equals(defaultCardPriority)) {
            SendDataToWear sendDataToWear = new SendDataToWear(this);
            sendDataToWear.addToDataMap(DataSharedPreferencesDAO.KEY_WEAR_CARD_PRIORITY,
                    defaultCardPriority);
            sendDataToWear.sendDataToWearCardPriority();
        }

        dao.close();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * This is an incoming event from the wear device that will open up the app on on the phone
     */
    private void openPhoneApp() {

        // Start Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
