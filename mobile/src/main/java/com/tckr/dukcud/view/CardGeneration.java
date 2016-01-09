package com.tckr.dukcud.view;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.tckr.dukcud.MainActivity;
import com.tckr.dukcud.R;
import com.tckr.dukcud.WearManagementActivity;
import com.tckr.dukcud.data.CounterTable;
import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.data.DatabaseDAO;
import com.tckr.dukcud.data.DateTimeHandler;
import com.tckr.dukcud.data.MilliSecondsToHoursMinutesSeconds;

import java.text.DecimalFormat;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * This class will handle the generation of cards for the UI. Just call the method here
 * to create a card which can be put onto the UI.
 */
public class CardGeneration {

    private DatabaseDAO dao;
    private Context context;

    /**
     * Constructor for the card generation. It will also open up a database connection.
     * Remember to close the connection when you are done by calling closeDatabaseConnection()
     * @param context .
     */
    public CardGeneration(Context context) {

        this.context = context;
        this.dao = new DatabaseDAO(context);
        openDatabaseConnection();
    }

    /**
     * This will open the database connection
     */
    public void openDatabaseConnection() {
        dao.open();
    }

    /**
     * This will close the database connection
     */
    public void closeDatabaseConnection() {
        dao.close();
    }

    /**
     * This method will grab the data for the all time card. It will show data from the beginning
     * of when the application was installed by the user.
     * @return
     * Card
     */
    public Card getAllTimeCard() {

        // Get the data for the wear from the settings table
        //long wearLastSync = dao.getSettingsWearLaySync();

        // Set the variables needed to create the card.
        int deviceOnCount = dao.getCounterCountOn(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);
        int deviceOnCountUnlock = dao.getCounterCountUnlock(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);
        long deviceTimeOnCount = dao.getCounterCountOnTime(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);
        int wearOnCount = dao.getCounterCountOnWear(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);
        long wearTimeOnCount = dao.getCounterCountOnTimeWear(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);

        // Some variables for the wear text
        String wearText = "";

        // If wearOnCount is 0 then you do not have a watch, so only do the below if you have a watch.
        if (wearOnCount != 0) {

            // Get the period of time that the android wear device has been on for and then set the text
            MilliSecondsToHoursMinutesSeconds mwt = new MilliSecondsToHoursMinutesSeconds(wearTimeOnCount);
            wearText = context.getString(R.string.all_time_card_message_wear,
                    new DecimalFormat("#,###,###").format(wearOnCount),
                    mwt.translateToReadableFormat(context));
        }

        // Create the card, and pass the wear last sync text.
        CypoCard card = new CypoCard(context, context.getString(R.string.all_time_card_info));
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the title of the card.
        cardHeader.setTitle(context.getString(R.string.all_time));
        card.addCardHeader(cardHeader);

        // Get the period of time that the device has been on for today.
        MilliSecondsToHoursMinutesSeconds mpt = new MilliSecondsToHoursMinutesSeconds(deviceTimeOnCount);

        // Add the text to the card
        card.setMainTitle(Html.fromHtml(context.getString(R.string.all_time_card_message,
                new DecimalFormat("#,###,###").format(deviceOnCount),
                new DecimalFormat("#,###,###").format(deviceOnCountUnlock),
                mpt.translateToReadableFormat(context)) + wearText));

        return card;
    }

    /**
     * getAllTimeChargeSOTCard()
     * This will show the card for the screen on time.
     * @return
     * Card
     */
    public Card getAllTimeChargeSOTCard() {

        CounterTable[] iDuration = dao.getInsightByTypeWeekOrMonth(DatabaseDAO.COUNTER_TYPE_CHARGE_SOT,
                0, 0, 0, DatabaseDAO.COUNTER_COUNT + " DESC");

        if (iDuration == null) {
            // If we are null, then return null
            return null;
        } else if (iDuration.length == 0) {
            // If the length is 0 then exit.
            return null;
        } else if (iDuration[0].getDate() == null) {
            // If date is null that means nothing populated, so no point to display the card.
            return null;
        }

        // Break down the date into components so we can create the card with nice Text. If there
        // was an issue to convert the date, return null.
        // Note the date is in yyyy-MM-dd HH:mm:ss so we need to pick on yyyy-MM-dd
        String[] dateRepIDuration = DateTimeHandler.getDayMonthYearBreakDown(iDuration[0].getDate().substring(0, 10));
        if (dateRepIDuration == null) {
            return null;
        }

        // Set up components for the card
        CypoCard card = new CypoCard(context, context.getString(R.string.sot_info));
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the title of the card. Use primary driver
        cardHeader.setTitle(context.getString(R.string.sot_insight));
        card.addCardHeader(cardHeader);

        // format will be like "Tuesday 15th September"
        // This is for the mount duration
        String cardDateIDuration = dateRepIDuration[7] + " " +
                dateRepIDuration[2] + DateTimeHandler.getDayOfMonthSuffix(Integer.parseInt(dateRepIDuration[2])) + " " +
                dateRepIDuration[5];

        MilliSecondsToHoursMinutesSeconds mls = new MilliSecondsToHoursMinutesSeconds(iDuration[0].getCount());

        // Set the main message
        card.setMainTitle(Html.fromHtml(context.getString(R.string.sot_message,
                cardDateIDuration, mls.translateToReadableFormat(context))));

        return card;
    }

