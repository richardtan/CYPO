package com.tckr.dukcud.wear.service;

import android.os.Build;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.tckr.dukcud.wear.data.DataStore;

/**
 * This class will handle messages that is sent from the phone
 */
public class WearService extends WearableListenerService {

    public static final String TAG = "wear.WearService";
    private static final String MESSAGE_PATH_DATA = "/cypo_phone_data";
    private static final String MESSAGE_PATH_WEAR_CARD_PRIORITY = "/cypo_wear_card_priority";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        // Get the path
        String path = messageEvent.getPath();

        if (path.equals(MESSAGE_PATH_DATA)) {
            // Syncing data
            dataSync(messageEvent);
        } else if (path.equals(MESSAGE_PATH_WEAR_CARD_PRIORITY)) {
            setWearCardPosition(messageEvent);
        }
    }

    /**
     * dataSync
     * Used to write some data to the Watch storage
     * @param messageEvent - the data packet
     */
    private void dataSync(MessageEvent messageEvent) {

        // get the data from the phone and put it in a DataMap object
        byte[] messageFromPhone = messageEvent.getData();
        DataMap dataMap = DataMap.fromByteArray(messageFromPhone);

        // Get the build serial number and see if it matches the wear device
        // Remember, we can have more than 1 Android Wear device so we gotta check
        if (Build.SERIAL.equals(dataMap.getString(DataStore.KEY_BUILD_SERIAL))) {

            // get the values for the default count and duration from the Android Wear Device
            DataStore ds = new DataStore(this);
            long defaultCount = ds.getDataLong(DataStore.KEY_DEFAULT_COUNT);
            long defaultDuration = ds.getDataLong(DataStore.KEY_DEFAULT_DURATION);

            // If the value from the phone is greater than the default count on the wear device then...
            if (defaultCount < dataMap.getInt(DataStore.KEY_DEFAULT_COUNT)) {
                ds.putDataLong(DataStore.KEY_DEFAULT_COUNT, dataMap.getInt(DataStore.KEY_DEFAULT_COUNT));
            }

            // If the value from the phone is greater than the default duration on the wear device then...
            if (defaultDuration < dataMap.getLong(DataStore.KEY_DEFAULT_DURATION)) {
                ds.putDataLong(DataStore.KEY_DEFAULT_DURATION, dataMap.getLong(DataStore.KEY_DEFAULT_DURATION));
            }

        }
    }

    /**
     * Use to set the new position of the card on wear
     * @param messageEvent - the data packet
     */
    private void setWearCardPosition(MessageEvent messageEvent) {

        // get the data from the phone and put it in a DataMap object
        byte[] messageFromPhone = messageEvent.getData();
        DataMap dataMap = DataMap.fromByteArray(messageFromPhone);

        // Set the new position of the Android wear device
        DataStore ds = new DataStore(this);
        ds.putDataString(DataStore.KEY_WEAR_CARD_PRIORITY, dataMap.getString(DataStore.KEY_WEAR_CARD_PRIORITY));

    }

}
