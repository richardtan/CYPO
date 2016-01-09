package com.tckr.dukcud.data;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * DateTimeHandler
 * This class will handle all the logic to get dates for the program.
 * Note, the developer (that's me) has been testing this application in the United Kingdom.
 * You will notice that there is a lot of converting dates into formats
 * and because I use days of weeks e.t.c. I do not know how it will react if I was on different
 * Locations. Therefore I have defaulted all Locale to the UK as I know it works and works
 * the way the program will work.
 */
public final class DateTimeHandler {

    private static final String TAG = "DateTimeHandler";

    /**
     * Method to get the next notification date. Notification will be set every 4am
     * @return a calendar object of the next time we are going to fire the notification
     */
    public static Calendar getNotificationDate() {

        // Set Calendar for today and also for the notification date.
        Calendar currentTime = Calendar.getInstance();
        Calendar nextNotificationTime = Calendar.getInstance();

        // Set current time including hours and minutes
        currentTime.setTimeInMillis(System.currentTimeMillis());

        // Set notification time without hours and minutes
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        try {
            nextNotificationTime.setTime(dateFormat.parse(dateFormat.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // If we have gone past 4 am then schedule the notification for the next day.
        // Else set the notification for 4am today
        if(currentTime.get(Calendar.HOUR_OF_DAY) >= 4) {
            nextNotificationTime.add(Calendar.DATE, 1); // Add 1 day
            nextNotificationTime.add(Calendar.HOUR_OF_DAY, 4); // Add 4 hours
        } else {
            nextNotificationTime.add(Calendar.HOUR_OF_DAY, 4); // Add 4 hours
        }

        //Below is for testing and will be pinged every minute.
        //nextNotificationTime.setTimeInMillis(System.currentTimeMillis() + 60000);

        Log.v(TAG, "Notification set at: " + nextNotificationTime.getTime());
        Log.v(TAG, "Notification set at timezone: " + nextNotificationTime.getTimeZone().getDisplayName());

        return nextNotificationTime;

    }

    /**
     * getDateByTimeStamp
     * This method will return the date in the format yyyy-mm-dd for a given timestamp.
     * @param timestamp in long format
     * @return a date in yyyy-MM-dd format
     */
    public static String getDateByTimeStamp(long timestamp) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date date = new Date();
        date.setTime(timestamp);
        return dateFormat.format(date);
    }

    /**
     * getDateByTimeStampLongFormat
     * This method will return the date in the format yyyy-mm-dd HH:mm:ss for a given timestamp.
     * @param timestamp in long format
     * @return a date in yyyy-MM-dd HH:mm:ss
     */
    public static String getDateByTimeStampLongFormat(long timestamp) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        Date date = new Date();
        date.setTime(timestamp);
        return dateFormat.format(date);
    }

    /**
     * This method will get the timestamp for today, at midnight
     * @return a long timestamp
     */
    public static long getTimeStampAtMidnightToday() {

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
     * Get the timestamp of when we should be starting the insight analyst.
     * TODO factor in time zone hence why a dedicated method
     * @return -
     */
    public static long getStartPointTimeStamp() {

        // Set Calendar for the start time.
        Calendar startTime = Calendar.getInstance();

        // Set start time without hours and minutes
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        try {
            startTime.setTime(dateFormat.parse(dateFormat.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Minus a Day at 12 Midnight
        startTime.add(Calendar.DATE, -1);

        return startTime.getTimeInMillis();
    }

    /**
     * Get the timestamp of when we should be ending the insight analyst.
     * TODO factor in time zone hence why a dedicated method
     * @return -
     */
    public static long getEndPointTimeStamp() {

        // Set Calendar for the end time.
        Calendar endTime = Calendar.getInstance();

        // Set end time without hours and minutes
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        try {
            endTime.setTime(dateFormat.parse(dateFormat.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return endTime.getTimeInMillis();
    }

    /**
     * timezone()
     * @return the timezone in short
     */
    public static String timezone() {

        Calendar currentTime = Calendar.getInstance();
        Date currentDate = currentTime.getTime();
        TimeZone timezone = currentTime.getTimeZone();
        return timezone.getDisplayName(timezone.inDaylightTime(currentDate), TimeZone.SHORT);
    }

    /**
     * todayDate
     * @return today date in YYYY-MM-DD
     */
    public static String todayDate() {

        Calendar currentTime = Calendar.getInstance();
        //Log.v(TAG, "todayDate() - Timezone is: "+ currentTime.getTimeZone());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        return sdf.format(currentTime.getTime());
    }

    /**
     * todayDateTime
     * @return today date in YYY-MM-DD HH:MM:SS
     */
    public static String todayDateTime() {

        Calendar currentTime = Calendar.getInstance();
        //Log.v(TAG, "todayDateTime() - Timezone is: "+ currentTime.getTimeZone());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        return sdf.format(currentTime.getTime());
    }

    /**
     * todayTimestamp()
     * @return today timestamp in long, the number of seconds since 1970-01-01 00:00:00 UTC
     */
    public static long todayTimestamp() {

        Calendar currentTime = Calendar.getInstance();
        //Log.v(TAG, "todayTimestamp() - Timestamp is: "+ currentTime.getTimeInMillis());
        return currentTime.getTimeInMillis();
    }


    /**
     * yesterdayDate
     * @return yesterday date in YYYY-MM-DD
     */
    public static String yesterdayDate() {

        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.DATE, - 1); // Remove one day
        //Log.v(TAG, "yesterdayDate() - Timezone is: "+ currentTime.getTimeZone());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd" , Locale.UK);
        return sdf.format(currentTime.getTime());
    }

    /**
     * getCurrentYearMonth
     * This will get the year and month in the format yyyyMM and return it back as a number
     * @param offset
     *      Offset the current date.
     *      If you want yesterday date then pass -1
     *      If you want tomorrows date then pass 1
     *      If you want today, just pass 0.
     * @return
     *      yyyyMM for the given month that we are in
     */
    public static int getYearMonth(int offset) {

        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.DATE, offset); // apply offset
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.UK);
        return Integer.parseInt(sdf.format(currentTime.getTime()));

    }

    /**
     * getCurrentYearWeek
     * This will get the year and week in the format yyyyww and return it back as a number
     * Now, we have to test for weeks being in a different year, i.e. Jan 01 2016 actually falls
     * in week 53 for 2015 so we have to make sure we bring back the right year.
     * @param offset
     *      Offset the current date.
     *      If you want yesterday date then pass -1
     *      If you want tomorrows date then pass 1
     *      If you want today, just pass 0.
     * @param date
     *      Input a pre-defined date in the format of yyyy-MM-dd. This will override the offset,
     *      if you do not want to use then just input null
     * @return
     *      yyyyww for the given week that we are in
     */
    public static int getYearWeek(int offset, String date) {

        Calendar currentTime = Calendar.getInstance();

        // If date is not null, set the date from the parsed date
        if (date != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                Date d = df.parse(date);
                currentTime.setTime(d);
            } catch (ParseException pe) {
                Log.e(TAG, "getYearWeek - cannot parse date from param date: " + pe);
                currentTime.add(Calendar.DATE, offset); // apply offset if something went wrong
            }
        } else {
            currentTime.add(Calendar.DATE, offset); // apply offset
        }

        SimpleDateFormat sdf = new SimpleDateFormat("ww", Locale.UK);

        // get the week and the year
        String week = sdf.format(currentTime.getTime());
        int year = DateTimeHandler.getYear(offset);

        if (week.equals("52") || week.equals("53")) {

            // If you are in January and you are in week 52 or 53 then minus 1 year
            SimpleDateFormat month = new SimpleDateFormat("MM", Locale.UK);
            if (month.format(currentTime.getTime()).equals("01")) {
                year = year - 1;
            }

        } else if (week.equals("01")) {

            // If you are in January and you are in week 52 or 53 then minus 1 year
            SimpleDateFormat month = new SimpleDateFormat("MM", Locale.UK);
            if (month.format(currentTime.getTime()).equals("12")) {
                year = year + 1;
            }

        }

        return Integer.parseInt(year + week);

    }

    /**
     * getCurrentYear
     * This will get the year in the format yyyy and return it back as a number
     * @param offset
     *      Offset the current date.
     *      If you want yesterday date then pass -1
     *      If you want tomorrows date then pass 1
     *      If you want today, just pass 0.
     * @return
     *      yyyy for the given year that we are in
     */
    public static int getYear(int offset) {

        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.DATE, offset); // apply offset
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.UK);
        return Integer.parseInt(sdf.format(currentTime.getTime()));

    }

    /**
     * getDayOfMonthSuffix
     * Gets the suffix for the date
     * TODO not sure what will happen when using different language, but not my problem now
     * @param day
     *      the day you want, so if you put in 2, you will get nd
     * @return
     *      st, nd, rd, th
     */
    public static String getDayOfMonthSuffix(int day) {

        // If you are 11, 12 or 13, return th
        if (day >= 11 && day <= 13) {
            return "th";
        }

        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * getDayMonthYearBreakDown
     * @param date
     *      Format will be yyyy-MM-dd
     * @return
     *      Array
     *      [0] = year
     *      [1] = month in number
     *      [2] = day without leading 0
     *      [3] = day with leading 0
     *      [4] = month, 3 character representation
     *      [5] = month full representation
     *      [6] = day name 3 character representation
     *      [7] = day name full representation
     *      [8] = year Week as yyyyww
     */
    public static String[] getDayMonthYearBreakDown(String date) {

        // Initialise the array
        String[] dateComponent = new String[9];

        try {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            Date d = df.parse(date);

            dateComponent[0] = new SimpleDateFormat("yyyy", Locale.UK).format(d); // YEAR
            dateComponent[1] = new SimpleDateFormat("MM", Locale.UK).format(d); // Month in number
            dateComponent[2] = new SimpleDateFormat("d", Locale.UK).format(d); // Day without the leading 0
            dateComponent[3] = new SimpleDateFormat("dd", Locale.UK).format(d); // Day with leading 0
            dateComponent[4] = new SimpleDateFormat("MMM", Locale.UK).format(d); // Month, 3 character representation
            dateComponent[5] = new SimpleDateFormat("MMMM", Locale.UK).format(d); // Month full representation
            dateComponent[6] = new SimpleDateFormat("EEE", Locale.UK).format(d); // Day name 3 character representation
            dateComponent[7] = new SimpleDateFormat("EEEE", Locale.UK).format(d); // Day name full representation
            dateComponent[8] = DateTimeHandler.getYearWeek(0, date) + ""; // Year Week as yyyyww

            return dateComponent;

        } catch (ParseException e) {
            Log.e(TAG, "getDayMonthYearBreakDown() : " + e);
        }

        return dateComponent;
    }

}
