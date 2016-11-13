package com.tckr.dukcud.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * DatabaseDAO
 * This will handle all queries to the Database
 * I will also attempt to write all queries using the API instead of RAW to avoid SQL injection
 */
public class DatabaseDAO {

    private static final String TAG = "DatabaseDAO";

    // Database fields
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    // Values for the Counter Table
    public static final String COUNTER = "counter";
    public static final String COUNTER_DATE = "date";
    public static final String COUNTER_COUNT = "count";
    public static final String COUNTER_TYPE = "type";
    public static final String COUNTER_MONTH = "month";
    public static final String COUNTER_YEAR_WEEK = "year_week";
    public static final String COUNTER_YEAR = "year";
    public static final String COUNTER_WEAR_SERIAL_NO = "wearSerialNo";
    public static final String COUNTER_TIMEZONE_CHANGE = "timezone_change";
    public static final String COUNTER_DATE_VALUE_DEFAULT = "default";
    public static final int COUNTER_TYPE_ON = 0;
    public static final int COUNTER_TYPE_USER_UNLOCK = 1;
    public static final int COUNTER_TYPE_ON_TIME = 2;
    public static final int COUNTER_TYPE_USER_UNLOCK_TIME = 3;
    public static final int COUNTER_TYPE_WEAR_ON = 4;
    public static final int COUNTER_TYPE_WEAR_ON_TIME = 5;
    public static final int COUNTER_TYPE_CHARGE_SOT = 6;

    // Values for the Settings Table
    public static final String SETTINGS = "settings";
    public static final String SETTINGS_NAME = "name";
    public static final String SETTINGS_DESCRIPTION = "description";
    public static final String SETTINGS_TEXT_VALUE = "text_value";
    public static final String SETTINGS_INT_VALUE = "int_value";

    // stores the timestamp of the last event the notification service has run up to on the screenlog table.
    public static final String SETTINGS_NAME_INSIGHT_LAST_RUN = "insight_last_run";
    public static final String SETTINGS_NAME_WEAR_LAST_SYNC = "wear_last_sync";

    // stores the settings for the wear devices received.
    public static final String SETTINGS_TEXT_VALUE_WEAR_DEVICE = "wear_device";
    public static final int SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW = 1;
    public static final int SETTINGS_INT_VALUE_WEAR_DEVICE_HIDE = 0;

    // Values for the ScreenLog Table
    public static final String SCREENLOG = "screenlog";
    public static final String SCREENLOG_DATE = "date";
    public static final String SCREENLOG_INTENT = "intent";
    public static final String SCREENLOG_TIMESTAMP = "timestamp";
    public static final String SCREENLOG_TIMEZONE = "timezone";
    public static final int SCREENLOG_INTENT_ON = 0;
    public static final int SCREENLOG_INTENT_OFF = 1;
    public static final int SCREENLOG_INTENT_SHUTDOWN = 2;
    public static final int SCREENLOG_INTENT_USER_PRESENT = 3;

    // Value for the TimeZoneDetector Table
    public static final String TZD = "tzd";
    public static final String TZD_DATE = "date";
    public static final String TZD_TIMEZONE = "timezone";

    // Setting up constants
    public static final String INSIGHT = "insight";
    public static final String INSIGHTARCH = "insightarc";
    public static final String INSIGHT_NAME = "name";
    public static final String INSIGHT_DISPLAY = "display";
    public static final String INSIGHT_DATE1 = "date1";
    public static final String INSIGHT_DATE2 = "date2";
    public static final String INSIGHT_DATE3 = "date3";
    public static final String INSIGHT_DATE4 = "date4";
    public static final String INSIGHT_DATETIME1 = "datetime1";
    public static final String INSIGHT_DATETIME2 = "datetime2";
    public static final String INSIGHT_DATETIME3 = "datetime3";
    public static final String INSIGHT_DATETIME4 = "datetime4";
    public static final String INSIGHT_FLOAT1 = "float1";
    public static final String INSIGHT_FLOAT2 = "float2";
    public static final String INSIGHT_FLOAT3 = "float3";
    public static final String INSIGHT_FLOAT4 = "float4";
    public static final String INSIGHT_INT1 = "int1";
    public static final String INSIGHT_INT2 = "int2";
    public static final String INSIGHT_INT3 = "int3";
    public static final String INSIGHT_INT4 = "int4";
    public static final String INSIGHT_TEXT1 = "text1";
    public static final String INSIGHT_TEXT2 = "text2";
    public static final String INSIGHT_TEXT3 = "text3";
    public static final String INSIGHT_TEXT4 = "text4";
    public static final String INSIGHT_TEXT5 = "text5";
    public static final String INSIGHT_TEXT6 = "text6";
    public static final String INSIGHT_TIMESTAMP1 = "timestamp1";
    public static final String INSIGHT_TIMESTAMP2 = "timestamp2";
    public static final String INSIGHT_TIMESTAMP3 = "timestamp3";
    public static final String INSIGHT_TIMESTAMP4 = "timestamp4";
    public static final String INSIGHT_ARCTIMESTAMP = "arctimestamp";

    // stores the count of the day that had the most screen on time
    public static final String INSIGHT_NAME_ALL_TIME_COUNT = "all_time_count";
    public static final String INSIGHT_NAME_ALL_TIME_DURATION = "all_time_duration";

    // stores the count of the most screen on time for all the weeks
    public static final String INSIGHT_NAME_WEEK_COUNT = "week_count";
    public static final String INSIGHT_NAME_WEEK_DURATION = "week_duration";

    // stores the count of the most screen on time for all the months
    public static final String INSIGHT_NAME_MONTH_COUNT = "month_count";
    public static final String INSIGHT_NAME_MONTH_DURATION = "month_duration";

    // Use to tell the system if we can display the insight. The insight will only be populated
    // if we have anything of interest to display to the end user. So by default it will be set
    // to NO
    public static final int INSIGHT_DISPLAY_OFF = 0;
    public static final int INSIGHT_DISPLAY_ON = 1;

