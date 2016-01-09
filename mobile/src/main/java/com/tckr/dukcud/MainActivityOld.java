package com.tckr.dukcud;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tckr.dukcud.data.CounterTable;
import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.data.DatabaseDAO;
import com.tckr.dukcud.data.DateTimeHandler;
import com.tckr.dukcud.data.MilliSecondsToHoursMinutesSeconds;
import com.tckr.dukcud.data.ScreenLogTable;
import com.tckr.dukcud.view.GetMenu;

/**
 * This is the debug activity. This was the first activity made on this app. It contains lots of
 * data that allowed me to debug the app.
 */
public class MainActivityOld extends AppCompatActivity {

    private static final String TAG = "MainActivityOld";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_old);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // Allows me to show a card to open up the android wear new device card
        DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(this);
        sharedPreferencesDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_NEW_WEAR_DEVICE, true);

        /** BELOW IS FOR TESTING **
        DatabaseDAO db = new DatabaseDAO(this);
        db.open();
        //db.isCurrentWearDevice("Nexus R", "1234567890");
        db.forTesting();
        db.close();
        /** END **/

        //System.out.println(DateTimeHandler.getCurrentYearMonth() + " : week : " + DateTimeHandler.getCurrentYearWeek());

    }

    /**
     * Create the menu
     * @param menu - what the system gives
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Before we inflate the menu, lets do something just incase
     * @param menu - default
     * @return - true
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return GetMenu.getOnPrepareOptionsMenu(menu, this);
    }

    /**
     * Handles when a user select something
     * @param item - item seleted
     * @return - true?
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (GetMenu.getOnOptionsItemSelected(item, this)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_body_fragment, container, false);

            TextView tv = (TextView) rootView.findViewById(R.id.temp);

            DatabaseDAO dao = new DatabaseDAO(inflater.getContext());
            dao.open();
            ScreenLogTable[] slt = dao.getScreenLog(50);

            String returnString = "\nDebug Mode - This is something I used for debugging. Note, this will produce a Android Wear card on the main screen.\n";

            returnString += "\nTotal number of screen on is: " + dao.getCounterCountOn(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);

            returnString += "\nTotal number of unlocks by user is: " + dao.getCounterCountUnlock(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT);

            MilliSecondsToHoursMinutesSeconds m1 = new MilliSecondsToHoursMinutesSeconds(dao.getCounterCountOnTime(DateTimeHandler.todayDate()));
            returnString += "\n\nToday your screen was on for: " + m1.getHours() + " hours, " + m1.getMinutes() + " minutes and " + m1.getSeconds() + " seconds";

            MilliSecondsToHoursMinutesSeconds m = new MilliSecondsToHoursMinutesSeconds(dao.getCounterCountOnTime(DateTimeHandler.yesterdayDate()));
            returnString += "\n\nYesterday your screen was on for: " + m.getHours() + " hours, " + m.getMinutes() + " minutes and " + m.getSeconds() + " seconds";

            MilliSecondsToHoursMinutesSeconds m2 = new MilliSecondsToHoursMinutesSeconds(dao.getCounterCountOnTime(DatabaseDAO.COUNTER_DATE_VALUE_DEFAULT));
            returnString += "\n\nTotal screen on time is: " + m2.getDays() + " days, " + m2.getHours() + " hours, " + m2.getMinutes() + " minutes and " + m2.getSeconds() + " seconds";

            CounterTable alltimecount = dao.getInsightAllTimeCount(DatabaseDAO.COUNTER_TYPE_ON);
            if(alltimecount != null) {
                returnString += "\n\nOn " + alltimecount.getDate() + " you turn on your device the most, having turned it on " + alltimecount.getCount() + " times";
            }
            returnString += "\n\nDATE|INTENT|INTENT NO.|TIMESTAMP|TIMEZONE\n";
            try {
                for (ScreenLogTable i : slt) {
                    returnString += i.getDate() + " | " + i.getIntentString() + " | " + i.getIntent() +
                            " | " + i.getTimestamp() + " | " + i.getTimezone() + "\n";
                }
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e);
            }

            //Display all row count so we can look at Storage options.
            returnString += dao.getAllRowCount() + "\n\n";

            DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(getActivity());

            returnString += "Battery SOT variables\n" +
                    DataSharedPreferencesDAO.KEY_LAST_CHARGE_TIME + ": " +
                    sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_LAST_CHARGE_TIME) + "\n" +
                    DataSharedPreferencesDAO.KEY_LAST_OFF_CHARGE_TIME + ": " +
                    sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_LAST_OFF_CHARGE_TIME) + "\n" +
                    DataSharedPreferencesDAO.KEY_ON_TIME_RECORD + ": " +
                    sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_ON_TIME_RECORD) + "\n" +
                    DataSharedPreferencesDAO.KEY_OFF_TIME_RECORD + ": " +
                    sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_OFF_TIME_RECORD) + "\n" +
                    DataSharedPreferencesDAO.KEY_TOTAL_TIME_RECORD + ": " +
                    sharedPreferencesDAO.getDataLong(DataSharedPreferencesDAO.KEY_TOTAL_TIME_RECORD) + "\n";

            tv.setText(returnString);
            dao.close();

            return rootView;
        }
    }
}
