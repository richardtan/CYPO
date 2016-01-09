package com.tckr.dukcud;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.data.DatabaseDAO;
import com.tckr.dukcud.data.SendDataToWear;
import com.tckr.dukcud.data.SettingsTable;

/**
 * Provides the user interface for the wear management
 */
public class WearManagementActivityFragment extends Fragment {

    String[] wearSerialNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wear_management, container, false);

        // Get the radio group so we can start inserting the radio buttons for the
        // different Android Wear devices
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.fwm_radio_group);

        // Set up database connection
        DatabaseDAO dao = new DatabaseDAO(view.getContext());
        dao.open();

        // Get the list of Android wear devices
        SettingsTable[] settingsTables = dao.getWearDevices();

        // Check if we are null, if we are do nothing
        if (settingsTables != null) {

            wearSerialNo = new String[settingsTables.length];

            // Loop through the android wear devices
            for (int i = 0; i < settingsTables.length; i++) {

                // Create a radio button
                RadioButton radioButton = new RadioButton(view.getContext());
                radioButton.setText(settingsTables[i].getDescription() + ": " + settingsTables[i].getName());
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getContext().getResources().getDimension(R.dimen.cypo_normal_text_size));
                radioButton.setTextColor(view.getContext().getResources().getColor(R.color.cypo_black));
                radioButton.setSaveEnabled(false);
                radioButton.setId(i);

                // Add the radio button to the group
                radioGroup.addView(radioButton, i);

                // See if you are the default and if you are check it
                if (settingsTables[i].getInt_value() == DatabaseDAO.SETTINGS_INT_VALUE_WEAR_DEVICE_SHOW) {
                    radioGroup.check(i);
                }

                // Add the serial number
                wearSerialNo[i] = settingsTables[i].getName();
            }
        }

        // Set up some listeners so we can action something
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Toast.makeText(getActivity(), group.getCheckedRadioButtonId() + " ID " + wearSerialNo[group.getCheckedRadioButtonId()], Toast.LENGTH_SHORT).show();

                // Set up database connection
                DatabaseDAO dao = new DatabaseDAO(getActivity());
                dao.open();

                // Set the wear device to be shown on the dashboard
                dao.setWearDeviceToShow(wearSerialNo[group.getCheckedRadioButtonId()]);
                dao.close();
            }
        });

        // Set the value for new wear device to false
        DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(getActivity());
        sharedPreferencesDAO.putDataBoolean(DataSharedPreferencesDAO.KEY_NEW_WEAR_DEVICE, false);

        // Get the priority radio group
        RadioGroup radioPriority = (RadioGroup) view.findViewById(R.id.fwm_radio_priority);

        // Get the value stored by the app
        String defaultCardPriority = sharedPreferencesDAO.getDataString(DataSharedPreferencesDAO.KEY_WEAR_CARD_PRIORITY);
        if ("".equals(defaultCardPriority)) {
            defaultCardPriority = getString(R.string.bottom);
        }

        // Get the names that we want to set
        String[] rbCardName = {getString(R.string.top),
                getString(R.string.bottom),
                getString(R.string.let_android_decide)};

        // Create the radio buttons
        for (int i = 0; i < rbCardName.length; i++) {

            // Create a radio button
            RadioButton radioButton = new RadioButton(view.getContext());
            radioButton.setText(rbCardName[i]);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getContext().getResources().getDimension(R.dimen.cypo_normal_text_size));
            radioButton.setTextColor(view.getContext().getResources().getColor(R.color.cypo_black));
            radioButton.setId(i);
            radioButton.setSaveEnabled(false); // Weird behaviour in Android when rotating screen. Used to solve it

            // Add the radio button to the group
            radioPriority.addView(radioButton, i);

            // See if you are the default and if you are check it
            if (defaultCardPriority.equals(rbCardName[i])) {
                radioPriority.check(i);
            }
        }

        // Set up some listeners so we can action something
        radioPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Get the names that we want to set
                String[] rbCardName = {getString(R.string.top),
                        getString(R.string.bottom),
                        getString(R.string.let_android_decide)};

                DataSharedPreferencesDAO sharedPreferencesDAO = new DataSharedPreferencesDAO(getActivity());
                sharedPreferencesDAO.putDataString(DataSharedPreferencesDAO.KEY_WEAR_CARD_PRIORITY,
                        rbCardName[group.getCheckedRadioButtonId()]);

                // Send the new card priority to wear. Note if you have more than 1 Android wear device
                // this setting will be sent to all Android wear device.
                SendDataToWear sendDataToWear = new SendDataToWear(getActivity());
                sendDataToWear.addToDataMap(DataSharedPreferencesDAO.KEY_WEAR_CARD_PRIORITY,
                        rbCardName[group.getCheckedRadioButtonId()]);
                sendDataToWear.sendDataToWearCardPriority();

            }
        });

        dao.close();
        return view;
    }

}