    /**
     * My default constructor
     * @param context context
     */
    public DatabaseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    /**
     * Open db
     */
    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Close DB
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * increaseCounterOnDefaultTime
     * This method will keep a track of the duration of the screentime.
     */
    public void increaseCounterOnDefaultTime() {

        Cursor cursor = null;

        try {

            // SELECT count FROM counter
            // WHERE date = default AND type = on_time;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {COUNTER_DATE_VALUE_DEFAULT, COUNTER_TYPE_ON_TIME + ""},
                    null,
                    null,
                    null
            );

            // Get the total time stored which we will add to later
            cursor.moveToFirst();
            long totalTime = cursor.getLong(0);

            // SELECT date, intent, timestamp, timezone FROM screenlog
            // ORDER BY timestamp DESC;
            cursor = db.query(
                    SCREENLOG,
                    new String[] {SCREENLOG_DATE, SCREENLOG_INTENT, SCREENLOG_TIMESTAMP, SCREENLOG_TIMEZONE},
                    null,
                    null,
                    null,
                    null,
                    SCREENLOG_TIMESTAMP + " DESC"
            );

            // Set variables to get the off and on times
            long onTime = -1;
            long offTime = -1;

            // Iterate through the screenlog table to look for recent on/off/shutdown times
            while(cursor.moveToNext()) {

                // Check to see if we got both on and off time. If so then lets get out of the loop
                if (onTime > 0 && offTime > 0) {
                    break;
                } else {

                    // Start of our logic to work get the latest on, off or shutdown time
                    if (cursor.getInt(1) == SCREENLOG_INTENT_ON) {

                        // If we have on event then lets store the latest on time
                        onTime = cursor.getLong(2);

                    } else if (cursor.getInt(1) == SCREENLOG_INTENT_OFF) {

                        // If we already set the offTime then something has gone wrong and we should
                        // not update the duration
                        if (offTime > 0) break;

                        // If we have off event then lets store the latest on time
                        offTime = cursor.getLong(2);

                    } else if (cursor.getInt(1) == SCREENLOG_INTENT_SHUTDOWN) {

                        // If we already set the offTime then something has gone wrong and we should
                        // not update the duration
                        if (offTime > 0) break;

                        // If we have shutdown event then lets store the latest on time
                        offTime = cursor.getLong(2);
                    }
                }
            }

            // Set variable for duration time
            long duration = 0;

            // set the duration if we have values for off and on time. If the duration is
            // in the negative for some reason (should never happen), then reset the duration back to 0
            if (onTime > 0 && offTime > 0) {
                duration = offTime - onTime;

                if (duration < 0) {
                    duration = 0;
                }
            }

            // Add the duration time of the on and off to the total time.
            totalTime += duration;

            // Create a Content Value to update the table
            ContentValues cv = new ContentValues();
            cv.put(COUNTER_COUNT, totalTime);

            // UPDATE counter SET count = <totalTime>
            // WHERE date = default AND type = on_time;
            db.update(
                    COUNTER,
                    cv,
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {COUNTER_DATE_VALUE_DEFAULT, COUNTER_TYPE_ON_TIME + ""}
            );

        } catch (Exception e) {
            Log.e(TAG, "increaseCounterOnDefaultTime: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "increaseCounterOnDefaultTime: " + e);
            }
        }
    }

    /**
     * increaseCounterOnDefaultTime
     * This method will keep a track of the duration of the screen time for today
     */
    public void increaseCounterOnTodayTime(int timezoneChange) {

        Cursor cursor = null;

        try {

            // Get today's date
            String today = DateTimeHandler.todayDate();

            // SELECT count FROM counter
            // WHERE date = today AND type = on_time;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {today, COUNTER_TYPE_ON_TIME + ""},
                    null,
                    null,
                    null
            );

            // Find out if we have a row existing in the database for today. If not we got to
            // set it up.
            long totalTime = 0;
            if (cursor.moveToFirst()) {
                totalTime = cursor.getLong(0);
            }

            // Find out if we need to do some inserting. We know if the totalTime was not updated
            // if the value is still 0.
            boolean needInsert = true;
            if (totalTime != 0) {
                needInsert = false;
            }

            // SELECT date, intent, timestamp, timezone FROM screenlog
            // ORDER BY timestamp DESC;
            cursor = db.query(
                    SCREENLOG,
                    new String[] {SCREENLOG_DATE, SCREENLOG_INTENT, SCREENLOG_TIMESTAMP, SCREENLOG_TIMEZONE},
                    null,
                    null,
                    null,
                    null,
                    SCREENLOG_TIMESTAMP + " DESC"
            );

            // Set variables to get the off and on times
            long onTime = -1;
            long offTime = -1;

            // Iterate through the screenlog table to look for recent on/off/shutdown times
            while(cursor.moveToNext()) {

                // Check to see if we got both on and off time. If so then lets get out of the loop
                if (onTime > 0 && offTime > 0) {
                    break;
                } else {

                    // Start of our logic to work get the latest on, off or shutdown time
                    if (cursor.getInt(1) == SCREENLOG_INTENT_ON) {

                        // If we have on event then lets store the latest on time
                        onTime = cursor.getLong(2);

                    } else if (cursor.getInt(1) == SCREENLOG_INTENT_OFF) {

                        // If we already set the offTime then something has gone wrong and we should
                        // not update the duration
                        if (offTime > 0) break;

                        // If we have off event then lets store the latest on time
                        offTime = cursor.getLong(2);

                    } else if (cursor.getInt(1) == SCREENLOG_INTENT_SHUTDOWN) {

                        // If we already set the offTime then something has gone wrong and we should
                        // not update the duration
                        if (offTime > 0) break;

                        // If we have shutdown event then lets store the latest on time
                        offTime = cursor.getLong(2);
                    }
                }
            }

            // Set variable for duration time
            long duration = 0;

            // set the duration and if we have values for off and on time. If the duration is
            // in the negative for some reason (should never happen), then reset the duration back to 0
            if (onTime > 0 && offTime > 0) {
                duration = offTime - onTime;

                if (duration < 0) {
                    duration = 0;
                }
            }

            // We are checking if the onTime was yesterday. If the onTime was yesterday then
            // we need to add the time duration for yesterday up to midnight. Then after add
            // the rest to today. FYI this is a PAIN IN THE BACKSIDE TO DO!!!!
            if (onTime > 0 && duration != 0) {

                String onDate = DateTimeHandler.getDateByTimeStamp(onTime); // Get onDate

                if (!onDate.equals(today)) {

                    // Get the timestamp for midnight
                    long timestampMidnight = DateTimeHandler.getTimeStampAtMidnightToday();

                    // SELECT count FROM counter
                    // WHERE date = onDate AND type = on_time;
                    cursor = db.query(
                            COUNTER,
                            new String[] {COUNTER_COUNT},
                            COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                            new String[] {onDate, COUNTER_TYPE_ON_TIME + ""},
                            null,
                            null,
                            null
                    );

                    // Find out if we have a row existing in the database for onDate.
                    // If not we got to set it up.
                    long totalTimeOnDate = 0;
                    if (cursor.moveToFirst()) {
                        totalTimeOnDate = cursor.getLong(0);
                    }

                    // Find out if we need to do some inserting.
                    // We know if the totalTimeOnDate was not updated if the value is still 0.
                    boolean needInsertOnDate = true;
                    if (totalTimeOnDate != 0) {
                        needInsertOnDate = false;
                    }

                    // Work out milliseconds we need to insert.
                    totalTimeOnDate = totalTimeOnDate + (timestampMidnight - onTime);

                    // If we need to insert, we will insert a new record. Else we will just update
                    // Note we will only do an insert or an update if we have a totalTime greater than 0.
                    if (needInsertOnDate && totalTimeOnDate > 0) {

                        // Create a Content Value to insert into the table
                        ContentValues cv = new ContentValues();
                        cv.put(COUNTER_DATE, onDate);
                        cv.put(COUNTER_COUNT, totalTimeOnDate);
                        cv.put(COUNTER_TYPE, COUNTER_TYPE_ON_TIME);
                        cv.put(COUNTER_TIMEZONE_CHANGE, 0);
                        cv.put(COUNTER_MONTH, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(onDate)[1]));
                        cv.put(COUNTER_YEAR_WEEK, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(onDate)[8]));
                        cv.put(COUNTER_YEAR, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(onDate)[0]));

                        // INSERT INTO counter VALUE (onDate, totalTimeOnDate, on_time, 0, month, year_week, year);
                        db.insert(
                                COUNTER,
                                null,
                                cv
                        );

                    } else if (totalTimeOnDate > 0) {

                        /**
                         * Check if we have a timezone change onDate. We can tell if the timezoneChange
                         * variable provided to this method is greater than 1. If it is then we need
                         * to set this on the counter.
                         */

                        // Get the count of how many different timezone entries we have in the database
                        // for onDate
                        // SELECT count(*) FROM tzd
                        // WHERE date = today
                        cursor = db.query(
                                TZD,
                                new String[] {"count(*)"},
                                TZD_DATE + " = ?",
                                new String[] {onDate},
                                null,
                                null,
                                null
                        );

                        // Set the timezoneChangeOnDate
                        int timezoneChangeOnDate = 0;
                        if (cursor.moveToFirst()) {
                            timezoneChangeOnDate = cursor.getInt(0);
                        }

                        // Create a Content Value to update the table
                        ContentValues cv = new ContentValues();
                        cv.put(COUNTER_COUNT, totalTimeOnDate);
                        cv.put(COUNTER_TIMEZONE_CHANGE, timezoneChangeOnDate);

                        // UPDATE counter SET count = <totalTimeOnDate>, timezone_change = <timezoneChangeOnDate>
                        // WHERE date = onDate AND type = on_time;
                        db.update(
                                COUNTER,
                                cv,
                                COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                                new String[]{onDate, COUNTER_TYPE_ON_TIME + ""}
                        );
                    }

                    // Set the new duration
                    duration = duration - (timestampMidnight - onTime);
                }
            }

            // Add the duration time of the on and off to the total time.
            totalTime += duration;

            // If we need to insert, we will insert a new record. Else we will just update
            // Note we will only do an insert or an update if we have a totalTime greater than 0.
            if (needInsert && totalTime > 0) {

                // Create a Content Value to insert into the table
                ContentValues cv = new ContentValues();
                cv.put(COUNTER_DATE, today);
                cv.put(COUNTER_COUNT, totalTime);
                cv.put(COUNTER_TYPE, COUNTER_TYPE_ON_TIME);
                cv.put(COUNTER_TIMEZONE_CHANGE, 0);
                cv.put(COUNTER_MONTH, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[1]));
                cv.put(COUNTER_YEAR_WEEK, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[8]));
                cv.put(COUNTER_YEAR, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[0]));

                // INSERT INTO counter VALUE (today, count, on_time, 0, month, year_week, year);
                db.insert(
                        COUNTER,
                        null,
                        cv
                );

            } else if (totalTime > 0) {

                /**
                 * Check if we have a timezone change today. We can tell if the timezoneChange
                 * variable provided to this method is greater than 1. If it is then we need
                 * to set this on the counter.
                 */
                int counterTimezoneChange = 0;
                if (timezoneChange > 1) {
                    counterTimezoneChange = timezoneChange;
                }

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(COUNTER_COUNT, totalTime);
                cv.put(COUNTER_TIMEZONE_CHANGE, counterTimezoneChange);

                // UPDATE counter SET count = <totalTime>, , timezone_change = <counterTimezoneChange>
                // WHERE date = today AND type = on_time;
                db.update(
                        COUNTER,
                        cv,
                        COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                        new String[]{today, COUNTER_TYPE_ON_TIME + ""}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "increaseCounterOnTodayTime: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "increaseCounterOnTodayTime: " + e);
            }
        }
    }

    /**
     * incrementCounterOnDefault
     * This method will increment the count on the table counter where date = default
     */
    public void incrementCounterOnDefault() {

        Cursor cursor = null;

        try {

            // SELECT count FROM counter
            // WHERE date = default AND type = on;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {COUNTER_DATE_VALUE_DEFAULT, COUNTER_TYPE_ON + ""},
                    null,
                    null,
                    null
            );

            cursor.moveToFirst();

            // Increment the count by 1
            int count = cursor.getInt(0) + 1;

            // Create a Content Value to update the table
            ContentValues cv = new ContentValues();
            cv.put(COUNTER_COUNT, count);

            // UPDATE counter SET count = <count>
            // WHERE date = default AND type = on;
            db.update(
                    COUNTER,
                    cv,
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {COUNTER_DATE_VALUE_DEFAULT, COUNTER_TYPE_ON + ""}
            );

        } catch (Exception e) {
            Log.e(TAG, "incrementCounterOnDefault: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "incrementCounterOnDefault: " + e);
            }
        }
    }

    /**
     * incrementCounterOnToday
     * This method will increment the count on the table counter where date = today
     * Time will come from the Android System Time
     */
    public void incrementCounterOnToday(int timezoneChange) {

        Cursor cursor = null;

        try {

            // Get today's date
            String today = DateTimeHandler.todayDate();

            // SELECT count FROM counter
            // WHERE date = today AND type = on;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {today, COUNTER_TYPE_ON + ""},
                    null,
                    null,
                    null
            );

            // Set the default count to 1
            int count = 1;

            // If a row exist then increment the count by 1.
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0) + 1;
            }

            // If count == 1 then INSERT, Else UPDATE
            if (count == 1) {

                // Create a Content Value to insert into the table
                ContentValues cv = new ContentValues();
                cv.put(COUNTER_DATE, today);
                cv.put(COUNTER_COUNT, count);
                cv.put(COUNTER_TYPE, COUNTER_TYPE_ON);
                cv.put(COUNTER_TIMEZONE_CHANGE, 0);
                cv.put(COUNTER_MONTH, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[1]));
                cv.put(COUNTER_YEAR_WEEK, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[8]));
                cv.put(COUNTER_YEAR, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[0]));

                // INSERT INTO counter VALUE (today, count, on, 0, month, year_week, year);
                db.insert(
                        COUNTER,
                        null,
                        cv
                );

            } else {

                /**
                 * Check if we have a timezone change today. We can tell if the timezoneChange
                 * variable provided to this method is greater than 1. If it is then we need
                 * to set this on the counter.
                 */
                int counterTimezoneChange = 0;
                if (timezoneChange > 1) {
                    counterTimezoneChange = timezoneChange;
                }

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(COUNTER_COUNT, count);
                cv.put(COUNTER_TIMEZONE_CHANGE, counterTimezoneChange);

                // UPDATE counter SET count = <count>, timezone_change = <counterTimezoneChange>
                // WHERE date = default AND type = on;
                db.update(
                        COUNTER,
                        cv,
                        COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                        new String[] {today, COUNTER_TYPE_ON + ""}
                );

            }

        } catch (Exception e) {
            Log.e(TAG, "incrementCounterOnToday: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "incrementCounterOnToday: " + e);
            }
        }
    }

    /**
     * incrementCounterULDefault
     * This method will increment the count on the table counter where date = default
     * This is for when the user unlocks the device
     */
    public void incrementCounterULDefault() {

        Cursor cursor = null;

        try {

            // SELECT count FROM counter
            // WHERE date = default AND type = user unlock;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {COUNTER_DATE_VALUE_DEFAULT, COUNTER_TYPE_USER_UNLOCK + ""},
                    null,
                    null,
                    null
            );

            cursor.moveToFirst();

            // Increment the count by 1
            int count = cursor.getInt(0) + 1;

            // Create a Content Value to update the table
            ContentValues cv = new ContentValues();
            cv.put(COUNTER_COUNT, count);

            // UPDATE counter SET count = <count>
            // WHERE date = default AND type = user unlock;
            db.update(
                    COUNTER,
                    cv,
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {COUNTER_DATE_VALUE_DEFAULT, COUNTER_TYPE_USER_UNLOCK + ""}
            );

        } catch (Exception e) {
            Log.e(TAG, "incrementCounterULDefault: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "incrementCounterULDefault: " + e);
            }
        }
    }

    /**
     * incrementCounterULToday
     * This method will increment the count on the table counter where date = today and where type
     * is for User unlocking
     * Time will come from the Android System Time
     */
    public void incrementCounterULToday(int timezoneChange) {

        Cursor cursor = null;

        try {

            // Get today's date
            String today = DateTimeHandler.todayDate();

            // SELECT count FROM counter
            // WHERE date = today AND type = user unlock;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                    new String[] {today, COUNTER_TYPE_USER_UNLOCK + ""},
                    null,
                    null,
                    null
            );

            // Set the default count to 1
            int count = 1;

            // If a row exist then increment the count by 1.
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0) + 1;
            }

            // If count == 1 then INSERT, Else UPDATE
            if (count == 1) {

                // Create a Content Value to insert into the table
                ContentValues cv = new ContentValues();
                cv.put(COUNTER_DATE, today);
                cv.put(COUNTER_COUNT, count);
                cv.put(COUNTER_TYPE, COUNTER_TYPE_USER_UNLOCK);
                cv.put(COUNTER_TIMEZONE_CHANGE, 0);
                cv.put(COUNTER_MONTH, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[1]));
                cv.put(COUNTER_YEAR_WEEK, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[8]));
                cv.put(COUNTER_YEAR, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(today)[0]));

                // INSERT INTO counter VALUE (today, count, on, 0, month, year_week, year);
                db.insert(
                        COUNTER,
                        null,
                        cv
                );

            } else {

                /**
                 * Check if we have a timezone change today. We can tell if the timezoneChange
                 * variable provided to this method is greater than 1. If it is then we need
                 * to set this on the counter.
                 */
                int counterTimezoneChange = 0;
                if (timezoneChange > 1) {
                    counterTimezoneChange = timezoneChange;
                }

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(COUNTER_COUNT, count);
                cv.put(COUNTER_TIMEZONE_CHANGE, counterTimezoneChange);

                // UPDATE counter SET count = <count>, timezone_change = <counterTimezoneChange>
                // WHERE date = default AND type = on;
                db.update(
                        COUNTER,
                        cv,
                        COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ?",
                        new String[] {today, COUNTER_TYPE_USER_UNLOCK + ""}
                );

            }

        } catch (Exception e) {
            Log.e(TAG, "incrementCounterULToday: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "incrementCounterULToday: " + e);
            }
        }

    }

    /**
     * insertLastChargeSOT
     * This method is used to insert the time spent with the device on the last charge.
     * @param date - date to be inserted
     * @param timestamp - time to be inserted
     */
    public void insertLastChargeSOT(String date, long timestamp) {

        try {

            // Set content values with the screenlogIntent passed by the calling program
            // Create a Content Value to insert into the table
            ContentValues cv = new ContentValues();
            cv.put(COUNTER_DATE, date);
            cv.put(COUNTER_COUNT, timestamp);
            cv.put(COUNTER_TYPE, COUNTER_TYPE_CHARGE_SOT);
            cv.put(COUNTER_TIMEZONE_CHANGE, 0);
            cv.put(COUNTER_MONTH, 0);
            cv.put(COUNTER_YEAR_WEEK, 0);
            cv.put(COUNTER_YEAR, 0);


            // Insert into the database
            db.insert(COUNTER,
                    null,
                    cv
            );

        } catch(Exception e) {
            Log.e(TAG, "insertLastChargeSOT: " + e);
        }
    }

    /**
     * insertInToScreenLog(int screenlogIntent)
     * Will insert the values passed into the ScreenLog table with todays date.
     * @param screenlogIntent - The intent value
     */
    public void insertInToScreenLog(int screenlogIntent) {

        try {

            // Set content values with the screenlogIntent passed by the calling program
            ContentValues cv = new ContentValues();
            cv.put(SCREENLOG_DATE, DateTimeHandler.todayDateTime());
            cv.put(SCREENLOG_INTENT, screenlogIntent);
            cv.put(SCREENLOG_TIMESTAMP, DateTimeHandler.todayTimestamp());
            cv.put(SCREENLOG_TIMEZONE, DateTimeHandler.timezone());

            // Insert into the database
            db.insert(SCREENLOG,
                    null,
                    cv
            );

        } catch(Exception e) {
            Log.e(TAG, "insertInToScreenLog: " + e);
        }
    }

    /**
     * insertInToTZDReturnCount
     * This method is used to detect if we have had a timezone change when we are turning the
     * screen on. This is so we can prompt the user that the readings could be off a bit, or we
     * can use this a bit later when running the analytic service.
     *
     * @return
     * int value that will tell me the count of different timezone.
     */
    public int insertInToTZDReturnCount() {

        Cursor cursor = null;

        try {

            // Get today's date
            String today = DateTimeHandler.todayDate();
            String timezone = DateTimeHandler.timezone();

            // SELECT timezone FROM tzd
            // WHERE date = today AND timezone = timezone;
            cursor = db.query(
                    TZD,
                    new String[] {TZD_TIMEZONE},
                    TZD_DATE + " = ? AND " + TZD_TIMEZONE + " = ? ",
                    new String[] {today, timezone},
                    null,
                    null,
                    null
            );

            // This is the variable to store the content from the DB
            String databaseTimezone = "";

            // If a row exist then increment the count by 1.
            if (cursor.moveToFirst()) {
                databaseTimezone = cursor.getString(0);
            }

            // If the current timezone is found in the database then do not do anything, else
            // store this value in the database with the date
            if (!timezone.equals(databaseTimezone)) {

                // Create a Content Value to insert into the table
                ContentValues cv = new ContentValues();
                cv.put(TZD_DATE, today);
                cv.put(TZD_TIMEZONE, timezone);


                // INSERT INTO tzd VALUE (today, timezone);
                db.insert(
                        TZD,
                        null,
                        cv
                );

            }

            // Get the count of how many different timezone entries we have in the database
            // for today
            // SELECT count(*) FROM tzd
            // WHERE date = today
            cursor = db.query(
                    TZD,
                    new String[] {"count(*)"},
                    TZD_DATE + " = ?",
                    new String[] {today},
                    null,
                    null,
                    null
            );

            // Return int variable
            int returnValue = 0;

            if (cursor.moveToFirst()) {
                returnValue = cursor.getInt(0);
            }

            return returnValue;

        } catch (Exception e) {
            Log.e(TAG, "insertInToTZDReturnCount: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "insertInToTZDReturnCount: " + e);
            }
        }

        return 0;
    }

    /**
     * By passing the date, you will return the count of how many times your device has been
     * turned on for.
     * @param date in the format of YYYYMMDD
     * @return count
     */
    public int getCounterCountOn(String date) {

        Cursor cursor = null;
        int count = 0;

        try {

            // SELECT count FROM counter
            // WHERE date = <date> AND type = on;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ? ",
                    new String[] {date, COUNTER_TYPE_ON + ""},
                    null,
                    null,
                    null
            );

            // If database entry does exist, store the value, else do nothing
            if(cursor.moveToFirst()) {
                // Store the count and prepare to return
                count = cursor.getInt(0);
            }

        } catch(Exception e) {
            Log.e(TAG, "getCounterCountOn: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getCounterCountOn: " + e);
            }
        }
        return count;
    }

    /**
     * The method will return the number of times the wear device has been turned on for.
     * @param date in the format of YYYYMMDD
     * @return the number of times the device has been turned on for.
     */
    public int getCounterCountOnWear(String date) {

        Cursor cursor = null;
        int count = 0;

        try {

            // Get the wear devices connected to this application
            SettingsTable[] settingsTables = this.getWearDevices();
            String serialNo = null;

            if (settingsTables != null) {

                // loop thought the setting tables.
                for (SettingsTable i : settingsTables) {

                    // Check to make sure you are the current wear device
                    if (i.getInt_value() == SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW) {
                        serialNo = i.getName(); // Store the serial number of wear device
                        break;
                    }
                }
            }

            // If there is no wear devices that is defaulted then don't shown anything
            if (serialNo == null) {
                return 0;
            }

            // SELECT count FROM counter
            // WHERE date = <date> AND type = wear_on AND wearSerialNo = <serialNo>;
            cursor = db.query(
                    COUNTER,
                    new String[]{COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ? AND " + COUNTER_WEAR_SERIAL_NO + " = ?",
                    new String[]{date, COUNTER_TYPE_WEAR_ON + "", serialNo},
                    null,
                    null,
                    null
            );

            // If database entry does exist, store the value, else do nothing
            if (cursor.moveToFirst()) {
                // Store the count and prepare to return
                count = cursor.getInt(0);
            }

        } catch (Exception e) {
            Log.e(TAG, "getCounterCountOnWear: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getCounterCountOnWear: " + e);
            }
        }
        return count;
    }

    /**
     * Method to find out how long the device was turned on for this particular day.
     * @param date in the format of YYYYMMDD
     * @return timestamp of how long the device was turned on for that particular day
     */
    public long getCounterCountOnTime(String date) {

        Cursor cursor = null;
        long count = -1;

        try {

            // SELECT count FROM counter
            // WHERE date = <date> AND type = on_time;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ? ",
                    new String[] {date, COUNTER_TYPE_ON_TIME + ""},
                    null,
                    null,
                    null
            );

            // If database entry does exist, store the value, else do nothing
            if(cursor.moveToFirst()) {

                // Store the count attribute for the time
                count = cursor.getLong(0);

                // If the date is today, then work out since the last screen on time event how long the
                // device has been on for so we can add it to the count.
                if (date.equals(DateTimeHandler.todayDate())) {

                    // SELECT timestamp FROM screenlog
                    // WHERE intent = on ORDER BY timestamp DESC;
                    cursor = db.query(
                            SCREENLOG,
                            new String[] {SCREENLOG_TIMESTAMP},
                            SCREENLOG_INTENT + " = ?",
                            new String[] {SCREENLOG_INTENT_ON + ""},
                            null,
                            null,
                            SCREENLOG_TIMESTAMP + " DESC"
                    );

                    // If database entry does exist (more often then not it will) then get
                    // the timestamp now and then take it away from the last on screen time
                    // and then add it to the count.
                    if(cursor.moveToFirst()) {
                        // Store the count and prepare to return
                        count += DateTimeHandler.todayTimestamp() - cursor.getLong(0);
                    }
                }
            }

        } catch(Exception e) {
            Log.e(TAG, "getCounterCountOnTime: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getCounterCountOnTime: " + e);
            }
        }
        return count;
    }

    /**
     * This method will get the time on for wear devices
     * @param date in the format of YYYYMMDD
     * @return a timestamp of how long the device was turned on for
     */
    public long getCounterCountOnTimeWear(String date) {

        Cursor cursor = null;
        long count = 0;

        try {

            // Get the wear devices connected to this application
            SettingsTable[] settingsTables = this.getWearDevices();
            String serialNo = null;

            if (settingsTables != null) {

                // loop thought the setting tables.
                for (SettingsTable i : settingsTables) {

                    // Check to make sure you are the current wear device
                    if (i.getInt_value() == SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW) {
                        serialNo = i.getName(); // Store the serial number of wear device
                        break;
                    }
                }
            }

            // If there is no wear devices that is defaulted then don't shown anything
            if (serialNo == null) {
                return 0;
            }

            // SELECT count FROM counter
            // WHERE date = <date> AND type = wear_on_time AND wearSerialNo = <serialNo>;
            cursor = db.query(
                    COUNTER,
                    new String[]{COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ? AND " + COUNTER_WEAR_SERIAL_NO + " = ?",
                    new String[]{date, COUNTER_TYPE_WEAR_ON_TIME + "", serialNo},
                    null,
                    null,
                    null
            );

            // If database entry does exist, store the value, else do nothing
            if (cursor.moveToFirst()) {
                // Store the count and prepare to return
                count = cursor.getLong(0);
            }

        } catch (Exception e) {
            Log.e(TAG, "getCounterCountOnTimeWear: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getCounterCountOnTimeWear: " + e);
            }
        }

        return count;
    }

    /**
     * Method to return the count of the number of times a user has unlock his device.
     * @param date in the format of YYYYMMDD
     * @return count of how many times the user has unlocked the device.
     */
    public int getCounterCountUnlock(String date) {

        Cursor cursor = null;
        int count = -1;

        try {

            // SELECT count FROM counter
            // WHERE date = <date> AND type = user unlock;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ? ",
                    new String[] {date, COUNTER_TYPE_USER_UNLOCK + ""},
                    null,
                    null,
                    null
            );

            // If database entry does exist, store the value, else do nothing
            if(cursor.moveToFirst()) {
                // Store the count and prepare to return
                count = cursor.getInt(0);
            }

        } catch(Exception e) {
            Log.e(TAG, "getCounterCountUnlock: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getCounterCountUnlock: " + e);
            }
        }
        return count;
    }

    /**
     * getWearDevices()
     * This method will get all the wear devices that we have received a message from a wear device.
     * @return
     *      Array value of the settings table where text value = wear devices
     */
    public SettingsTable[] getWearDevices() {

        Cursor cursor = null;
        ArrayList<SettingsTable> result = new ArrayList<>();

        try {

            // SELECT name, description, text_value, int_value FROM settings
            // WHERE text_value = wear_device;
            cursor = db.query(
                    SETTINGS,
                    new String[] {SETTINGS_NAME, SETTINGS_DESCRIPTION, SETTINGS_TEXT_VALUE, SETTINGS_INT_VALUE},
                    SETTINGS_TEXT_VALUE + " = ?",
                    new String[] {SETTINGS_TEXT_VALUE_WEAR_DEVICE},
                    null,
                    null,
                    null
            );

            while(cursor.moveToNext()) {
                result.add(SettingsTable.mapSettingsTable(cursor, context));
            }

            return result.toArray(new SettingsTable[result.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getWearDevices: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getWearDevices: " + e);
            }
        }

        return null;
    }

    /**
     * getInsightAllTimeCount
     * Use to get data for the all time count.
     * @param
     *      type - Use the constants to select the type that you want
     * @return
     *      A single counter table object that can be used to get the value for data and count
     */
    public CounterTable getInsightAllTimeCount(int type) {
        Cursor cursor = null;

        try {

            // SELECT date, count, type, timezone_change, month, year_week, year FROM counter
            // WHERE type = <type> AND date <> 'default'
            // ORDER BY count DESC;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_DATE, COUNTER_COUNT, COUNTER_TYPE,
                            COUNTER_TIMEZONE_CHANGE, COUNTER_MONTH, COUNTER_YEAR_WEEK, COUNTER_YEAR},
                    COUNTER_TYPE + " = ? AND " + COUNTER_DATE + " <> ?",
                    new String[] {type + "", COUNTER_DATE_VALUE_DEFAULT},
                    null,
                    null,
                    COUNTER_COUNT + " DESC"
            );

            if(cursor.moveToFirst()) {
                return CounterTable.mapCounterTable(cursor);
            }


        } catch(Exception e) {
            Log.e(TAG, "getInsightAllTimeCount: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightAllTimeCount: " + e);
            }
        }

        return null;

    }

    /**
     * getInsightAllTimeCount
     * This will get the day and count where the device has been turned on the most.
     * @return
     *      INSIGHT_NAME: all_time_count
     *      INSIGHT_INT1: count of the amount of times the device was turned on for
     *      INSIGHT_DATE1: date of when the device was turned on the the most
     *      INSIGHT_INT2: count of the amount of times the device was unlocked most
     *      INSIGHT_DATE2: date of when the device was unlocked the most
     */
    @Deprecated
    public InsightTable getInsightAllTimeCount() {

        Cursor cursor = null;

        try {

            // SELECT name, int1, date1 FROM insight
            // WHERE name = all_time_count AND display = ON
            // ORDER BY date DESC;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_NAME, INSIGHT_INT1, INSIGHT_DATE1, INSIGHT_INT2, INSIGHT_DATE2},
                    INSIGHT_NAME + " = ? AND " + INSIGHT_DISPLAY + " = ?",
                    new String[] {INSIGHT_NAME_ALL_TIME_COUNT, INSIGHT_DISPLAY_ON + ""},
                    null,
                    null,
                    null
            );

            if(cursor.moveToFirst()) {
                return InsightTable.mapInsightTable(cursor, context);
            }

        } catch(Exception e) {
            Log.e(TAG, "getInsightAllTimeCount: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightAllTimeCount: " + e);
            }
        }

        return null;
    }

    /**
     * getInsightAllTimeCountTime
     * This will get the day and duration where the device has been turned on the most.
     * @return
     *      INSIGHT_NAME: all_time_count
     *      INSIGHT_INT1: count of the amount of times the device was turned on for
     *      INSIGHT_DATE1: date of when the device was turned on the the most
     */
    @Deprecated
    public InsightTable getInsightAllTimeCountTime() {

        Cursor cursor = null;

        try {

            // SELECT name, int1, date1 FROM insight
            // WHERE name = all_time_duration AND display = ON
            // ORDER BY date DESC;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_NAME, INSIGHT_INT1, INSIGHT_DATE1},
                    INSIGHT_NAME + " = ? AND " + INSIGHT_DISPLAY + " = ?",
                    new String[] {INSIGHT_NAME_ALL_TIME_DURATION, INSIGHT_DISPLAY_ON + ""},
                    null,
                    null,
                    null
            );

            if(cursor.moveToFirst()) {
                return InsightTable.mapInsightTable(cursor, context);
            }

        } catch(Exception e) {
            Log.e(TAG, "getInsightAllTimeCountTime: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightAllTimeCountTime: " + e);
            }
        }

        return null;
    }

    /**
     * This gets a data from the database, either by week of month which is then used to populate
     * the information to the user. It is kept generic so it can be used, but one case where it is
     * being used is by getting all count for a particular month. We can then sort descending and
     * when iterating through the results, we only return back the first result. This will give
     * us the row item where we had the most ON count for the month.
     * @param type the type of what we trying to find out, for example ON or OFF
     * @param year YYYY
     * @param month MM
     * @param yearWeek WW
     * @param orderBy ASC or DESC
     * @return counter table object
     */
    public CounterTable[] getInsightByTypeWeekOrMonth(int type, int year, int month, int yearWeek, String orderBy) {

        Cursor cursor = null;
        ArrayList<CounterTable> result = new ArrayList<>();

        // Set up selection and selection arguments for the type you want to query.
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(type + "");
        String selection = COUNTER_TYPE + " = ? ";

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (year != 0) {
            selection += "AND " + COUNTER_YEAR + " = ? ";
            selectionArgs.add(year + "");
        }

        // Test if we have a month parameter, if we do then add it to the selection and selection arguments
        if (month != 0) {
            selection += "AND " + COUNTER_MONTH + " = ? ";
            selectionArgs.add(month + "");
        }

        // Test if we have a yearWeek parameter, if we do then add it to the selection and selection arguments
        if (yearWeek != 0) {
            selection += "AND " + COUNTER_YEAR_WEEK + " = ? ";
            selectionArgs.add(yearWeek + "");
        }

        try {

            // SELECT date, count, type, timezone_change, month, year_week, year FROM counter
            // WHERE <selection> = <selectionArgs>
            // ORDER BY <orderBy>;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_DATE, COUNTER_COUNT, COUNTER_TYPE,
                            COUNTER_TIMEZONE_CHANGE, COUNTER_MONTH, COUNTER_YEAR_WEEK, COUNTER_YEAR},
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    null,
                    null,
                    orderBy
            );

            while(cursor.moveToNext()) {
                result.add(CounterTable.mapCounterTable(cursor));
            }

            return result.toArray(new CounterTable[result.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getInsightWeekOrMonth: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightWeekOrMonth: " + e);
            }
        }

        return null;

    }

    /**
     * getInsightMonthCountOn
     * This will get the data on the insight table for the insight month count.
     * Because we have to access the data in many different ways, this method has been setup to be
     * access in many different ways, so that I do not need to keep rebuilding this over and over
     * again to access queries which are slightly different. The below is what can be used for the
     * select query
     *
     * @param year
     *      If 0 is used, then year won't be used for the where claus
     * @param yearMonth
     *      If 0 is used, then yearMonth won't be used for the where claus
     * @param  orderBy
     *      If null is passed, then no order by.
     *      If you want to use it, you must specify the column name and either ASC or DESC
     * @return
     *      Array of items which will contain the definition of the insight for month_count:
     *       - INSIGHT_NAME: "mount_count" - yyyyMM
     *       - INSIGHT_TEXT1: "month_count" <- can be used to grab all items that relates to month count.
     *       - INSIGHT_INT1: number is to store the count value for this given attribute
     *       - INSIGHT_INT2: number is to store the unlock count value for this given attribute
     *       - INSIGHT_TIMESTAMP1: year in the format yyyy
     *       - INSIGHT_TIMESTAMP2: the year month combination in the format of yyyyMM
     *       - INSIGHT_DATE1: date use to store the related count value
     *       - INSIGHT_DATE2: date use to store the related unlock count value
     */
    public InsightTable[] getInsightMonthCountOn(int year, int yearMonth, String orderBy) {

        Cursor cursor = null;
        ArrayList<InsightTable> result = new ArrayList<>();

        // Set up selection and selection arguments for month count.
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(INSIGHT_NAME_MONTH_COUNT);
        String selection = INSIGHT_TEXT1 + " = ? ";

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (year != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP1 + " = ? ";
            selectionArgs.add(year + "");
        }

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (yearMonth != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP2 + " = ? ";
            selectionArgs.add(yearMonth + "");
        }

        try {

            // SELECT name, text1, int1, timestamp1, timestamp2, date1 FROM insight
            // WHERE <selection> = <selectionArgs>
            // ORDER BY <orderBy>;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_NAME, INSIGHT_TEXT1, INSIGHT_INT1, INSIGHT_INT2,
                            INSIGHT_TIMESTAMP1, INSIGHT_TIMESTAMP2, INSIGHT_DATE1, INSIGHT_DATE2},
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    null,
                    null,
                    orderBy
            );

            while(cursor.moveToNext()) {
                result.add(InsightTable.mapInsightTable(cursor, context));
            }

            return result.toArray(new InsightTable[result.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getInsightMonthCountOn: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightMonthCountOn: " + e);
            }
        }

        return null;
    }

    /**
     * getInsightMonthCountOnTime
     * This will get the data on the insight table for the insight month duration.
     * Because we have to access the data in many different ways, this method has been setup to be
     * access in many different ways, so that I do not need to keep rebuilding this over and over
     * again to access queries which are slightly different. The below is what can be used for the
     * select query
     *
     * @param year
     *      If 0 is used, then year won't be used for the where claus
     * @param yearMonth
     *      If 0 is used, then yearMonth won't be used for the where claus
     * @param  orderBy
     *      If null is passed, then no order by.
     *      If you want to use it, you must specify the column name and either ASC or DESC
     * @return
     *      Array of items which will contain the definition of the insight for month_count:
     *       - INSIGHT_NAME: "mount_duration" - yyyyMM
     *       - INSIGHT_TEXT1: "month_duration" <- can be used to grab all items that relates to month duration.
     *       - INSIGHT_INT1: number is to store the count value for this given attribute
     *       - INSIGHT_TIMESTAMP1: year in the format yyyy
     *       - INSIGHT_TIMESTAMP2: the year month combination in the format of yyyyMM
     *       - INSIGHT_DATE1: date use to store the related duration value
     */
    public InsightTable[] getInsightMonthCountOnTime(int year, int yearMonth, String orderBy) {

        Cursor cursor = null;
        ArrayList<InsightTable> result = new ArrayList<>();

        // Set up selection and selection arguments for month duration.
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(INSIGHT_NAME_MONTH_DURATION);
        String selection = INSIGHT_TEXT1 + " = ? ";

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (year != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP1 + " = ? ";
            selectionArgs.add(year + "");
        }

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (yearMonth != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP2 + " = ? ";
            selectionArgs.add(yearMonth + "");
        }

        try {

            // SELECT name, text1, int1, int2, timestamp1, timestamp2, date1, date2 FROM insight
            // WHERE <selection> = <selectionArgs>
            // ORDER BY <orderBy>;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_NAME, INSIGHT_TEXT1, INSIGHT_INT1,
                            INSIGHT_TIMESTAMP1, INSIGHT_TIMESTAMP2, INSIGHT_DATE1},
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    null,
                    null,
                    orderBy
            );

            while(cursor.moveToNext()) {
                result.add(InsightTable.mapInsightTable(cursor, context));
            }

            return result.toArray(new InsightTable[result.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getInsightMonthCountOnTime: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightMonthCountOnTime: " + e);
            }
        }

        return null;
    }

    /**
     * getInsightWeekCountOn
     * This will get the data on the insight table for the insight week count.
     * Because we have to access the data in many different ways, this method has been setup to be
     * access in many different ways, so that I do not need to keep rebuilding this over and over
     * again to access queries which are slightly different. The below is what can be used for the
     * select query
     *
     * @param year
     *      If 0 is used, then year won't be used for the where claus
     * @param yearWeek
     *      If 0 is used, then yearWeek won't be used for the where claus
     * @param  orderBy
     *      If null is passed, then no order by.
     *      If you want to use it, you must specify the column name and either ASC or DESC
     * @return
     *      Array of items which will contain the definition of the insight for week_count:
     *       - INSIGHT_NAME: "week_count" - yyyyMM
     *       - INSIGHT_TEXT1: "week_count" <- can be used to grab all items that relates to week count.
     *       - INSIGHT_INT1: number is to store the count value for this given attribute
     *       - INSIGHT_INT2: number is to store the unlock count value for this given attribute
     *       - INSIGHT_TIMESTAMP1: year in the format yyyy
     *       - INSIGHT_TIMESTAMP2: the year week combination in the format of yyyyMM
     *       - INSIGHT_DATE1: date use to store the related count value
     *       - INSIGHT_DATE2: date use to store the related unlock count value
     */
    public InsightTable[] getInsightWeekCountOn(int year, int yearWeek, String orderBy) {

        Cursor cursor = null;
        ArrayList<InsightTable> result = new ArrayList<>();

        // Set up selection and selection arguments for week count.
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(INSIGHT_NAME_WEEK_COUNT);
        String selection = INSIGHT_TEXT1 + " = ? ";

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (year != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP1 + " = ? ";
            selectionArgs.add(year + "");
        }

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (yearWeek != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP2 + " = ? ";
            selectionArgs.add(yearWeek + "");
        }

        try {

            // SELECT name, text1, int1, int2, timestamp1, timestamp2, date1, date2 FROM insight
            // WHERE <selection> = <selectionArgs>
            // ORDER BY <orderBy>;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_NAME, INSIGHT_TEXT1, INSIGHT_INT1, INSIGHT_INT2,
                            INSIGHT_TIMESTAMP1, INSIGHT_TIMESTAMP2, INSIGHT_DATE1, INSIGHT_DATE2},
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    null,
                    null,
                    orderBy
            );

            while(cursor.moveToNext()) {
                result.add(InsightTable.mapInsightTable(cursor, context));
            }

            return result.toArray(new InsightTable[result.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getInsightWeekCountOn: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightWeekCountOn: " + e);
            }
        }

        return null;
    }

    /**
     * getInsightWeekCountOnTime
     * This will get the data on the insight table for the insight week duration.
     * Because we have to access the data in many different ways, this method has been setup to be
     * access in many different ways, so that I do not need to keep rebuilding this over and over
     * again to access queries which are slightly different. The below is what can be used for the
     * select query
     *
     * @param year
     *      If 0 is used, then year won't be used for the where claus
     * @param yearWeek
     *      If 0 is used, then yearWeek won't be used for the where claus
     * @param  orderBy
     *      If null is passed, then no order by.
     *      If you want to use it, you must specify the column name and either ASC or DESC
     * @return
     *      Array of items which will contain the definition of the insight for month_count:
     *       - INSIGHT_NAME: "mount_week" - yyyyMM
     *       - INSIGHT_TEXT1: "month_week" <- can be used to grab all items that relates to week duration.
     *       - INSIGHT_INT1: number is to store the count value for this given attribute
     *       - INSIGHT_TIMESTAMP1: year in the format yyyy
     *       - INSIGHT_TIMESTAMP2: the year week combination in the format of yyyyMM
     *       - INSIGHT_DATE1: date use to store the related duration value
     */
    public InsightTable[] getInsightWeekCountOnTime(int year, int yearWeek, String orderBy) {

        Cursor cursor = null;
        ArrayList<InsightTable> result = new ArrayList<>();

        // Set up selection and selection arguments for week duration.
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(INSIGHT_NAME_WEEK_DURATION);
        String selection = INSIGHT_TEXT1 + " = ? ";

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (year != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP1 + " = ? ";
            selectionArgs.add(year + "");
        }

        // Test if we have a year parameter, if we do then add it to the selection and selection arguments
        if (yearWeek != 0) {
            selection += "AND " + INSIGHT_TIMESTAMP2 + " = ? ";
            selectionArgs.add(yearWeek + "");
        }

        try {

            // SELECT name, text1, int1, timestamp1, timestamp2, date1 FROM insight
            // WHERE <selection> = <selectionArgs>
            // ORDER BY <orderBy>;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_NAME, INSIGHT_TEXT1, INSIGHT_INT1,
                            INSIGHT_TIMESTAMP1, INSIGHT_TIMESTAMP2, INSIGHT_DATE1},
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    null,
                    null,
                    orderBy
            );

            while(cursor.moveToNext()) {
                result.add(InsightTable.mapInsightTable(cursor, context));
            }

            return result.toArray(new InsightTable[result.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getInsightWeekCountOnTime: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getInsightWeekCountOnTime: " + e);
            }
        }

        return null;
    }

    /**
     * This method will get the int_value from the settings table when the name is insight_last_run
     * @return -
     */
    @Deprecated
    public long getSettingsInsightLastRun() {

        Cursor cursor = null;
        int int_value = -1;

        try {

            // SELECT int_value FROM settings
            // WHERE name = insight last run;
            cursor = db.query(
                    SETTINGS,
                    new String[] {SETTINGS_INT_VALUE},
                    SETTINGS_NAME + " = ?",
                    new String[] {SETTINGS_NAME_INSIGHT_LAST_RUN},
                    null,
                    null,
                    null
            );

            // If database entry does exist, store the value, else do nothing
            if(cursor.moveToFirst()) {
                // Store the int_value and prepare to return
                int_value = cursor.getInt(0);
            }

        } catch(Exception e) {
            Log.e(TAG, "getSettingsInsightLastRun: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getSettingsInsightLastRun: " + e);
            }
        }
        return int_value;
    }

    /**
     * This method will get the list of entries in the screenlog table. It will exit when the
     * numOfRows is met.
     * @return count
     */
    public ScreenLogTable[] getScreenLog(int numOfRows) {

        Cursor cursor = null;
        ArrayList<ScreenLogTable> screenLogResult = new ArrayList<>();

        try {

            // SELECT date, intent FROM screenlog
            // ORDER BY date DESC;
            cursor = db.query(
                    SCREENLOG,
                    new String[] {SCREENLOG_DATE, SCREENLOG_INTENT, SCREENLOG_TIMESTAMP, SCREENLOG_TIMEZONE},
                    null,
                    null,
                    null,
                    null,
                    SCREENLOG_TIMESTAMP + " DESC"
            );

            // Iterate through the cursor and put the data in the Array List
            int count = 0;
            while(cursor.moveToNext()) {
                screenLogResult.add(ScreenLogTable.mapScreenLogTable(cursor, context));
                count++;

                // Exit when count == numOfRows
                if(count == numOfRows) {
                    break;
                }
            }

            return screenLogResult.toArray(new ScreenLogTable[screenLogResult.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getScreenLog: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getScreenLog: " + e);
            }
        }

        return null;
    }

    /**
     * Return the screenlog entries by a range timestamp search. Order by ASC
     * @return ScreenLogTable[]
     */
    @Deprecated
    public ScreenLogTable[] getScreenLogByRangeTimestamp(long fromTimestamp, long toTimestamp) {

        Cursor cursor = null;
        ArrayList<ScreenLogTable> screenLogResult = new ArrayList<ScreenLogTable>();

        try {

            // SELECT date, intent, timestamp, timezone FROM screenlog
            // WHERE timestamp >= fromTimestamp AND timestamp <= toTimestamp
            // ORDER BY date DESC;
            cursor = db.query(
                    SCREENLOG,
                    new String[] {SCREENLOG_DATE, SCREENLOG_INTENT, SCREENLOG_TIMESTAMP, SCREENLOG_TIMEZONE},
                    SCREENLOG_TIMESTAMP + " >= ? AND " + SCREENLOG_TIMESTAMP + " <= ?",
                    new String[] {fromTimestamp + "", toTimestamp + ""},
                    null,
                    null,
                    SCREENLOG_TIMESTAMP + " ASC"
            );

            // Iterate through the cursor and put the data in the Array List
            while(cursor.moveToNext()) {
                screenLogResult.add(ScreenLogTable.mapScreenLogTable(cursor, context));
            }

            return screenLogResult.toArray(new ScreenLogTable[screenLogResult.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getScreenLogByTimestamp: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getScreenLogByTimestamp: " + e);
            }
        }

        return null;
    }

    /**
     * Return the screenlog entry by the timestamp giving. Note, it is very unlikely but we could return
     * more than one value so we are going to return an array.
     * @return ScreenLogTable[]
     */
    @Deprecated
    public ScreenLogTable[] getScreenLogByTimestamp(long timestamp) {

        Cursor cursor = null;
        ArrayList<ScreenLogTable> screenLogResult = new ArrayList<ScreenLogTable>();

        try {

            // SELECT date, intent, timestamp, timezone FROM screenlog
            // WHERE timestamp = timestamp
            // ORDER BY date DESC;
            cursor = db.query(
                    SCREENLOG,
                    new String[] {SCREENLOG_DATE, SCREENLOG_INTENT, SCREENLOG_TIMESTAMP, SCREENLOG_TIMEZONE},
                    SCREENLOG_TIMESTAMP + " = ?",
                    new String[] {timestamp + ""},
                    null,
                    null,
                    null
            );

            // Iterate through the cursor and put the data in the Array List
            while(cursor.moveToNext()) {
                screenLogResult.add(ScreenLogTable.mapScreenLogTable(cursor, context));
            }

            return screenLogResult.toArray(new ScreenLogTable[screenLogResult.size()]);

        } catch(Exception e) {
            Log.e(TAG, "getScreenLogByTimestamp: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getScreenLogByTimestamp: " + e);
            }
        }

        return null;
    }

    /**
     * removeZeroValueCounterSOT
     * Especially when you are using wireless charging, you might sometime flood the counter
     * table with a lot of values because you might not be charging correctly. This means there
     * is a likely chance that we could just be entering pointless data into the system. This
     * method can be used to clean it up.
     */
    public void removeZeroValueCounterSOT() {

        try {

            // DELETE FROM counter
            // WHERE type = TYPE_CHARGE_SOT AND counter = 0
            db.delete(COUNTER,
                    COUNTER_TYPE + " = ? AND " + COUNTER_COUNT + " = 0",
                    new String[] {COUNTER_TYPE_CHARGE_SOT + ""}
            );

        } catch (Exception e) {
            Log.e(TAG, "removeZeroValueCounterSOT: " + e);
        }
    }

    /**
     * isCurrentWearDevice()
     * This will return back to the calling program if this device is to be used for CYPO.
     * From play service 7.3 we can have multiple wear devices connected to the application.
     * Currently the application can only handle one device logically. So we need to find out if
     * the device we just received a message from is the device that we should use. This will
     * change once an update is pushed to this application so currently this is temporary solution.
     *
     * If the device is not in the system, then it will insert the device. If this is the first
     * android device to be registered then it will default to be the device used for CYPO. If not
     * then it will default to a device to not show, plus set a message so that a card will be
     * shown to the end user that more than one device is available.
     *
     * @param model - the device model from Build.MODEL
     * @param serial - the device serial number from Build.SERIAL
     * @return
     *      true if this device should be used as the default
     *      false if this device should not be used as the default
     */
    public boolean isCurrentWearDevice(String model, String serial) {

        try {

            // Get the list of available android wear device that we have heard a prompt from.
            SettingsTable[] st = this.getWearDevices();

            boolean wearDeviceFound = false;
            boolean wearDeviceShow = false;
            boolean hasADeviceShow = false;

            // Below is to check if we have already received a message from the wear device that
            // was called to this method. If we have then find out if it is the current wear
            // device that will be shown on the phone app.
            // If no device is found, then find out if we have a device already that is set to a
            // default. The reason for this is so that we can store this new device to be the default
            // or not the default.
            if (st != null) {
                for (SettingsTable i : st) {

                    // Check if the name on settings table is equal to the serial number
                    // If found then set wearDeviceFound to true
                    if (i.getName().equals(serial)) {
                        wearDeviceFound = true;

                        // Now check if this device is to be used for CYPO
                        if (i.getInt_value() == SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW) {
                            wearDeviceShow = true;
                        }
                    }

                    // Check to see if we have any devices where the wear device is show
                    if (i.getInt_value() == SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW) {
                        hasADeviceShow = true;
                    }
                }
            }

            // If we found the device and it is the default, return true
            if (wearDeviceShow) {
                return true;
            }

            // If this device is new, then we need to store this new wear device.
            if (!wearDeviceFound) {

                // Create a Content Value to insert into the table
                ContentValues cv = new ContentValues();
                cv.put(SETTINGS_NAME, serial); // Add serial number
                cv.put(SETTINGS_DESCRIPTION, model); // Add model number
                cv.put(SETTINGS_TEXT_VALUE, SETTINGS_TEXT_VALUE_WEAR_DEVICE);
                cv.put(SETTINGS_INT_VALUE,
                        hasADeviceShow ? SETTINGS_INT_VALUE_WEAR_DEVICE_HIDE : SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW);
                // If we already have a device that is default, then set this new device as HIDE, else show it.

                // INSERT INTO settings VALUE (serial, model, wear_device, show/hide);
                db.insert(
                        SETTINGS,
                        null,
                        cv
                );

                // If we already have a device then return false, else return true as the new inserted
                // wear device inserted into the settings table is now the default.
                if (hasADeviceShow) {

                    // Alert the system that we have a new wear device that is not the default.
                    DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(context);
                    sharedPreferencesDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_NEW_WEAR_DEVICE, true);

                    return false;
                } else {
                    return true;
                }
            }


        } catch(Exception e) {
            Log.e(TAG, "isCurrentWearDevice(): " + e);
        }

        // Something has gone wrong.
        return false;
    }

    /**
     * isLastScreenIntentIntentOn()
     * Check to see if the last intent on the screenlog table was a screen on. In this case we
     * need to ignore the user present
     * @return
     *      true if the last intent on the screen log is ON
     *      false if the last intent was not a screen ON
     */
    public boolean isLastScreenIntentOn() {

        Cursor cursor = null;

        try {

            // SELECT date, intent, timestamp, timezone FROM screenlog
            // ORDER BY timestamp DESC;
            cursor = db.query(
                    SCREENLOG,
                    new String[] {SCREENLOG_DATE, SCREENLOG_INTENT, SCREENLOG_TIMESTAMP, SCREENLOG_TIMEZONE},
                    null,
                    null,
                    null,
                    null,
                    SCREENLOG_TIMESTAMP + " DESC"
            );

            // Loop to see if the last intent was a screen on. Ignore User present.
            while(cursor.moveToNext()) {

                // If you are a user present event then continue and check the next item on the loop
                if (cursor.getInt(1) == SCREENLOG_INTENT_USER_PRESENT) {
                    continue;
                }
                else if (cursor.getInt(1) == SCREENLOG_INTENT_ON) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch(Exception e) {
            Log.e(TAG, "isLastScreenIntentIntentOn(): " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "isLastScreenIntentIntentOn(): " + e);
            }
        }

        return false;
    }

    /**
     * Use to update the Insight Table with a new value for screen on if it is the record for your
     * device. If not then leave alone
     */
    @Deprecated
    public void setInsightAllTimeCountOn() {

        Cursor cursor = null;

        try {

            // Get the count form yesterday
            int countFromYesterday = this.getCounterCountOn(DateTimeHandler.yesterdayDate());

            // SELECT int1 FROM insight
            // WHERE name = all_time_count;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_INT1},
                    INSIGHT_NAME + " = ?",
                    new String[] {INSIGHT_NAME_ALL_TIME_COUNT},
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();

            // If yesterday count is greater than the count of all time, then update
            if(countFromYesterday > cursor.getInt(0)) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT1, countFromYesterday);
                cv.put(INSIGHT_DATE1, DateTimeHandler.yesterdayDate());
                cv.put(INSIGHT_DISPLAY, INSIGHT_DISPLAY_ON);

                // UPDATE insight SET int1 = <countFromYesterday>, date1 = <yesterdayDate>
                // WHERE name = all_time_count;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_ALL_TIME_COUNT}
                );
            }

        } catch (Exception e) {
            Log.v(TAG, "setNumberScreenOnAllTime: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "setNumberScreenOnAllTime: " + e);
            }
        }
    }

    /**
     * setInsightAllTimeCountUnlock
     * Use to update the Insight Table with a new value for screen unlock if it is the record for your
     * device. If not then leave alone.
     * Same as AllTimeCount, however use for unlocking the device. Value store in int2 and date2.
     */
    @Deprecated
    public void setInsightAllTimeCountUnlock() {

        Cursor cursor = null;

        try {

            // Get the count form yesterday
            int countFromYesterday = this.getCounterCountUnlock(DateTimeHandler.yesterdayDate());

            // SELECT int2 FROM insight
            // WHERE name = all_time_count;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_INT2},
                    INSIGHT_NAME + " = ?",
                    new String[] {INSIGHT_NAME_ALL_TIME_COUNT},
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();

            // If yesterday count is greater than the count of all time, then update
            if(countFromYesterday > cursor.getInt(0)) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT2, countFromYesterday);
                cv.put(INSIGHT_DATE2, DateTimeHandler.yesterdayDate());
                cv.put(INSIGHT_DISPLAY, INSIGHT_DISPLAY_ON);

                // UPDATE insight SET int2 = <countFromYesterday>, date2 = <yesterdayDate>
                // WHERE name = all_time_count;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_ALL_TIME_COUNT}
                );
            }

        } catch (Exception e) {
            Log.v(TAG, "setInsightAllTimeCountUnlock: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "setInsightAllTimeCountUnlock: " + e);
            }
        }
    }

    /**
     * Use to update the Insight Table with a new value for all time duration record if it has beat
     * the old record. If it beats the old record then WOOHOO and set it
     */
    @Deprecated
    public void setInsightAllTimeDuration() {

        Cursor cursor = null;

        try {

            // Get the duration form yesterday
            long durationFromYesterday = this.getCounterCountOnTime(DateTimeHandler.yesterdayDate());

            // SELECT int1 FROM insight
            // WHERE name = all_time_duration;
            cursor = db.query(
                    INSIGHT,
                    new String[] {INSIGHT_INT1},
                    INSIGHT_NAME + " = ?",
                    new String[] {INSIGHT_NAME_ALL_TIME_DURATION},
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();

            // If yesterday count is greater than the count of all time, then update
            if(durationFromYesterday > cursor.getInt(0)) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT1, durationFromYesterday);
                cv.put(INSIGHT_DATE1, DateTimeHandler.yesterdayDate());
                cv.put(INSIGHT_DISPLAY, INSIGHT_DISPLAY_ON);

                // UPDATE insight SET int1 = <countFromYesterday>, date1 = <yesterdayDate>
                // WHERE name = all_time_duration;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_ALL_TIME_DURATION}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "setInsightAllTimeDuration: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "setInsightAllTimeDuration: " + e);
            }
        }
    }

    /**
     * setInsightMonthCountOn
     * This method will be used to work out if yesterday had the most on screen on count for the
     * current month. If so update the insight table.
     */
    @Deprecated
    public void setInsightMonthCountOn() {

        // Set up months if we do not have anything set up
        this.setupInsightMonth();

        try {

            // Get the count form yesterday
            int countFromYesterday = this.getCounterCountOn(DateTimeHandler.yesterdayDate());

            // Get the data from insight table, we should only return 1 value, if we have more than one
            // then we need to exit.
            InsightTable[] it = this.getInsightMonthCountOn(0, DateTimeHandler.getYearMonth(-1), null);

            // If the length is 0 or greater than or equal to 2 then exit
            if (it.length == 0 || it.length >= 2) {
                return;
            }

            // If yesterday count is greater than the count of the current month, then update
            if (countFromYesterday > it[0].getInt1()) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT1, countFromYesterday);
                cv.put(INSIGHT_DATE1, DateTimeHandler.yesterdayDate());

                // UPDATE insight SET int1 = <countFromYesterday>, date1 = <yesterdayDate>
                // WHERE name = month_count-<yyyyMM>;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_MONTH_COUNT + "-" + DateTimeHandler.getYearMonth(-1)}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "setInsightMonthCountOn: " + e);
        }

    }

    /**
     * setInsightMonthCountUnlock
     * This method will be used to work out if yesterday had the most unlocked count for the
     * current month. If so update the insight table.
     */
    @Deprecated
    public void setInsightMonthCountUnlock() {

        // Set up months if we do not have anything set up
        this.setupInsightMonth();

        try {

            // Get the count form yesterday
            int countFromYesterday = this.getCounterCountUnlock(DateTimeHandler.yesterdayDate());

            // Get the data from insight table, we should only return 1 value, if we have more than one
            // then we need to exit.
            InsightTable[] it = this.getInsightMonthCountOn(0, DateTimeHandler.getYearMonth(-1), null);

            // If the length is 0 or greater than or equal to 2 then exit
            if (it.length == 0 || it.length >= 2) {
                return;
            }

            // If yesterday count is greater than the count of the current month, then update
            if (countFromYesterday > it[0].getInt2()) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT2, countFromYesterday);
                cv.put(INSIGHT_DATE2, DateTimeHandler.yesterdayDate());

                // UPDATE insight SET int2 = <countFromYesterday>, date2 = <yesterdayDate>
                // WHERE name = month_count-<yyyyMM>;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_MONTH_COUNT + "-" + DateTimeHandler.getYearMonth(-1)}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "setInsightMonthCountUnlock: " + e);
        }

    }

    /**
     * setInsightMonthCountOnTime
     * This method will be used to work out if yesterday had the most on screen duration for the
     * current month. If so update the insight table.
     */
    @Deprecated
    public void setInsightMonthCountOnTime() {

        try {

            // Get the count form yesterday
            long countFromYesterday = this.getCounterCountOnTime(DateTimeHandler.yesterdayDate());

            // Get the data from insight table, we should only return 1 value, if we have more than one
            // then we need to exit.
            InsightTable[] it = this.getInsightMonthCountOnTime(0, DateTimeHandler.getYearMonth(-1), null);

            // If the length is 0 or greater than or equal to 2 then exit
            if (it.length == 0 || it.length >= 2) {
                return;
            }

            // If yesterday count is greater than the count of the current month, then update
            if (countFromYesterday > it[0].getInt1()) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT1, countFromYesterday);
                cv.put(INSIGHT_DATE1, DateTimeHandler.yesterdayDate());

                // UPDATE insight SET int1 = <countFromYesterday>, date1 = <yesterdayDate>
                // WHERE name = month_duration-<yyyyMM>;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_MONTH_DURATION + "-" + DateTimeHandler.getYearMonth(-1)}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "setInsightMonthCountOnTime: " + e);
        }

    }

    /**
     * setInsightWeekCountOn
     * This method will be used to work out if yesterday had the most on screen on count for the
     * current week. If so update the insight table.
     */
    @Deprecated
    public void setInsightWeekCountOn() {

        // Set up weeks if we do not have anything set up
        this.setupInsightWeek();

        try {

            // Get the count form yesterday
            int countFromYesterday = this.getCounterCountOn(DateTimeHandler.yesterdayDate());

            // Get the data from insight table, we should only return 1 value, if we have more than one
            // then we need to exit.
            InsightTable[] it = this.getInsightWeekCountOn(0, DateTimeHandler.getYearWeek(-1, null), null);

            // If the length is 0 or greater than or equal to 2 then exit
            if (it.length == 0 || it.length >= 2) {
                return;
            }

            // If yesterday count is greater than the count of the current week, then update
            if (countFromYesterday > it[0].getInt1()) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT1, countFromYesterday);
                cv.put(INSIGHT_DATE1, DateTimeHandler.yesterdayDate());

                // UPDATE insight SET int1 = <countFromYesterday>, date1 = <yesterdayDate>
                // WHERE name = week_count-<yyyyMM>;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_WEEK_COUNT + "-" + DateTimeHandler.getYearWeek(-1, null)}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "setInsightWeekCountOn: " + e);
        }
    }

    /**
     * setInsightWeekCountUnlock
     * This method will be used to work out if yesterday had the most unlock count for the
     * current week. If so update the insight table.
     */
    @Deprecated
    public void setInsightWeekCountUnlock() {

        // Set up weeks if we do not have anything set up
        this.setupInsightWeek();

        try {

            // Get the count form yesterday
            int countFromYesterday = this.getCounterCountUnlock(DateTimeHandler.yesterdayDate());

            // Get the data from insight table, we should only return 1 value, if we have more than one
            // then we need to exit.
            InsightTable[] it = this.getInsightWeekCountOn(0, DateTimeHandler.getYearWeek(-1, null), null);

            // If the length is 0 or greater than or equal to 2 then exit
            if (it.length == 0 || it.length >= 2) {
                return;
            }

            // If yesterday count is greater than the count of the current week, then update
            if (countFromYesterday > it[0].getInt2()) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT2, countFromYesterday);
                cv.put(INSIGHT_DATE2, DateTimeHandler.yesterdayDate());

                // UPDATE insight SET int2 = <countFromYesterday>, date2 = <yesterdayDate>
                // WHERE name = week_count-<yyyyMM>;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_WEEK_COUNT + "-" + DateTimeHandler.getYearWeek(-1, null)}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "setInsightWeekCountUnlock: " + e);
        }
    }

    /**
     * setInsightWeekCountOnTime
     * This method will be used to work out if yesterday had the most on screen duration for the
     * current week. If so update the insight table.
     */
    @Deprecated
    public void setInsightWeekCountOnTime() {

        try {

            // Get the count form yesterday
            long countFromYesterday = this.getCounterCountOnTime(DateTimeHandler.yesterdayDate());

            // Get the data from insight table, we should only return 1 value, if we have more than one
            // then we need to exit.
            InsightTable[] it = this.getInsightWeekCountOnTime(0, DateTimeHandler.getYearWeek(-1, null), null);

            // If the length is 0 or greater than or equal to 2 then exit
            if (it.length == 0 || it.length >= 2) {
                return;
            }

            // If yesterday count is greater than the count of the current month, then update
            if (countFromYesterday > it[0].getInt1()) {

                // Create a Content Value to update the table
                ContentValues cv = new ContentValues();
                cv.put(INSIGHT_INT1, countFromYesterday);
                cv.put(INSIGHT_DATE1, DateTimeHandler.yesterdayDate());

                // UPDATE insight SET int1 = <countFromYesterday>, date1 = <yesterdayDate>
                // WHERE name = week_duration-<yyyyMM>;
                db.update(
                        INSIGHT,
                        cv,
                        INSIGHT_NAME + " = ?",
                        new String[] {INSIGHT_NAME_WEEK_DURATION + "-" + DateTimeHandler.getYearWeek(-1, null)}
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "setInsightMonthCountOnTime: " + e);
        }
    }

    /**
     * setWearDeviceToShow()
     * This method will set the device sent through the parameter to be the new device to be shown
     * on the application
     * @param serialNo - Unique serial number
     */
    public void setWearDeviceToShow(String serialNo) {

        try {

            // Create a Content Value to update the table
            ContentValues cv = new ContentValues();
            cv.put(SETTINGS_INT_VALUE, SETTINGS_INT_VALUE_WEAR_DEVICE_HIDE);

            // UPDATE settings SET int_value = HIDE
            // WHERE text_value = WEAR AND int_value = SHOW;
            db.update(
                    SETTINGS,
                    cv,
                    SETTINGS_TEXT_VALUE + " = ? AND " + SETTINGS_INT_VALUE + " = ?",
                    new String[] {SETTINGS_TEXT_VALUE_WEAR_DEVICE, SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW + ""}
            );

            // Clear
            cv.clear();

            // New content value for updating the wear device to show
            cv.put(SETTINGS_INT_VALUE, SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW);

            // UPDATE settings SET int_value = SHOW
            // WHERE text_value = WEAR AND int_value = HIDE AND name = <serialNo>;
            db.update(
                    SETTINGS,
                    cv,
                    SETTINGS_TEXT_VALUE + " = ? AND " + SETTINGS_INT_VALUE + " = ? AND " + SETTINGS_NAME + " = ?",
                    new String[]{SETTINGS_TEXT_VALUE_WEAR_DEVICE, SETTINGS_INT_VALUE_WEAR_DEVICE_HIDE + "", serialNo}
            );

        } catch(Exception e) {
            Log.e(TAG, "setWearDeviceToShow: " + e);
        }
    }

    /**
     * setupInsightMonth
     * This method is used to setup rows for the insight table for the months of the current year.
     * If there is nothing in the insight table for this year then we will need to set it up.
     * Just call this method up and it will handle it for the current year and will determine
     * if something needs to be setup or not.
     *
     * Table Def for MOUNT COUNT:
     *      INSIGHT_NAME: "mount_count" - yyyyMM
     *      INSIGHT_TEXT1: "month_count" <- can be used to grab all items that relates to month count.
     *      INSIGHT_INT1: number is to store the count value for this given attribute
     *      INSIGHT_TIMESTAMP1: year in the format yyyy
     *      INSIGHT_TIMESTAMP2: the year month combination in the format of yyyyMM
     *      INSIGHT_DATE1: date use to store the related count value
     *
     * Table Def for MOUNT DURATION:
     *      INSIGHT_NAME: "mount_duration" - yyyyMM
     *      INSIGHT_TEXT1: "month_duration" <- can be used to grab all items that relates to month duration.
     *      INSIGHT_INT1: number is to store the count value for this given attribute
     *      INSIGHT_TIMESTAMP1: year in the format yyyy
     *      INSIGHT_TIMESTAMP2: the year month combination in the format of yyyyMM
     *      INSIGHT_DATE1: date use to store the related duration value
     */
    public void setupInsightMonth() {

        Cursor cursor = null;

        try {

            // First thing to do is check how many item rows we have to accommodate the entry.
            // SELECT count(*) FROM insight
            // WHERE text1 = month_count AND timestamp1 = currentYear;
            cursor = db.query(
                    INSIGHT,
                    new String[] {"count(*)"},
                    INSIGHT_TEXT1 + " = ? AND " + INSIGHT_TIMESTAMP1 + " = ?" ,
                    new String[] {INSIGHT_NAME_MONTH_COUNT, DateTimeHandler.getYear(0) + "" },
                    null,
                    null,
                    null
            );

            cursor.moveToFirst();

            // If we have less than 3 then we need to setup, else do nothing.
            if (cursor.getInt(0) < 3) {

                // Loop starts from 1 (Jan) and go on till 12 (Dec)
                for(int i = 1; i <= 12; i++) {

                    // Get a year month value. If month is one digit then add a 0.
                    int monthYear = Integer.parseInt(DateTimeHandler.getYear(0) + (i <= 9 ? "0" + i : i + ""));

                    ContentValues contentValues = new ContentValues();

                    // Setup for Month Count
                    contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_MONTH_COUNT + "-" + monthYear); //Name - Year Month Combination
                    contentValues.put(DatabaseDAO.INSIGHT_TEXT1, DatabaseDAO.INSIGHT_NAME_MONTH_COUNT);
                    contentValues.put(DatabaseDAO.INSIGHT_INT1, 0); // Default Count
                    contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP1, DateTimeHandler.getYear(0)); //Current Year
                    contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP2, monthYear); //Year Month Combination
                    db.insert(DatabaseDAO.INSIGHT, null, contentValues);

                    contentValues.clear();

                    // Setup for Month Duration
                    contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_MONTH_DURATION + "-" + monthYear); //Name - Year Month Combination
                    contentValues.put(DatabaseDAO.INSIGHT_TEXT1, DatabaseDAO.INSIGHT_NAME_MONTH_DURATION);
                    contentValues.put(DatabaseDAO.INSIGHT_INT1, 0); // Default Count
                    contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP1, DateTimeHandler.getYear(0)); //Current Year
                    contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP2, monthYear); //Year Month Combination
                    db.insert(DatabaseDAO.INSIGHT, null, contentValues);

                }
            }

        } catch (Exception e) {
            Log.e(TAG, "setupInsightMonth: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "setupInsightMonth: " + e);
            }
        }

    }

    /**
     * setupInsightWeek
     * This method is used to setup rows for the insight table for the weeks of the current year.
     * If there is nothing in the insight table for this year then we will need to set it up.
     * Just call this method up and it will handle it for the current year and will determine
     * if something needs to be setup or not.
     *
     * Note, because of the way the week works, when you are in the first week of January you could
     * be registered as the last week of December, so we have to make sure we got them setup as well.
     *
     * Table Def for WEEK COUNT:
     *      INSIGHT_NAME: "week_count" - yyyyMM
     *      INSIGHT_TEXT1: "week_count" <- can be used to grab all items that relates to week count.
     *      INSIGHT_INT1: number is to store the count value for this given attribute
     *      INSIGHT_TIMESTAMP1: year in the format yyyy
     *      INSIGHT_TIMESTAMP2: the year week combination in the format of yyyyMM
     *      INSIGHT_DATE1: date use to store the related count value
     *
     * Table Def for WEEK DURATION:
     *      INSIGHT_NAME: "week_duration" - yyyyMM
     *      INSIGHT_TEXT1: "week_duration" <- can be used to grab all items that relates to week duration.
     *      INSIGHT_INT1: number is to store the count value for this given attribute
     *      INSIGHT_TIMESTAMP1: year in the format yyyy
     *      INSIGHT_TIMESTAMP2: the year week combination in the format of yyyyMM
     *      INSIGHT_DATE1: date use to store the related duration value
     */
    public void setupInsightWeek() {

        Cursor cursor = null;

        try {

            // First thing to do is check how many items rows we have to accommodate the entry.
            // SELECT count(*) FROM insight
            // WHERE text1 = week_count AND timestamp1 = currentYear;
            cursor = db.query(
                    INSIGHT,
                    new String[] {"count(*)"},
                    INSIGHT_TEXT1 + " = ? AND " + INSIGHT_TIMESTAMP1 + " = ?" ,
                    new String[] {INSIGHT_NAME_WEEK_COUNT, DateTimeHandler.getYear(0) + "" },
                    null,
                    null,
                    null
            );

            cursor.moveToFirst();

            // If we have less than 5 then we need to setup, else do nothing.
            if (cursor.getInt(0) < 5) {

                // Loop starts from 1 and go on till 53 (Dec)
                for(int i = 1; i <= 53; i++) {

                    // Get a year month value. If month is one digit then add a 0.
                    int weekYear = Integer.parseInt(DateTimeHandler.getYear(0) + (i <= 9 ? "0" + i : i + ""));

                    ContentValues contentValues = new ContentValues();

                    // There is a possibility that the 1st and 53rd values might be setup, so we got to handle it.
                    // INSIGHT_NAME is unique so it will come up with a unique constraint.
                    try {

                        // Setup for Week Count
                        contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_WEEK_COUNT + "-" + weekYear); //Name - Year Week Combination
                        contentValues.put(DatabaseDAO.INSIGHT_TEXT1, DatabaseDAO.INSIGHT_NAME_WEEK_COUNT);
                        contentValues.put(DatabaseDAO.INSIGHT_INT1, 0); // Default Count
                        contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP1, DateTimeHandler.getYear(0)); //Current Year
                        contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP2, weekYear); //Year Week Combination
                        db.insert(DatabaseDAO.INSIGHT, null, contentValues);
                        contentValues.clear();

                        // Setup for Week Duration
                        contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_WEEK_DURATION + "-" + weekYear); //Name - Year Week Combination
                        contentValues.put(DatabaseDAO.INSIGHT_TEXT1, DatabaseDAO.INSIGHT_NAME_WEEK_DURATION);
                        contentValues.put(DatabaseDAO.INSIGHT_INT1, 0); // Default Count
                        contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP1, DateTimeHandler.getYear(0)); //Current Year
                        contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP2, weekYear); //Year Week Combination
                        db.insert(DatabaseDAO.INSIGHT, null, contentValues);
                        contentValues.clear();

                    } catch (Exception e) {
                        Log.e(TAG, "setupInsightMonth: Already set up - " + e);
                    }

                    // If you are 1 and 53 then set up an entry on either side of the current year
                    if (i == 1 || i == 53) {

                        // if you are 1, then plus 1 year, if you are not then minus one year
                        int newYear = (i == 1 ? DateTimeHandler.getYear(0) + 1 : DateTimeHandler.getYear(0) - 1);

                        // Get a year month value. If month is one digit then add a 0.
                        int newWeekYear = Integer.parseInt(newYear + (i == 1 ? "0" + i : i + ""));

                        try {

                            // Setup for Week Count
                            contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_WEEK_COUNT + "-" + newWeekYear); //Name - Year Week Combination
                            contentValues.put(DatabaseDAO.INSIGHT_TEXT1, DatabaseDAO.INSIGHT_NAME_WEEK_COUNT);
                            contentValues.put(DatabaseDAO.INSIGHT_INT1, 0); // Default Count
                            contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP1, newYear); //Current Year
                            contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP2, newWeekYear); //Year Week Combination
                            db.insert(DatabaseDAO.INSIGHT, null, contentValues);
                            contentValues.clear();

                            // Setup for Week Duration
                            contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_WEEK_DURATION + "-" + newWeekYear); //Name - Year Week Combination
                            contentValues.put(DatabaseDAO.INSIGHT_TEXT1, DatabaseDAO.INSIGHT_NAME_WEEK_DURATION);
                            contentValues.put(DatabaseDAO.INSIGHT_INT1, 0); // Default Count
                            contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP1, newYear); //Current Year
                            contentValues.put(DatabaseDAO.INSIGHT_TIMESTAMP2, newWeekYear); //Year Week Combination
                            db.insert(DatabaseDAO.INSIGHT, null, contentValues);
                            contentValues.clear();

                        } catch (Exception e) {
                            Log.e(TAG, "setupInsightMonth: Already set up extra - " + e);
                        }

                    }

                }

            }

        } catch (Exception e) {
            Log.e(TAG, "setupInsightMonth: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "setupInsightMonth: " + e);
            }
        }

    }

    /**
     * updateWearData
     * This method we update the database with data from the android wear watch.
     * @param date - date
     * @param count - the count
     * @param type - type, so either 4 or 5
     * @param serialNo - the serial number of the watch, unique value
     * @return true if we updated something in the database
     * false if we did not update anything
     */
    public boolean updateWearData(String date, long count, int type, String serialNo) {

        Cursor cursor = null;

        try {

            // Check to see what the current count is. Reason is if the value store on the phone app
            // is greater than what is coming from wear, then we should not update. The reason
            // that this could happen is if the wear app was reinstalled on the wear device. Therefore
            // we do not want to wipe the data on the phone application.
            //
            // SELECT count FROM counter
            // WHERE date = <date> AND type = <type> AND wearSerialNo = <serialNo>;
            cursor = db.query(
                    COUNTER,
                    new String[] {COUNTER_COUNT},
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ? AND " + COUNTER_WEAR_SERIAL_NO + " = ?",
                    new String[] {date, type + "", serialNo},
                    null,
                    null,
                    null
            );

            // Check if we have anything stored on the database. If we do then check if the value
            // in the DB is greater than what is coming from wear. If it is then do not update by
            // setting shouldTryUpdate to false.
            if (cursor.moveToFirst()) {
                if (cursor.getLong(0) > count) {
                    return false;
                }
            }

            // Create a Content Value to update the table
            ContentValues cv = new ContentValues();
            cv.put(COUNTER_COUNT, count);

            // UPDATE counter SET count = <count>
            // WHERE date = <date> AND type = <type> AND wearSerialNo = <serialNo>;
            int rowsUpdated = db.update(
                    COUNTER,
                    cv,
                    COUNTER_DATE + " = ? AND " + COUNTER_TYPE + " = ? AND " + COUNTER_WEAR_SERIAL_NO + " = ?",
                    new String[]{date, type + "", serialNo}
            );

            // If 0 rows updated, it means that the record does not exist, so lets create it.
            if (rowsUpdated == 0) {

                // Create a Content Value to insert into the table
                cv.clear();
                cv.put(COUNTER_DATE, date);
                cv.put(COUNTER_COUNT, count);
                cv.put(COUNTER_TYPE, type);
                cv.put(COUNTER_TIMEZONE_CHANGE, 0);

                // Only try and insert when date is not default.
                if (!date.equals(COUNTER_DATE_VALUE_DEFAULT)) {
                    cv.put(COUNTER_MONTH, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(date)[1]));
                    cv.put(COUNTER_YEAR_WEEK, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(date)[8]));
                    cv.put(COUNTER_YEAR, Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(date)[0]));
                }

                cv.put(COUNTER_WEAR_SERIAL_NO, serialNo);

                // INSERT INTO counter VALUE (<today>, <count>, <type>, 0, month, year_week, year, serialNo);
                db.insert(COUNTER, null, cv);
            }

            return true;

        } catch (Exception e) {
            Log.v(TAG, "updateSettingsWearLastSync: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "updateSettingsWearLastSync: " + e);
            }
        }

        return false;
    }

    /**
     * This method will update the the setting for android wear on when it was last updated.
     */
    public void updateSettingsWearLastSync() {

        try {

            // Create a Content Value to update the table
            ContentValues cv = new ContentValues();
            cv.put(SETTINGS_INT_VALUE, DateTimeHandler.todayTimestamp());

            // UPDATE settings SET int_value = <timestamp>
            // WHERE name = wear_last_sync;
            int rowsUpdated = db.update(
                    SETTINGS,
                    cv,
                    SETTINGS_NAME + " = ?",
                    new String[] {SETTINGS_NAME_WEAR_LAST_SYNC}
            );

            // If 0 rows updated, it means that the record does not exist, so lets create it.
            if (rowsUpdated == 0) {

                // Set up the values for the Settings Table.
                cv.clear();
                cv.put(DatabaseDAO.SETTINGS_NAME, SETTINGS_NAME_WEAR_LAST_SYNC);
                cv.put(DatabaseDAO.SETTINGS_DESCRIPTION, "Stores the timestamp of when android wear last sync data");
                db.insert(DatabaseDAO.SETTINGS, null, cv);

                db.insert(SETTINGS, null, cv);

            }

        } catch (Exception e) {
            Log.v(TAG, "updateSettingsWearLastSync: " + e);
        }
    }

    /**
     * Get the value of when the last sync happened for the android wear device.
     * @return timestamp of when there was a sync to the main Android Wear Device
     */
    public long getSettingsWearLaySync() {

        Cursor cursor = null;
        long timestamp = 0;

        try {

            // SELECT int_value FROM settings
            // WHERE name = wear_lay_sync;
            cursor = db.query(
                    SETTINGS,
                    new String[] {SETTINGS_INT_VALUE},
                    SETTINGS_NAME + " = ?",
                    new String[] {SETTINGS_NAME_WEAR_LAST_SYNC},
                    null,
                    null,
                    null
            );

            // If database entry does exist, store the value, else do nothing
            if(cursor.moveToFirst()) {
                // Store the count and prepare to return
                timestamp = cursor.getLong(0);
            }

        } catch(Exception e) {
            Log.e(TAG, "getSettingsWearLaySync: " + e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getSettingsWearLaySync: " + e);
            }
        }
        return timestamp;
    }

    /**
     * This method is a test method to get all the row count. This is so we can manage the data
     * storage of the devices.
     */
    public String getAllRowCount() {

        try {

            Cursor c = db.rawQuery("SELECT count(*) FROM settings", null);
            c.moveToFirst();
            String r = "\nSettings Row Count: " + c.getString(0);
            c.close();

            c = db.rawQuery("SELECT count(*) FROM counter", null);
            c.moveToFirst();
            r += "\nCounter Row Count: " + c.getString(0);
            c.close();

            c = db.rawQuery("SELECT count(*) FROM screenlog", null);
            c.moveToFirst();
            r += "\nScreenLog Row Count: " + c.getString(0);
            c.close();

            c = db.rawQuery("SELECT count(*) FROM tzd", null);
            c.moveToFirst();
            r += "\nTZD Row Count: " + c.getString(0);
            c.close();

            c = db.rawQuery("SELECT count(*) FROM insight", null);
            c.moveToFirst();
            r += "\nInsight Row Count: " + c.getString(0);
            c.close();

            c = db.rawQuery("SELECT count(*) FROM insightarc", null);
            c.moveToFirst();
            r += "\nInsightARC Row Count: " + c.getString(0) + "\n";

            c = db.rawQuery("SELECT date, count, type, timezone_change, month, year_week, year, wearSerialNo FROM counter WHERE date <> 'default' ORDER BY date DESC", null);
            int count = 0;
            while(c.moveToNext()) {
                r+= "\nCounter: " + c.getString(0) + " " + c.getString(1) + " " + c.getString(2) + " " + c.getString(3) + " " + c.getString(4) +  " " + c.getString(5) + " " + c.getString(6) + " " + c.getString(7);
                count++;
                if (count == 45) break;
            }

            c = db.rawQuery("SELECT date, count, type, timezone_change, month, year_week, year, wearSerialNo FROM counter WHERE date = 'default' ORDER BY date DESC", null);
            count = 0;
            while(c.moveToNext()) {
                r+= "\nCountDefault: " + c.getString(0) + " " + c.getString(1) + " " + c.getString(2) + " " + c.getString(3) + " " + c.getString(4) +  " " + c.getString(5) + " " + c.getString(6) + " " + c.getString(7);
                count++;
                if (count == 45) break;
            }

            c.close();

            return r;

        } catch (Exception e) {
            Log.e(TAG, "forTesting(): " + e);
        }

        return null;

    }

    /**
     * This method is used for testing, it is used to execute some commands to bring the DB back in line.
     */
    public void forTesting() {
        try {

            /*ContentValues contentValues = new ContentValues();
            // Set up the values for the Settings Table.
            contentValues.put(DatabaseDAO.SETTINGS_NAME, DatabaseDAO.SETTINGS_NAME_INSIGHT_LAST_RUN);
            contentValues.put(DatabaseDAO.SETTINGS_DESCRIPTION, "Stores the timestamp of the last event the notification service has run up to on the screenlog table");
            db.insert(DatabaseDAO.SETTINGS, null, contentValues);
            contentValues.clear();

            String createScreenLogIndex =
                    "CREATE INDEX screenlog_ixa ON screenlog (timestamp DESC)";
            db.execSQL(createScreenLogIndex);

            db.execSQL("ALTER TABLE counter ADD CONSTRAINT counter_unique UNIQUE(date, type);");
            */
            //db.execSQL("CREATE UNIQUE INDEX counter_ixa ON counter (date, type)");

            /*version 1.b
            //db.execSQL("ALTER TABLE counter ADD COLUMN timezone_change INT DEFAULT 0");

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_ALL_TIME_DURATION);
            contentValues.put(DatabaseDAO.INSIGHT_INT1, 0);
            contentValues.put(DatabaseDAO.INSIGHT_DATE1, DateTimeHandler.yesterdayDate());
            db.insert(DatabaseDAO.INSIGHT, null, contentValues);

            */

            /* start of populating month year and yeark week *
            db.execSQL("ALTER TABLE counter ADD COLUMN month INT");
            db.execSQL("ALTER TABLE counter ADD COLUMN year_week INT");
            db.execSQL("ALTER TABLE counter ADD COLUMN year INT");

            // SELECT date, intent FROM screenlog
            // ORDER BY date DESC;
            Cursor cursor = db.rawQuery(
                    "SELECT DISTINCT date FROM counter"
                    , null
            );

            while(cursor.moveToNext()) {

                if(COUNTER_DATE_VALUE_DEFAULT.equals(cursor.getString(0))) {
                    continue;
                } else {
                    String month = DateTimeHandler.getDayMonthYearBreakDown(cursor.getString(0))[1];
                    String yearweek = DateTimeHandler.getDayMonthYearBreakDown(cursor.getString(0))[8];
                    String year = DateTimeHandler.getDayMonthYearBreakDown(cursor.getString(0))[0];


                    db.execSQL("UPDATE counter SET month = " + month +
                            ", year_week = " + yearweek +
                            ", year = " + year +
                            " WHERE date = '" + cursor.getString(0) +"';");
                }

            }

            /* populate the month year and yearweek

            db.execSQL("ALTER TABLE counter ADD COLUMN wearSerialNo Text");
            db.execSQL("UPDATE counter SET wearSerialNo = 'P027XS4SKG' WHERE type IN (4,5)");
            */

        } catch (Exception e) {
            Log.e(TAG, "forTesting(): " + e);
        }
    }

}
