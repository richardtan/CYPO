package com.tckr.dukcud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.service.ScreenService;

/**
 * Provides the user interface for the about screen
 */
public class AboutActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = this.getActivity();

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Set the text for the about message
        TextView aboutMsg = (TextView) view.findViewById(R.id.fa_message);
        aboutMsg.setMovementMethod(LinkMovementMethod.getInstance());
        aboutMsg.setText(Html.fromHtml(getString(R.string.about_msg)));

        // Get the setting for the switch to enable debug mode, disable notification and foreground service
        final DataSharedPreferencesDAO dspDAO = new DataSharedPreferencesDAO(this.getActivity());
        boolean debugModeOn = dspDAO.getDataBoolean(DataSharedPreferencesDAO.KEY_DEBUG_MODE);
        boolean disableNotification = dspDAO.getDataBoolean(DataSharedPreferencesDAO.KEY_NOTIFICATION_DISABLE);
        boolean foregroundOn = ScreenService.isForegroundServiceEnable(this.getActivity());

        // Set the switch to on or off debug mode
        Switch fa_debug = (Switch) view.findViewById(R.id.fa_debug);
        fa_debug.setChecked(debugModeOn);

        // Set the listener
        fa_debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dspDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_DEBUG_MODE, isChecked);
            }
        });

        // Set the switch to on or off notification
        Switch fa_notify = (Switch) view.findViewById(R.id.fa_notify);
        fa_notify.setChecked(disableNotification);

        // Set the listener
        fa_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dspDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_NOTIFICATION_DISABLE, isChecked);
            }
        });

        // Set the switch to on or off the foreground. If you are part of the manufacture device it will be turned on by default.
        Switch fa_foreground = (Switch) view.findViewById(R.id.fa_foreground);
        fa_foreground.setChecked(foregroundOn);

        // Set the listener for the foreground service button clicker
        fa_foreground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                dspDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_FOREGROUND_SERVICE, isChecked);
                dspDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_FOREGROUND_SERVICE_SET_ONCE, true);

                // Call up the service as the service handles the start up for the foreground service
                // and stopping of the foreground service
                Intent screenIntent = new Intent(context, ScreenService.class);
                context.startService(screenIntent);
            }
        });

        return view;
    }

}
