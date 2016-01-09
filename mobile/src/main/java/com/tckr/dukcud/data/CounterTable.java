package com.tckr.dukcud.data;

import android.database.Cursor;

/**
 * A Java Bean type class which describes the values and interaction for the table, CounterTable
 */
public class CounterTable {

    private String date;
    private long count;
    private int type;
    private int timezoneChange;
    private int month;
    private int yearWeek;
    private int year;
    private String wearSerialNo;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTimezoneChange() {
        return timezoneChange;
    }

    public void setTimezoneChange(int timezoneChange) {
        this.timezoneChange = timezoneChange;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYearWeek() {
        return yearWeek;
    }

    public void setYearWeek(int yearWeek) {
        this.yearWeek = yearWeek;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getWearSerialNo() {
        return wearSerialNo;
    }

    public void setWearSerialNo(String wearSerialNo) {
        this.wearSerialNo = wearSerialNo;
    }

    /**
     * mapCounterTable
     * @param cursor - The data parsed into this method
     * @return a CounterTable object to the return program with properties that can be used
     */
    public static CounterTable mapCounterTable(Cursor cursor) {

        CounterTable ct = new CounterTable();

        // set defaults
        ct.setDate("null");
        ct.setCount(0);
        ct.setType(-1);
        ct.setTimezoneChange(0);
        ct.setMonth(0);
        ct.setYearWeek(0);
        ct.setYear(0);
        ct.setWearSerialNo("null");

        // Iterate through the columns in the cursor and update the variables.
        for (String columns : cursor.getColumnNames()) {

            switch (columns) {
                case DatabaseDAO.COUNTER_DATE:
                    ct.setDate(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_DATE)));
                    break;
                case DatabaseDAO.COUNTER_COUNT:
                    ct.setCount(Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_COUNT))));
                    break;
                case DatabaseDAO.COUNTER_TYPE:
                    ct.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_TYPE))));
                    break;
                case DatabaseDAO.COUNTER_TIMEZONE_CHANGE:
                    ct.setTimezoneChange(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_TIMEZONE_CHANGE))));
                    break;
                case DatabaseDAO.COUNTER_MONTH:
                    ct.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_MONTH))));
                    break;
                case DatabaseDAO.COUNTER_YEAR_WEEK:
                    ct.setYearWeek(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_YEAR_WEEK))));
                    break;
                case DatabaseDAO.COUNTER_YEAR:
                    ct.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_YEAR))));
                    break;
                case DatabaseDAO.COUNTER_WEAR_SERIAL_NO:
                    ct.setWearSerialNo(cursor.getString(cursor.getColumnIndex(DatabaseDAO.COUNTER_WEAR_SERIAL_NO)));
                    break;
            }
        }

        return ct;
    }

}
