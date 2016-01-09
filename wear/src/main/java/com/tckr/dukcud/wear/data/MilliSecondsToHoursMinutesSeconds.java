package com.tckr.dukcud.wear.data;

import android.content.Context;

import com.tckr.dukcud.R;

/**
 * Class to translate MilliSeconds to Hours, Minutes and Seconds
 */
public class MilliSecondsToHoursMinutesSeconds {

    private int hours;
    private int minutes;
    private int seconds;
    private int days;

    public MilliSecondsToHoursMinutesSeconds(long milliseconds) {

        // casting to the int.
        hours = (int) (milliseconds / (1000 * 60 * 60)) % 24;
        minutes = (int) (milliseconds / (1000 * 60)) % 60;
        seconds = (int) (milliseconds / 1000) % 60;
        days = (int) (milliseconds / (1000 * 60 * 60 * 24));
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getDays() {
        return days;
    }

    /**
     * This method will translate the timestamp to a readable format, but in a loose way, i.e.
     * it is 100% accurate, but it will do ;)
     * @param context the context
     * @return a readable format to the end user, like over 2 hours ago
     */
    public String translateToReadableFormatLoose(Context context) {

        if (hours == 1) {
            return context.getString(R.string.over_1_hour_ago);
        } else if (hours > 1) {
            return context.getString(R.string.over_x_hours_ago, hours);
        } else if (minutes == 0) {
            return context.getString(R.string.one_minute_ago);
        } else {
            return context.getString(R.string.x_minute_ago, minutes + 1);
        }
    }

    /**
     * This method will translate the timestamp to a readable format
     * @param context the context
     * @return String in a readable format like 1 hour and 34 minutes
     */
    public String translateToReadableFormat(Context context) {

        // Set the return string
        String returnValue = "";

        // Array of days, hours, minutes and seconds that was populated
        String[] valuesToPopulate = new String[4];

        // Count of how many times we are populating a string to return to the end user
        int valuesPopulated = 0;

        // position where we last populated
        int lastIndexPopulated = -1;

        // Work out days
        if(days == 1) {
            valuesToPopulate[0] = days + " " + context.getString(R.string.day);
            valuesPopulated++;
            lastIndexPopulated = 0;
        } else if (days > 1) {
            valuesToPopulate[0] = days + " " + context.getString(R.string.days);
            valuesPopulated++;
            lastIndexPopulated = 0;
        }

        // Work out hours
        if(hours == 1) {
            valuesToPopulate[1] = hours + " " + context.getString(R.string.hour);
            valuesPopulated++;
            lastIndexPopulated = 1;
        } else if (hours > 1) {
            valuesToPopulate[1] = hours + " " + context.getString(R.string.hours);
            valuesPopulated++;
            lastIndexPopulated = 1;
        }

        // Work out minutes
        if(minutes == 1) {
            valuesToPopulate[2] = minutes + " " + context.getString(R.string.minute);
            valuesPopulated++;
            lastIndexPopulated = 2;
        } else if (minutes > 1) {
            valuesToPopulate[2] = minutes + " " + context.getString(R.string.minutes);
            valuesPopulated++;
            lastIndexPopulated = 2;
        }

        // Work out seconds
        if(seconds == 1) {
            valuesToPopulate[3] = seconds + " " + context.getString(R.string.second);
            valuesPopulated++;
            lastIndexPopulated = 3;
        } else if (seconds > 1) {
            valuesToPopulate[3] = seconds + " " + context.getString(R.string.seconds);
            valuesPopulated++;
            lastIndexPopulated = 3;
        }

        if (valuesPopulated == 0) {
            // If 0 has been passed then return 0 seconds
            return "0 " + context.getString(R.string.seconds);
        } else if (valuesPopulated == 1) {
            // If only one item was populated than just return that array index
            return valuesToPopulate[lastIndexPopulated];
        } else {

            // a pointer to write "and" instead of comma.
            boolean charAnd = false;

            // used to know the first iteration that was used for commas/and placement
            boolean first = true;

            for(int i = 3; i >= 0; i--) {

                // Continue if null
                if (valuesToPopulate[i] == null) {
                    continue;
                }

                if (first) {
                    returnValue = valuesToPopulate[i];
                } else if (charAnd) {
                    returnValue = valuesToPopulate[i] + " " + context.getString(R.string.and) + " " + returnValue;
                } else {
                    returnValue = valuesToPopulate[i] + ", " + returnValue;
                }

                // We have done the first iteration so mark it that we have.
                first = false;

                // Set boolean to tell the next iteration if they need to put "and" in the text.
                if (i == lastIndexPopulated) {
                    charAnd = true;
                } else {
                    charAnd = false;
                }
            }

        }

        return returnValue;
    }
}
