package com.tckr.dukcud.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class to set up the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DUKCUD";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Lets create the database :)
     * @param db database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        ContentValues contentValues = new ContentValues();

        /* Create settings table */
        String createSettings =
                "CREATE TABLE settings (" +
                        "name TEXT PRIMARY KEY," +
                        "description TEXT DEFAULT NULL," +
                        "text_value TEXT DEFAULT 'N/A'," +
                        "int_value INT DEFAULT 0" +
                ")";
        db.execSQL(createSettings);

        // Set up the values for the Settings Table.
        contentValues.put(DatabaseDAO.SETTINGS_NAME, DatabaseDAO.SETTINGS_NAME_INSIGHT_LAST_RUN);
        contentValues.put(DatabaseDAO.SETTINGS_DESCRIPTION, "Stores the timestamp of the last event the notification service has run up to on the screenlog table");
        db.insert(DatabaseDAO.SETTINGS, null, contentValues);
        contentValues.clear();

        /* Create counter table */
        String createCounter =
                "CREATE TABLE counter (" +
                        "date TEXT, " +
                        "count INT, " +
                        "type INT, " +
                        "timezone_change INT, " +
                        "month INT, " +
                        "year_week INT, " +
                        "year INT, " +
                        "wearSerialNo TEXT, " +
                        "unique (date, type, wearSerialNo)" +
                ")";
        db.execSQL(createCounter);

        db.execSQL("CREATE UNIQUE INDEX counter_ixa ON counter (date, type, wearSerialNo)");

        /*
         * Create the default row for the counter table. This will be use to track
         * the amount of times the screen gets turned on from the first time the
         * application has been installed
         */
        contentValues.put(DatabaseDAO.COUNTER_DATE, DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);
        contentValues.put(DatabaseDAO.COUNTER_COUNT, 0);
        contentValues.put(DatabaseDAO.COUNTER_TYPE, DatabaseDAO.COUNTER_TYPE_ON);
        contentValues.put(DatabaseDAO.COUNTER_TIMEZONE_CHANGE, 0);
        db.insert(DatabaseDAO.COUNTER, null, contentValues);
        contentValues.clear();

        contentValues.put(DatabaseDAO.COUNTER_DATE, DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);
        contentValues.put(DatabaseDAO.COUNTER_COUNT, 0);
        contentValues.put(DatabaseDAO.COUNTER_TYPE, DatabaseDAO.COUNTER_TYPE_USER_UNLOCK);
        contentValues.put(DatabaseDAO.COUNTER_TIMEZONE_CHANGE, 0);
        db.insert(DatabaseDAO.COUNTER, null, contentValues);
        contentValues.clear();

        contentValues.put(DatabaseDAO.COUNTER_DATE, DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);
        contentValues.put(DatabaseDAO.COUNTER_COUNT, 0);
        contentValues.put(DatabaseDAO.COUNTER_TYPE, DatabaseDAO.COUNTER_TYPE_ON_TIME);
        contentValues.put(DatabaseDAO.COUNTER_TIMEZONE_CHANGE, 0);
        db.insert(DatabaseDAO.COUNTER, null, contentValues);
        contentValues.clear();

        /*
         * Create screenlog table
         * This table will record every interaction when the user turns on or off the screen
         */
        String createScreenLog =
                "CREATE TABLE screenlog (" +
                        "date TEXT," +
                        "intent INT," +
                        "timestamp INT," +
                        "timezone TEXT" +
                ")";
        db.execSQL(createScreenLog);

        db.execSQL("CREATE INDEX screenlog_ixa ON screenlog (timestamp DESC)");

        String createTimeZoneDetector =
                "CREATE TABLE tzd (" +
                        "date TEXT, " +
                        "timezone TEXT" +
                        ")";
        db.execSQL(createTimeZoneDetector);

        /*
         * This will create the insight table which will be used to display useful information
         * to the end user. The table is quite big because at the time of development I was not
         * sure what time of "insight" I will be providing so I created a table to store quite
         * a bit of information if needed. Then a schema to describe each type is needed
         */
        String createInsight =
                "CREATE TABLE insight (" +
                        "name TEXT PRIMARY KEY, " +
                        "display INT DEFAULT 0, " +
                        "date1 TEXT, " +
                        "date2 TEXT, " +
                        "date3 TEXT, " +
                        "date4 TEXT, " +
                        "datetime1 TEXT, " +
                        "datetime2 TEXT, " +
                        "datetime3 TEXT, " +
                        "datetime4 TEXT, " +
                        "float1 REAL, " +
                        "float2 REAL, " +
                        "float3 REAL, " +
                        "float4 REAL, " +
                        "int1 INT, " +
                        "int2 INT, " +
                        "int3 INT, " +
                        "int4 INT, " +
                        "text1 TEXT, " +
                        "text2 TEXT, " +
                        "text3 TEXT, " +
                        "text4 TEXT, " +
                        "text5 TEXT, " +
                        "text6 TEXT, " +
                        "timestamp1 INT, " +
                        "timestamp2 INT, " +
                        "timestamp3 INT, " +
                        "timestamp4 INT" +
                ")";
        db.execSQL(createInsight);

        /**
         * POPULATE TABLE FOR INSIGHT
         **/

        // Create a row for ALL_TIME_COUNT
        contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_ALL_TIME_COUNT);
        contentValues.put(DatabaseDAO.INSIGHT_INT1, 0);
        contentValues.put(DatabaseDAO.INSIGHT_DATE1, DateTimeHandler.yesterdayDate());
        db.insert(DatabaseDAO.INSIGHT, null, contentValues);
        contentValues.clear();

        // Create a row for ALL_TIME_COUNT
        contentValues.put(DatabaseDAO.INSIGHT_NAME, DatabaseDAO.INSIGHT_NAME_ALL_TIME_DURATION);
        contentValues.put(DatabaseDAO.INSIGHT_INT1, 0);
        contentValues.put(DatabaseDAO.INSIGHT_DATE1, DateTimeHandler.yesterdayDate());
        db.insert(DatabaseDAO.INSIGHT, null, contentValues);
        contentValues.clear();

        /**
         * END
         **/

        // This table is a copy of the insight table, however when the service runs once a day
        // the timestamp will be stamped so if we need the data later we have it.
        String createInsightArc =
                "CREATE TABLE insightarc (" +
                        "name TEXT, " +
                        "date1 TEXT, " +
                        "date2 TEXT, " +
                        "date3 TEXT, " +
                        "date4 TEXT, " +
                        "datetime1 TEXT, " +
                        "datetime2 TEXT, " +
                        "datetime3 TEXT, " +
                        "datetime4 TEXT, " +
                        "float1 REAL, " +
                        "float2 REAL, " +
                        "float3 REAL, " +
                        "float4 REAL, " +
                        "int1 INT, " +
                        "int2 INT, " +
                        "int3 INT, " +
                        "int4 INT, " +
                        "text1 TEXT, " +
                        "text2 TEXT, " +
                        "text3 TEXT, " +
                        "text4 TEXT, " +
                        "text5 TEXT, " +
                        "text6 TEXT, " +
                        "timestamp1 INT, " +
                        "timestamp2 INT, " +
                        "timestamp3 INT, " +
                        "timestamp4 INT, " +
                        "arctimestamp INT" +
                ")";
        db.execSQL(createInsightArc);

        Log.v(TAG, "Finished Creating database");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


}
