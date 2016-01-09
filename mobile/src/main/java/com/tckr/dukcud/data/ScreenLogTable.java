package com.tckr.dukcud.data;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.tckr.dukcud.R;

/**
 * A Java Bean type class which describes the values and interaction for the table, ScreenLogTable
 */
public class ScreenLogTable {

    private Context context;

    private String date;
    private int intent;
    private String intentString;
    private long timestamp;
    private String timezone;

    public ScreenLogTable(Context context) {
        this.context = context;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIntent() {
        return intent;
    }

    public void setIntent(int intent) {
        this.intent = intent;
    }

    public String getIntentString() {
        return intentString;
    }

    public void setIntentString(int intent) {

        Resources resources = context.getResources();

        this.intentString = "null";

        if(intent == DatabaseDAO.SCREENLOG_INTENT_OFF) {
            this.intentString = resources.getString(R.string.screenlog_intent_off);
        } else if(intent == DatabaseDAO.SCREENLOG_INTENT_ON) {
            this.intentString = resources.getString(R.string.screenlog_intent_on);
        } else if(intent == DatabaseDAO.SCREENLOG_INTENT_SHUTDOWN) {
            this.intentString = resources.getString(R.string.screenlog_intent_shutdown);
        } else if(intent == DatabaseDAO.SCREENLOG_INTENT_USER_PRESENT) {
            this.intentString = resources.getString(R.string.screenlog_intent_user_present);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * Use to map values from a cursor
     * @param cursor the data
     * @param context context
     * @return a ScreenLogTable object
     */
    public static ScreenLogTable mapScreenLogTable(Cursor cursor, Context context) {

        ScreenLogTable slt = new ScreenLogTable(context);

        // Set the default
        slt.setDate("null");
        slt.setIntent(-1);
        slt.setIntentString(-1);
        slt.setTimestamp(0);
        slt.setTimezone("null");

        // Iterate through the columns in the cursor and update the variables.
        for (String columns : cursor.getColumnNames()) {

            switch (columns) {
                case DatabaseDAO.SCREENLOG_DATE:
                    slt.setDate(cursor.getString(cursor.getColumnIndex(DatabaseDAO.SCREENLOG_DATE)));
                    break;
                case DatabaseDAO.SCREENLOG_INTENT:
                    slt.setIntent(cursor.getInt(cursor.getColumnIndex(DatabaseDAO.SCREENLOG_INTENT)));
                    slt.setIntentString(cursor.getInt(cursor.getColumnIndex(DatabaseDAO.SCREENLOG_INTENT)));
                    break;
                case DatabaseDAO.SCREENLOG_TIMESTAMP:
                    slt.setTimestamp(cursor.getLong(cursor.getColumnIndex(DatabaseDAO.SCREENLOG_TIMESTAMP)));
                    break;
                case DatabaseDAO.SCREENLOG_TIMEZONE:
                    slt.setTimezone(cursor.getString(cursor.getColumnIndex(DatabaseDAO.SCREENLOG_TIMEZONE)));
                    break;
            }
        }

        return slt;
    }
}
