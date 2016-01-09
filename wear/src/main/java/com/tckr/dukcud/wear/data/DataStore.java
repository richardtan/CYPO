package com.tckr.dukcud.wear.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class will provide all the interface for storing the data on the Wear app.
 * Note, we are using the UK Locale when doing date conversion. This is because I am
 * from the UK and I know it works on this Locale :)
 */
public class DataStore {

    private static final String TAG = "wear.DataStore";

    // Define the filename for the shared preference
    private static final String FILENAME = "WearStore";

    // This defines the key type of the string array. Gives the index for the elements.
    public static final int ARRAY_KEY_COUNT = 0;
    public static final int ARRAY_KEY_DURATION = 1;
    public static final int ARRAY_KEY_DATE = 2;

    // Default key to store data from the start of time
    public static final String KEY_DEFAULT_COUNT = "default_count";
    public static final String KEY_DEFAULT_DURATION = "default_duration";

    // Key to store the last time the wear device had it's screen switched off and on.
    public static final String KEY_LAST_OFF = "last_off";
    public static final String KEY_LAST_ON = "last_on";

    // Key store to store the device model and serial to be sent to the phone/tablet.
    public static final String KEY_BUILD_MODEL = "build_model";
    public static final String KEY_BUILD_SERIAL = "build_serial";

    // Key store to store the position of where the wear cards will be displayed
    public static final String KEY_WEAR_CARD_PRIORITY = "WearCardPriority";
    public static final String KEY_WEAR_CARD_PRIORITY_TOP = "Top";
    public static final String KEY_WEAR_CARD_PRIORITY_BOTTOM = "Bottom";
    public static final String KEY_WEAR_CARD_PRIORITY_DEFAULT = "Let Android decide";

    /*
        For reference, use CountDownTimer
        http://stackoverflow.com/questions/15435475/android-how-to-measure-an-elapsed-amount-of-time
     */

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

    // Create a global variable for the share preference
    private SharedPreferences sharedPreferences;

    /**
     * Constructor to set the share preference object.
     * @param context -
     */
    public DataStore(Context context) {
        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * Get the long for the key
     * @param key the value we want to get
     * @return the value from the key, 0 if does not exist
     */
    public long getDataLong(String key){
        return sharedPreferences.getLong(key, 0);
    }

    /**
     * Get the string data for the key
     * @param key the value we want to get
     * @return - the value from the key, blank if does not exist
     */
    public String getDataString(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**
     * This method will get the set of keys that will be used to access data today. We can offset
     * it by a day by passing a integer value where 1 unit equals 1 day in the pass.
     * @return Array where:
     *
     *      [0] = count
     *      [1] = duration
     *      [2] = date
     *
     */
    public String[] getTodayKeys(int offset) {

        try {

            // Set the current date
            Calendar currentTime = Calendar.getInstance();

            // Apply the offset
            currentTime.add(Calendar.DATE, - offset);

            // Get the day. 0 = Sunday ... 7 = Saturday
            int dayNumber = currentTime.get(Calendar.DAY_OF_WEEK);

            String day = "";

            // Get the days of the week. I did not use the simple date format as you never
            // know if you get weird output.
            switch (dayNumber) {
                case Calendar.SUNDAY:
                    day = "sunday";
                    break;
                case Calendar.MONDAY:
                    day = "monday";
                    break;
                case Calendar.TUESDAY:
                    day = "tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    day = "wednesday";
                    break;
                case Calendar.THURSDAY:
                    day = "thursday";
                    break;
                case Calendar.FRIDAY:
                    day = "friday";
                    break;
                case Calendar.SATURDAY:
                    day = "saturday";
                    break;
            }

            // Return null if no day is found. Else perform
            if (day.equals("")) {
                return null;
            } else {

                // Create the key to return
                String[] returnKey = {
                        day + "_count",
                        day + "_duration",
                        day + "_date"
                };

                return returnKey;
            }

        } catch (Exception e) {
            Log.e(TAG, "getTodayKeys() - Error has happened: " + e);
        }

        return null;
    }

    /**
     * Insert long values into the system
     * @param key -
     * @param value -
     * @return true if successful, false if not
     */
    public boolean putDataLong(String key, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * Insert string values into the system
     * @param key -
     * @param value -
     * @return true if successful, false if not
     */
    public boolean putDataString(String key, String value) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * This method will get the timestamp for today, at midnight
     * @return long timestamp at midnight
     */
    public static long timeStampAtMidnightToday() {

        // Set Calendar instance.
        Calendar timestampMidnight = Calendar.getInstance();

        // Set the date which excludes the current time to get the timestamp from midnight
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        try {
            timestampMidnight.setTime(dateFormat.parse(dateFormat.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestampMidnight.getTimeInMillis();
    }

    /**
     * Get today's date in YYYYMMDD format
     * @return today date in YYYYMMDD
     */
    public static String todayDate() {

        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        return sdf.format(currentTime.getTime());
    }

    /**
     * todayTimestamp()
     * @return today timestamp in long, the number of seconds since 1970-01-01 00:00:00 UTC
     */
    public static long todayTimestamp() {

        Calendar currentTime = Calendar.getInstance();
        return currentTime.getTimeInMillis();
    }

}
