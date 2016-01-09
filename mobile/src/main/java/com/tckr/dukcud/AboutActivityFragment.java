package com.tckr.dukcud;

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

/**
 * Provides the user interface for the about screen
 */
public class AboutActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Set the text for the about message
        TextView aboutMsg = (TextView) view.findViewById(R.id.fa_message);
        aboutMsg.setMovementMethod(LinkMovementMethod.getInstance());
        aboutMsg.setText(Html.fromHtml(getString(R.string.about_msg)));

        // Get the setting for the switch to enable debug mode
        final DataSharedPreferencesDAO dspDAO = new DataSharedPreferencesDAO(this.getActivity());
        boolean debugModeOn = dspDAO.getDataBoolean(DataSharedPreferencesDAO.KEY_DEBUG_MODE);

        // Set the switch to on or off for the UI
        Switch fa_debug = (Switch) view.findViewById(R.id.fa_debug);
        fa_debug.setChecked(debugModeOn);

        // Set the listener
        fa_debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dspDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_DEBUG_MODE, isChecked);
            }
        });

        return view;
    }

}
