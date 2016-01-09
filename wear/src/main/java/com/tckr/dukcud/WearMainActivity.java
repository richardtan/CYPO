package com.tckr.dukcud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.tckr.dukcud.wear.data.DataStore;
import com.tckr.dukcud.wear.service.ScreenService;

/**
 * Do not use, the below was used for testing purposes only, however left in here just in case
 * I need to refer to anything
 */
public class WearMainActivity extends Activity {

    /**
     * ANDROID WEAR
     * To get it working, make sure debugging is turned on the watch
     * Make sure bluetooth debugging is enabled on the app
     * Run the below:
     * adb forward tcp:4444 localabstract:/adb-hub; adb connect localhost:4444
     */

    private TextView mTextView;
    private Activity itself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_main_activity);
        itself = this;

        /****
         * Start Intent for screen service
         ****/

        Intent screenIntent = new Intent(this, ScreenService.class);
        this.startService(screenIntent);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                String returnString = getString(R.string.hello_round);

                DataStore ds = new DataStore(itself);
                String[] todayKey = ds.getTodayKeys(0);

                try {
                    returnString += "\nNumber of screen on: " + ds.getDataLong(todayKey[DataStore.ARRAY_KEY_COUNT]);
                    returnString += "\nDuration: " + ds.getDataLong(todayKey[DataStore.ARRAY_KEY_DURATION]);
                    returnString += "\nDate: " + ds.getDataString(todayKey[DataStore.ARRAY_KEY_DATE]);
                    
                } catch (Exception e) {}

                mTextView.setText(returnString);
            }
        });
    }
}
