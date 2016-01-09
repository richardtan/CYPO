package com.tckr.dukcud.wear.data;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.tckr.dukcud.wear.service.ScreenService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to send data back to the Android application on the phone.
 *
 * From Play Service 7.3 we can have multiple wear devices connected to a phone. As we are only sending
 * messages, applications where there is the listener setup will receive the message. In future we
 * might get multiple devices able to talk to each other. The below will attempt to send the messages
 * to all devices. It is up to the application to accept them or just reject them.
 */
public class SendDataToPhone {

    private static final String TAG = "wear.SendDataToPhone";

    // One second timeout
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private static final String MESSAGE_PATH_DATA = "/cypo_wear_data";
    private static final String MESSAGE_PATH_OPEN_APP = "/cypo_wear_open_app";

    // Set up some global variables needed for this class
    private GoogleApiClient client;
    private String nodeId;
    private Context context;
    private byte[] dataPacket;

    /**
     * Constructor to set up the GoogleAPIClient
     * @param context -
     */
    public SendDataToPhone(Context context) {
        client = this.getGoogleApiClient(context);
        this.context = context;
        Log.v(TAG, "Complete constructor for SendDataToPhone");
    }

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context -
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Send the last 7 days worth of data to the phone or connected device. We sending all 7 days
     * every time, plus sending the all time stats as well
     */
    public void sendDataToHandheld() {

        // Create a DataMap and DataStore object
        DataMap dataMap = new DataMap();
        DataStore ds = new DataStore(context);

        // Values for Monday
        dataMap.putLong(DataStore.KEY_MON_COUNT, ds.getDataLong(DataStore.KEY_MON_COUNT));
        dataMap.putLong(DataStore.KEY_MON_DURATION, ds.getDataLong(DataStore.KEY_MON_DURATION));
        dataMap.putString(DataStore.KEY_MON_DATE, ds.getDataString(DataStore.KEY_MON_DATE));

        // Values for Tuesday
        dataMap.putLong(DataStore.KEY_TUE_COUNT, ds.getDataLong(DataStore.KEY_TUE_COUNT));
        dataMap.putLong(DataStore.KEY_TUE_DURATION, ds.getDataLong(DataStore.KEY_TUE_DURATION));
        dataMap.putString(DataStore.KEY_TUE_DATE, ds.getDataString(DataStore.KEY_TUE_DATE));

        // Values for Wednesday
        dataMap.putLong(DataStore.KEY_WED_COUNT, ds.getDataLong(DataStore.KEY_WED_COUNT));
        dataMap.putLong(DataStore.KEY_WED_DURATION, ds.getDataLong(DataStore.KEY_WED_DURATION));
        dataMap.putString(DataStore.KEY_WED_DATE, ds.getDataString(DataStore.KEY_WED_DATE));

        // Values for Thursday
        dataMap.putLong(DataStore.KEY_THU_COUNT, ds.getDataLong(DataStore.KEY_THU_COUNT));
        dataMap.putLong(DataStore.KEY_THU_DURATION, ds.getDataLong(DataStore.KEY_THU_DURATION));
        dataMap.putString(DataStore.KEY_THU_DATE, ds.getDataString(DataStore.KEY_THU_DATE));

        // Values for Friday
        dataMap.putLong(DataStore.KEY_FRI_COUNT, ds.getDataLong(DataStore.KEY_FRI_COUNT));
        dataMap.putLong(DataStore.KEY_FRI_DURATION, ds.getDataLong(DataStore.KEY_FRI_DURATION));
        dataMap.putString(DataStore.KEY_FRI_DATE, ds.getDataString(DataStore.KEY_FRI_DATE));

        // Values for Saturday
        dataMap.putLong(DataStore.KEY_SAT_COUNT, ds.getDataLong(DataStore.KEY_SAT_COUNT));
        dataMap.putLong(DataStore.KEY_SAT_DURATION, ds.getDataLong(DataStore.KEY_SAT_DURATION));
        dataMap.putString(DataStore.KEY_SAT_DATE, ds.getDataString(DataStore.KEY_SAT_DATE));

        // Values for Sunday
        dataMap.putLong(DataStore.KEY_SUN_COUNT, ds.getDataLong(DataStore.KEY_SUN_COUNT));
        dataMap.putLong(DataStore.KEY_SUN_DURATION, ds.getDataLong(DataStore.KEY_SUN_DURATION));
        dataMap.putString(DataStore.KEY_SUN_DATE, ds.getDataString(DataStore.KEY_SUN_DATE));

        // Default values
        dataMap.putLong(DataStore.KEY_DEFAULT_COUNT, ds.getDataLong(DataStore.KEY_DEFAULT_COUNT));
        dataMap.putLong(DataStore.KEY_DEFAULT_DURATION, ds.getDataLong(DataStore.KEY_DEFAULT_DURATION));

        // Values to identify the wear device
        dataMap.putString(DataStore.KEY_BUILD_MODEL, Build.MODEL);
        dataMap.putString(DataStore.KEY_BUILD_SERIAL, Build.SERIAL);

        // Now convert the DataMap to a byte array to be sent to the handheld device...
        dataPacket = dataMap.toByteArray();

        new Thread(new Runnable() {
            @Override
            public void run() {

            client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

            // Find if we got any connected device, or node which is a device
            NodeApi.GetConnectedNodesResult result =
                    Wearable.NodeApi.getConnectedNodes(client).await();

            List<Node> nodes = result.getNodes();

            // Test if we got any nodes
            if (nodes != null) {

                // Iterate through all the nodes and try and send the message
                for (int i = 0; i < nodes.size(); i++) {
                    nodeId = nodes.get(i).getId();

                    // Send the data to handheld
                    MessageApi.SendMessageResult sendMessageResult =
                            Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE_PATH_DATA, dataPacket)
                                    .await(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

                    Log.v(TAG, "sendDataToHandheld() - Message sent successfully? on node " + i + ": " +
                            sendMessageResult.getStatus().isSuccess());

                    // If message was successfully sent to the handheld then on the ScreenService class
                    // update the static variable timestampOfLastSendDataToPhone
                    if (sendMessageResult.getStatus().isSuccess()) {

                        // Set the new timestamp for now.
                        ScreenService.timestampOfLastSendDataToPhone = DataStore.todayTimestamp();
                    }
                }
            }
            client.disconnect();

            }
        }).start();
    }

    /**
     * Send a message to the phone to instruct it to open the application.
     */
    public void openPhoneApp() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

                // Find if we got any connected device, or node which is a device
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();

                List<Node> nodes = result.getNodes();

                // Test if we got any nodes
                if (nodes != null) {

                    boolean hasSentAlready = false;

                    // Iterate through all the nodes and try and send the message
                    for (int i = 0; i < nodes.size(); i++) {
                        nodeId = nodes.get(i).getId();

                        // Send the data to handheld
                        MessageApi.SendMessageResult sendMessageResult =
                                Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE_PATH_OPEN_APP, null)
                                        .await(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

                        // If success then show success animation, else show fail animation
                        if (sendMessageResult.getStatus().isSuccess() && !hasSentAlready) {
                            // Show animation that we were successfully at opening app on the phone
                            Intent intentAnimation = new Intent(context, ConfirmationActivity.class);
                            intentAnimation.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                            intentAnimation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentAnimation, ActivityOptions.makeCustomAnimation(context, 0, 0).toBundle());

                            // Say we have sent already so we do not show the animation again
                            hasSentAlready = true;
                        }
                    }
                }
                client.disconnect();

            }
        }).start();

    }
}
