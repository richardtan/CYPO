package com.tckr.dukcud.data;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class will be used to send data back to the wear device
 */
public class SendDataToWear {

    private static final String TAG = "SendDataToWear";
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private static final String MESSAGE_PATH_DATA = "/cypo_phone_data";
    private static final String MESSAGE_PATH_WEAR_CARD_PRIORITY = "/cypo_wear_card_priority";

    // Set up some global variables needed for this class
    private GoogleApiClient client;
    private String nodeId;
    private byte[] dataPacket;
    private DataMap dataMap;

    /**
     * Constructor to set up the GoogleAPIClient
     * @param context -
     */
    public SendDataToWear(Context context) {
        client = this.getGoogleApiClient(context);
        this.dataMap = new DataMap();
        Log.v(TAG, "Complete constructor for SendDataToWear");
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
     * Method to send data to the android wear watch. This will send the count of the watch data
     * which is on the phone
     */
    public void sendDataToWear() {

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

                        Log.v(TAG, "sendDataToWear() - Message sent successfully? on node " + i + ": " +
                                sendMessageResult.getStatus().isSuccess());

                    }
                }
                client.disconnect();

            }
        }).start();
    }

    /**
     * Send the card priority to wear
     */
    public void sendDataToWearCardPriority() {

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
                                Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE_PATH_WEAR_CARD_PRIORITY, dataPacket)
                                        .await(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

                        Log.v(TAG, "sendDataToWearCardPriority() - Message sent successfully? on node " + i + ": " +
                                sendMessageResult.getStatus().isSuccess());

                    }
                }
                client.disconnect();

            }
        }).start();
    }

    /**
     * addToDataMap - use for a value that is of type String
     * @param key - The key
     * @param value - String type
     */
    public void addToDataMap(String key, String value) {
        dataMap.putString(key, value);
    }

    /**
     * addToDataMap - use for a value that is of type int
     * @param key - The key
     * @param value - int type
     */
    public void addToDataMap(String key, int value) {
        dataMap.putInt(key, value);
    }

    /**
     * addToDataMap - use for a value that is of type int
     * @param key - The key
     * @param value - int type
     */
    public void addToDataMap(String key, long value) {
        dataMap.putLong(key, value);
    }

}