    /**
     * getAllTimeInsightCard
     * This will return back a card which shows the end user on what day did they used their phone the most.
     * @return
     *      Card to the calling program. If we cannot return the card then it will return null.
     */
    public Card getAllTimeInsightCard() {

        // Get the data for the all time count
        //InsightTable iCount = dao.getInsightAllTimeCount();
        CounterTable iCount = dao.getInsightAllTimeCount(DatabaseDAO.COUNTER_TYPE_ON);

        // If iCount is null, then nothing has been set, so might as well exit
        if (iCount == null) {
            return null;
        } else if (iCount.getDate() == null) {
            // If date is null that means nothing populated, so no point to display the card.
            return null;
        }

        // Get the data for the all time duration
        CounterTable iDuration = dao.getInsightAllTimeCount(DatabaseDAO.COUNTER_TYPE_ON_TIME);

        // If iDuration is null, then nothing has been set, so might as well exit
        if (iDuration == null) {
            return null;
        } else if (iDuration.getDate() == null) {
            // If date is null that means nothing populated, so no point to display the card.
            return null;
        }

        // For all time count - primary driver
        // Break down the date into components so we can create the card with nice Text. If there
        // was an issue to convert the date, return null.
        String[] dateRepICount = DateTimeHandler.getDayMonthYearBreakDown(iCount.getDate());
        if (dateRepICount == null) {
            return null;
        }

        // For all time duration
        // Break down the date into components so we can create the card with nice Text. If there
        // was an issue to convert the date, return null.
        String[] dateRepIDuration = DateTimeHandler.getDayMonthYearBreakDown(iDuration.getDate());
        if (dateRepIDuration == null) {
            return null;
        }

        // Set up components for the card
        CypoCard card = new CypoCard(context, "");
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the title of the card
        cardHeader.setTitle(context.getString(R.string.all_time_insight));
        card.addCardHeader(cardHeader);

        // format will be like "Tuesday 15th September"
        // This is for the mount count
        String cardDateICount = dateRepICount[7] + " " +
                dateRepICount[2] + DateTimeHandler.getDayOfMonthSuffix(Integer.parseInt(dateRepICount[2])) + " " +
                dateRepICount[5];

        // format will be like "Tuesday 15th September"
        // This is for the mount duration
        String cardDateIDuration = dateRepIDuration[7] + " " +
                dateRepIDuration[2] + DateTimeHandler.getDayOfMonthSuffix(Integer.parseInt(dateRepIDuration[2])) + " " +
                dateRepIDuration[5];

        MilliSecondsToHoursMinutesSeconds mls = new MilliSecondsToHoursMinutesSeconds(iDuration.getCount());

        // Set the main message
        card.setMainTitle(Html.fromHtml(context.getString(R.string.all_time_insight_card_message,
                cardDateICount, iCount.getCount(), cardDateIDuration, mls.translateToReadableFormat(context))));

        return card;

    }

