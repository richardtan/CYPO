package com.tckr.dukcud.view;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.tckr.dukcud.AboutActivity;
import com.tckr.dukcud.MainActivityOld;
import com.tckr.dukcud.R;
import com.tckr.dukcud.WearManagementActivity;
import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.data.DatabaseDAO;
import com.tckr.dukcud.data.SettingsTable;

/**
 * Class to get the menu for the activities
 */
public class GetMenu {

    public static boolean getOnPrepareOptionsMenu(Menu menu, Context context) {

        DatabaseDAO dao = new DatabaseDAO(context);
        DataSharedPreferencesDAO dspDAO = new DataSharedPreferencesDAO(context);
        dao.open();

        // If getWearDevice has values, that means we have a wear device, so lets see the menu
        MenuItem menuManageWear = menu.findItem(R.id.main_menu_manage_wear);
        SettingsTable[] wearDevices = dao.getWearDevices();
        if (wearDevices == null) {
            menuManageWear.setVisible(false);
        } else if (wearDevices.length < 1) {
            menuManageWear.setVisible(false);
        }

        // Check if debug mode is on
        MenuItem menuDebug = menu.findItem(R.id.main_menu_debug);
        if (!dspDAO.getDataBoolean(DataSharedPreferencesDAO.KEY_DEBUG_MODE)) {
            menuDebug.setVisible(false);
        }

        dao.close();
        return true;
    }

    /**
     * getOnOptionsItemSelected
     * Generic method for menus
     * @param item
     * @param context
     * @return true or false
     */
    public static boolean getOnOptionsItemSelected(MenuItem item, Context context) {

        int id = item.getItemId();

        // Click on About
        if (id == R.id.main_menu_about) {
            context.startActivity(new Intent(context, AboutActivity.class));
            return true;
        }

        // Click on Wear Management
        if (id == R.id.main_menu_manage_wear) {

            context.startActivity(new Intent(context, WearManagementActivity.class));
            return true;
        }

        // Click on Debug
        if (id == R.id.main_menu_debug) {
            context.startActivity(new Intent(context, MainActivityOld.class));
            return true;
        }

        return false;
    }
}
