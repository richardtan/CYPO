package com.tckr.dukcud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tckr.dukcud.view.GetMenu;

/**
 * Activity to manage the wear settings if a device is available
 */
public class WearManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_management);
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
}