    /**
     * getMonthInsightCard
     * This will set up the month insight card. This will always look at yesterday date and then
     * check to see if we got any insight for the month. If we do then return a card back.
     * @return
     *      Card to the calling program. If we cannot return the card then it will return null.
     */
    public Card getMonthInsightCard() {

        // Get the on count for the week using yesterday date. We could get more values, therefore
        // we will do a sort on the count to pick to day we have the maximum count. We will
        // exit if we do not have anything
        //InsightTable[] iCount = dao.getInsightMonthCountOn(0, DateTimeHandler.getYearMonth(-1), null);
        CounterTable[] iCount = dao.getInsightByTypeWeekOrMonth(DatabaseDAO.COUNTER_TYPE_ON,
                Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(DateTimeHandler.yesterdayDate())[0]),
                Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(DateTimeHandler.yesterdayDate())[1]),
                0, DatabaseDAO.COUNTER_COUNT + " DESC");

        if (iCount == null) {
            // If we are null, then return null
            return null;
        } else if (iCount.length == 0) {
            // If the length is 0 then exit.
            return null;
        } else if (iCount[0].getDate() == null) {
            // If date is null that means nothing is populated, so no point to display the card.
            return null;
        }

        // Get the duration for the week using yesterday date. We could get more values, therefore
        // we will do a sort on the count to pick to day we have the maximum count. We will
        // exit if we do not have anything
        //InsightTable[] iDuration = dao.getInsightMonthCountOnTime(0, DateTimeHandler.getYearMonth(-1), null);
        CounterTable[] iDuration = dao.getInsightByTypeWeekOrMonth(DatabaseDAO.COUNTER_TYPE_ON_TIME,
                Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(DateTimeHandler.yesterdayDate())[0]),
                Integer.parseInt(DateTimeHandler.getDayMonthYearBreakDown(DateTimeHandler.yesterdayDate())[1]),
                0, DatabaseDAO.COUNTER_COUNT + " DESC");

        if (iDuration == null) {
            // If we are null, then return null
            return null;
        } if (iDuration.length == 0) {
            // If the length is 0 then exit.
            return null;
        } else if (iDuration[0].getDate() == null) {
            // If date is null that means nothing populated, so no point to display the card.
            return null;
        }

        // For month count - primary driver
        // Break down the date into components so we can create the card with nice Text. If there
        // was an issue to convert the date, return null.
        String[] dateRepICount = DateTimeHandler.getDayMonthYearBreakDown(iCount[0].getDate());
        if (dateRepICount == null) {
            return null;
        }

        // For month duration
        // Break down the date into components so we can create the card with nice Text. If there
        // was an issue to convert the date, return null.
        String[] dateRepIDuration = DateTimeHandler.getDayMonthYearBreakDown(iDuration[0].getDate());
        if (dateRepIDuration == null) {
            return null;
        }

        // Set up components for the card
        CypoCard card = new CypoCard(context, "");
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the title of the card. Use primary driver
        cardHeader.setTitle(dateRepICount[5] + " " + dateRepICount[0] + " " + context.getString(R.string.insights_caps));
        card.addCardHeader(cardHeader);

        // format will be like "Tuesday 15th September"
        // This is for the mount count
        String cardDateICount = dateRepICount[7] + " " +
                dateRepICount[2] + DateTimeHandler.getDayOfMonthSuffix(Integer.parseInt(dateRepICount[2])) + " " +
                dateRepICount[5];

        // format will be like "Tuesday 15th September"
        // This is for the mount duration
        String cardDateIDuration = dateRepIDuration[7] + " " +
                dateRepIDuration[2] + DateTimeHandler.getDayOfMonthSuffix(Integer.parseInt(dateRepIDuration[2])) + " " +
                dateRepIDuration[5];

        MilliSecondsToHoursMinutesSeconds mls = new MilliSecondsToHoursMinutesSeconds(iDuration[0].getCount());

        // Set the main message
        card.setMainTitle(Html.fromHtml(context.getString(R.string.month_insight_card_message,
                cardDateICount, iCount[0].getCount(), cardDateIDuration, mls.translateToReadableFormat(context))));

        return card;

    }

    /**
     * This method will create and return the card to the UI.
     * @return
     * Card
     */
    public Card getTodayCard() {

        // Get the data for the wear from the settings table
        long wearLastSync = dao.getSettingsWearLaySync();

        // Set the variables needed to create the card.
        int deviceOnCount = dao.getCounterCountOn(DateTimeHandler.todayDate());
        int deviceOnCountUnlock = dao.getCounterCountUnlock(DateTimeHandler.todayDate());
        long deviceTimeOnCount = dao.getCounterCountOnTime(DateTimeHandler.todayDate());
        int wearOnCount = dao.getCounterCountOnWear(DateTimeHandler.todayDate());
        long wearTimeOnCount = dao.getCounterCountOnTimeWear(DateTimeHandler.todayDate());

        // Some variables for the wear text
        String wearLastSyncText = "";
        String wearText = "";

        // If wearOnCount is 0 then you do not have a watch, so only do the below if you have a watch.
        if (wearOnCount != 0) {

            // Get the last time the wear device was last sync and put it into a string ready to add to the card
            long wearLastSyncElapse = DateTimeHandler.todayTimestamp() - wearLastSync;
            MilliSecondsToHoursMinutesSeconds mls = new MilliSecondsToHoursMinutesSeconds(wearLastSyncElapse);
            wearLastSyncText = context.getString(R.string.wear_last_sync, mls.translateToReadableFormatLoose(context));

            // Get the period of time that the android wear device has been on for and then set the text
            MilliSecondsToHoursMinutesSeconds mwt = new MilliSecondsToHoursMinutesSeconds(wearTimeOnCount);
            wearText = context.getString(R.string.today_card_message_wear,
                    wearOnCount,
                    mwt.translateToReadableFormat(context));
        }

        // Create the card, and pass the wear last sync text.
        CypoCard card = new CypoCard(context, wearLastSyncText);
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the title of the card.
        cardHeader.setTitle(context.getString(R.string.today_insight));
        card.addCardHeader(cardHeader);

        // Get the period of time that the device has been on for today.
        MilliSecondsToHoursMinutesSeconds mpt = new MilliSecondsToHoursMinutesSeconds(deviceTimeOnCount);

        // Add the text to the card
        card.setMainTitle(Html.fromHtml(context.getString(R.string.today_card_message,
                deviceOnCount,
                deviceOnCountUnlock,
                mpt.translateToReadableFormat(context)) + wearText));

        return card;
    }

    /**
     * getWeekInsightCard
     * This will set up the week insight card. This will always look at yesterday date and then
     * check to see if we got any insight for the week. If we do then return a card back.
     * @return
     *      Card to the calling program. If we cannot return the card then it will return null.
     */
    public Card getWeekInsightCard() {

        // Get the on count for the week using yesterday date. We could get more values, therefore
        // we will do a sort on the count to pick to day we have the maximum count. We will
        // exit if we do not have anything
        //InsightTable[] iCount = dao.getInsightWeekCountOn(0, DateTimeHandler.getYearWeek(-1, null), null);
        CounterTable[] iCount = dao.getInsightByTypeWeekOrMonth(DatabaseDAO.COUNTER_TYPE_ON, 0, 0,
                DateTimeHandler.getYearWeek(-1, null), DatabaseDAO.COUNTER_COUNT + " DESC");

        if (iCount == null) {
            // If we are null, then return null
            return null;
        } else if (iCount.length == 0) {
            // If the length is 0 then exit.
            return null;
        } else if (iCount[0].getDate() == null) {
            // If date is null that means nothing populated, so no point to display the card.
            return null;
        }

        // Get the duration for the week using yesterday date. We could get more values, therefore
        // we will do a sort on the count to pick to day we have the maximum count. We will
        // exit if we do not have anything
        //InsightTable[] iDuration = dao.getInsightWeekCountOnTime(0, DateTimeHandler.getYearWeek(-1, null), null);
        CounterTable[] iDuration = dao.getInsightByTypeWeekOrMonth(DatabaseDAO.COUNTER_TYPE_ON_TIME, 0, 0,
                DateTimeHandler.getYearWeek(-1, null), DatabaseDAO.COUNTER_COUNT + " DESC");

        if (iDuration == null) {
            // If we are null, then return null
            return null;
        } else if (iDuration.length == 0 ) {
            // If the length is 0 then exit.
            return null;
        } else if (iDuration[0].getDate() == null) {
            // If date is null that means nothing populated, so no point to display the card.
            return null;
        }

        // For week count - primary driver
        // Break down the date into components so we can create the card with nice Text. If there
        // was an issue to convert the date, return null.
        String[] dateRepICount = DateTimeHandler.getDayMonthYearBreakDown(iCount[0].getDate());
        if (dateRepICount == null) {
            return null;
        }

        // For week duration
        // Break down the date into components so we can create the card with nice Text. If there
        // was an issue to convert the date, return null.
        String[] dateRepIDuration = DateTimeHandler.getDayMonthYearBreakDown(iDuration[0].getDate());
        if (dateRepIDuration == null) {
            return null;
        }

        // Set up components for the card
        CypoCard card = new CypoCard(context, "");
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the title of the card. Use primary driver
        cardHeader.setTitle(context.getString(R.string.this_weeks_insights));
        card.addCardHeader(cardHeader);

        // format will be like "Tuesday 15th September"
        // This is for the week count
        String cardDateICount = dateRepICount[7] + " " +
                dateRepICount[2] + DateTimeHandler.getDayOfMonthSuffix(Integer.parseInt(dateRepICount[2])) + " " +
                dateRepICount[5];

        // format will be like "Tuesday 15th September"
        // This is for the week duration
        String cardDateIDuration = dateRepIDuration[7] + " " +
                dateRepIDuration[2] + DateTimeHandler.getDayOfMonthSuffix(Integer.parseInt(dateRepIDuration[2])) + " " +
                dateRepIDuration[5];

        MilliSecondsToHoursMinutesSeconds mls = new MilliSecondsToHoursMinutesSeconds(iDuration[0].getCount());

        // Set the main message
        card.setMainTitle(Html.fromHtml(context.getString(R.string.week_insight_card_message,
                cardDateICount, iCount[0].getCount(), cardDateIDuration, mls.translateToReadableFormat(context))));

        return card;

    }

    /**
     * This method will generate the card for yesterday's insight
     * @return the card with all the data populated
     *      will also return null if no data exist for yesterday.
     */
    public Card getYesterdayCard() {

        // Set the variables needed to create the card.
        int deviceOnCount = dao.getCounterCountOn(DateTimeHandler.yesterdayDate());
        int deviceOnCountUnlock = dao.getCounterCountUnlock(DateTimeHandler.yesterdayDate());
        long deviceTimeOnCount = dao.getCounterCountOnTime(DateTimeHandler.yesterdayDate());
        int wearOnCount = dao.getCounterCountOnWear(DateTimeHandler.yesterdayDate());
        long wearTimeOnCount = dao.getCounterCountOnTimeWear(DateTimeHandler.yesterdayDate());

        // If deviceOnCount is 0, then we have no data for yesterday. If this is the case then
        // return null.
        if (deviceOnCount == 0) {
            return null;
        }

        // Some variables for the wear text
        String wearText = "";

        // If wear count is not 0, then we have watch data to return back to the card.
        if (wearOnCount != 0) {

            // Get the period of time that the android wear device has been on for and then set the text
            MilliSecondsToHoursMinutesSeconds mwt = new MilliSecondsToHoursMinutesSeconds(wearTimeOnCount);
            wearText = "\n\n" + context.getString(R.string.yesterday_card_message_wear,
                    wearOnCount,
                    mwt.translateToReadableFormat(context));
        }

        CypoCard card = new CypoCard(context, "");
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the header for the card.
        cardHeader.setTitle(context.getString(R.string.yesterday_insight));
        card.addCardHeader(cardHeader);

        // Get the period of time that the device has been on for today.
        MilliSecondsToHoursMinutesSeconds mpt = new MilliSecondsToHoursMinutesSeconds(deviceTimeOnCount);

        // Set the text for the card
        card.setMainTitle(Html.fromHtml(context.getString(R.string.yesterday_card_message,
                deviceOnCount,
                deviceOnCountUnlock,
                mpt.translateToReadableFormat(context)) + wearText));

        return card;
    }

    /**
     * This will return to the system the card to welcome the user to the application. If the user
     * clicks on the card, the card will be removed from the screen. To achive this, a cheat..
     * just reopen the same activity again.
     * @return the welcome card
     */
    public Card welcomeCard() {

        CypoCard card = new CypoCard(context, "");
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set Title
        cardHeader.setTitle(context.getString(R.string.welcome));
        card.addCardHeader(cardHeader);

        // Set Message
        card.setMainTitle(Html.fromHtml(context.getString(R.string.welcome_msg)));

        // Set the card so that it is now clickable
        card.setClickable(true);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                // Tell the system we have looked at the welcome card so it will not be shown again
                DataSharedPreferencesDAO dsp = new DataSharedPreferencesDAO(context);
                dsp.putDataBoolean(DataSharedPreferencesDAO.KEY_WELCOME_COMPLETE, true);

                // Refresh the card again by calling the same activity.. kinda a hack :)
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });

        return card;
    }

    /**
     * This card will be used create a card if we have a new wear device connecting to our app
     * It will prompt the user to manage there devices
     * @return
     * Card
     */
    public Card newWearDeviceCard() {

        CypoCard card = new CypoCard(context, "");
        CypoCardHeader cardHeader = new CypoCardHeader(context);

        // Set the header
        cardHeader.setTitle(context.getString(R.string.new_wear_device));
        cardHeader.setTextColourResourceId(R.color.cypo_white);
        card.addCardHeader(cardHeader);

        // Set the text and also the colour
        card.setMainTitle(context.getString(R.string.new_wear_device_msg));
        card.setMainTextColour(R.color.cypo_white);

        // Set the background to the primary color
        card.setBackgroundColorResourceId(R.color.cypo_primary);

        // Set if it is clickable.
        card.setClickable(true);

        //Set onClick listener
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                context.startActivity(new Intent(context, WearManagementActivity.class));

            }
        });

        return card;
    }

}
