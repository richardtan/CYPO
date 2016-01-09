package com.tckr.dukcud.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Used as an interface to get and store key value pairs, mostly for settings
 */
public class DataSharedPreferencesDAO {

    private static final String TAG = "DataSharedPreferencesDAO";

    // Filename for the shared preferences
    private static final String FILENAME = "CypoDataStore";

    // Key that we have in our shared preferences
    public static final String KEY_LAST_CHARGE_TIME = "LastChargeTime";
    public static final String KEY_LAST_OFF_CHARGE_TIME = "LastOffChargeTime";
    public static final String KEY_ON_TIME_RECORD = "OnTimeRecord";
    public static final String KEY_OFF_TIME_RECORD = "OffTimeRecord";
    public static final String KEY_TOTAL_TIME_RECORD = "TotalTimeRecord";

    public static final String KEY_SUCCESS_SHUTDOWN = "SuccessShutdown";
    public static final String KEY_NEW_WEAR_DEVICE = "NewWearDevice";

    public static final String KEY_WEAR_CARD_PRIORITY = "WearCardPriority";

    public static final String KEY_WELCOME_COMPLETE = "WelcomeComplete";
    public static final String KEY_DEBUG_MODE = "DebugMode";

    // Create a global variable for the share preference
    private SharedPreferences sharedPreferences;

    /**
     * Constructor to set the share preference object.
     * @param context - the passed context
     */
    public DataSharedPreferencesDAO(Context context) {
        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * Get ethe boolean for the key
     * @param key - the key
     * @return - the store value
     */
    public boolean getDataBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Get the int for the key
     * @param key - the key
     * @return - the store value
     */
    public long getDataInt(String key){
        return sharedPreferences.getLong(key, 0);
    }

    /**
     * Get the long for the key
     * @param key - the key
     * @return - the store value
     */
    public long getDataLong(String key){
        return sharedPreferences.getLong(key, 0);
    }

    /**
     * Get the string data for the key
     * @param key - the key
     * @return - the store value
     */
    public String getDataString(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**
     * Insert the boolean value into the system
     * @param key - the key
     * @param value - the value to be stored
     * @return - true or false
     */
    public boolean putDataBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * Insert the int value into the system
     * @param key - the key
     * @param value - the value to be stored
     * @return - true or false
     */
    public boolean putDataInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * Insert the long values into the system
     * @param key - the key
     * @param value - the value to be stored
     * @return - true or false
     */
    public boolean putDataLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * Insert string values into the system
     * @param key - the key
     * @param value - the value to be stored
     * @return - true or false
     */
    public boolean putDataString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * resetCounterOnPowerOn()
     * This method is used when power is connected. If it is connected then you will reset the
     * counter for KEY_ON_TIME_RECORD, KEY_OFF_TIME_RECORD and KEY_TOTAL_TIME_RECORD
     */
    public void resetCounterOnPowerOn() {

        this.putDataLong(KEY_ON_TIME_RECORD, 0);
        this.putDataLong(KEY_OFF_TIME_RECORD, 0);
        this.putDataLong(KEY_TOTAL_TIME_RECORD, 0);

    }

    /**
     * resetAfterHardReBoot()
     * Method to reset the on and off time due to an improper reboot to the system.
     */
    public void resetAfterHardReBoot() {
        this.putDataLong(KEY_ON_TIME_RECORD, 0);
        this.putDataLong(KEY_OFF_TIME_RECORD, 0);
        this.putDataLong(KEY_TOTAL_TIME_RECORD, 0);
    }

    /**
     * incrementScreenOn()
     * Use to track the time when the screen was turned on when you are off the charger
     * When successful then:
     *      KEY_ON_TIME_RECORD = now
     *      KEY_OFF_TIME_RECORD = 0
     */
    public void incrementScreenOn() {

        // Only increment if you are charging, if you are not do not increment.
        if(!this.isCharging()) {
            long onTime = this.getDataLong(KEY_ON_TIME_RECORD);

            if (onTime == 0) {

                // Set the new onTime, and reset the offTime
                this.putDataLong(KEY_ON_TIME_RECORD, DateTimeHandler.todayTimestamp());
                this.putDataLong(KEY_OFF_TIME_RECORD, 0);
            }
        }

    }

    /**
     * incrementScreenOff()
     * Use to track the time when the screen was turned off when you are off the charger
     * When successful then:
     *      KEY_ON_TIME_RECORD = 0
     *      KEY_OFF_TIME_RECORD = now
     *      KEY_TOTAL_TIME_RECORD = addition to off - on time.
     */
    public void incrementScreenOff() {

        // Only increment if you are charging, if you are not do not increment.
        if(!this.isCharging()) {

            // Get the onTime for testing
            long onTime = this.getDataLong(KEY_ON_TIME_RECORD);

            // Only increment if onTime is not 0. We only get here if we have two off or two shutdown
            // intent together, which is very unlikely
            if(onTime != 0) {

                // Put the timestamp on KEY_OFF_TIME_RECORD
                this.putDataLong(KEY_OFF_TIME_RECORD, DateTimeHandler.todayTimestamp());

                // Then get the value of the off time
                long offTime = this.getDataLong(KEY_OFF_TIME_RECORD);

                // Get the total time.
                long totalTime = this.getDataLong(KEY_TOTAL_TIME_RECORD);

                // Get the difference of the offTime and onTime and add it to the total time
                this.putDataLong(KEY_TOTAL_TIME_RECORD, totalTime + (offTime - onTime));

                // Reset the onTime
                this.putDataLong(KEY_ON_TIME_RECORD, 0);
            }
        }
    }

    /**
     * isCharging()
     * Tells the system if your phone is charging
     * @return
     *      true if you are charging
     *      false if you are not charging.
     */
    public boolean isCharging() {

        // Get the values for the lastCharge and lastOffCharge
        long lastCharge = this.getDataLong(KEY_LAST_CHARGE_TIME);
        long lastOffCharge = this.getDataLong(KEY_LAST_OFF_CHARGE_TIME);

        // If last off charging time to greater than last charging, then you are off charge
        if (lastOffCharge > lastCharge) {
            return false;
        } else {
            return true;
        }

    }

}
