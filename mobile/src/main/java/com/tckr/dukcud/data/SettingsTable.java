package com.tckr.dukcud.data;

import android.content.Context;
import android.database.Cursor;

/**
 * A Java Bean type class which describes the values and interaction for the table, Settings
 */
public class SettingsTable {

    private Context context;

    private String name;
    private String description;
    private String text_value;
    private long int_value;

    public SettingsTable(Context context) {
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText_value() {
        return text_value;
    }

    public void setText_value(String text_value) {
        this.text_value = text_value;
    }

    public long getInt_value() {
        return int_value;
    }

    public void setInt_value(long int_value) {
        this.int_value = int_value;
    }

    /**
     * Use to map values from a cursor
     * @param cursor the data
     * @param context context
     * @return a SettingsTable object
     */
    public static SettingsTable mapSettingsTable(Cursor cursor, Context context) {

        SettingsTable st = new SettingsTable(context);

        // Set the default
        st.setName("null");
        st.setDescription("null");
        st.setText_value("null");
        st.setInt_value(-1);

        // Iterate through the columns in the cursor and update the variables.
        for (String columns : cursor.getColumnNames()) {

            switch (columns) {
                case DatabaseDAO.SETTINGS_NAME:
                    st.setName(cursor.getString(cursor.getColumnIndex(DatabaseDAO.SETTINGS_NAME)));
                    break;
                case DatabaseDAO.SETTINGS_DESCRIPTION:
                    st.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseDAO.SETTINGS_DESCRIPTION)));
                    break;
                case DatabaseDAO.SETTINGS_TEXT_VALUE:
                    st.setText_value(cursor.getString(cursor.getColumnIndex(DatabaseDAO.SETTINGS_TEXT_VALUE)));
                    break;
                case DatabaseDAO.SETTINGS_INT_VALUE:
                    st.setInt_value(cursor.getInt(cursor.getColumnIndex(DatabaseDAO.SETTINGS_INT_VALUE)));
                    break;
            }
        }

        return st;
    }


}
