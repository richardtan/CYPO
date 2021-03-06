package com.tckr.dukcud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tckr.dukcud.service.NotificationService;
import com.tckr.dukcud.service.ScreenService;
import com.tckr.dukcud.view.GetMenu;

/**
 * First activity that is launched when the app is started
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Used to start all the Service needed for the app to work
        this.startAllService();
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
     * startAllService()
     * Used to start all the services. Used mainly so that if something FC then at least we can
     * recover just by opening the app.
     */
    public void startAllService() {

        /****
         * Start Intent for screen service
         ****/
        Intent screenIntent = new Intent(this, ScreenService.class);
        this.startService(screenIntent);

        // Create Notification
        NotificationService.restartNotification(this);
        
    }
}
