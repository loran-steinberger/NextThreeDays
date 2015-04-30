package com.example.loran.nextthreedays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.loran.nextthreedays.MainActivity;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pebble {

    long rec = 0;

    MainActivity context;
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("66905270-be23-4a72-958f-a13def9b089b");
    int DATA_PAYLOAD = 1;
    int dataReceivedIndex = DATA_PAYLOAD;
    int trial = 0;

    private class PebbleConnectionReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //Log.i(MainActivity.TAG, "Pebble connected!");
        }
    }

    private class PebbleDisconnectionReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //Log.i(MainActivity.TAG, "Pebble Disconnected!");
        }
    }

    private class PebbleAckInfoReciever extends PebbleKit.PebbleAckReceiver {


        protected PebbleAckInfoReciever(UUID subscribedUuid) {
            super(subscribedUuid);
        }

        @Override
        public void receiveAck(Context context, int transactionId) {
            //Log.i(MainActivity.TAG, "Received ack for transaction " + transactionId + " trial:" + trial);
            trial = 0;
            if (transactionId == dataReceivedIndex) {
                dataReceivedIndex++;
            }
        }
    }



    private class PebbleNackInfoReceiver extends PebbleKit.PebbleNackReceiver {

        protected PebbleNackInfoReceiver(UUID subscribedUuid) {
            super(subscribedUuid);
        }

        @Override
        public void receiveNack(Context context, int transactionId) {
            trial++;
            //Log.i(MainActivity.TAG, "Received nack for transaction " + transactionId + " trial:" + trial);

        }
    }

    private class PebbleDataReceiver extends PebbleKit.PebbleDataReceiver {
        protected PebbleDataReceiver(UUID subscribedUuid) { super (subscribedUuid);}

        @Override
        public void receiveData(final Context context, final int transId, final PebbleDictionary data) {

            //Log.i(MainActivity.TAG, "Received value=" + data.getInteger(0) + " for key: 0");
            rec = data.getInteger(0);
            //1 is rock
            //2 is paper
            //3 is scissors
            PebbleKit.sendAckToPebble(context, transId);
        }
    }

    public long getRec() { return rec;}

    public void initConnection() {
        PebbleKit.startAppOnPebble(context, PEBBLE_APP_UUID);
        PebbleKit.registerPebbleConnectedReceiver(context, new PebbleConnectionReceiver());
        PebbleKit.registerPebbleDisconnectedReceiver(context, new PebbleDisconnectionReceiver());
        PebbleKit.registerReceivedAckHandler(context, new PebbleAckInfoReciever(PEBBLE_APP_UUID));
        PebbleKit.registerReceivedNackHandler(context, new PebbleNackInfoReceiver(PEBBLE_APP_UUID));
        PebbleKit.registerReceivedDataHandler(context, new PebbleDataReceiver(PEBBLE_APP_UUID));
    }

    public void sendAlertToPebble(String message) {
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title", message);
        data.put("body", "hit back to replay");
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "MyAndroidApp");
        i.putExtra("notificationData", notificationData);

        //Log.d(MainActivity.TAG, "About to send a modal alert to Pebble: " + notificationData);
        context.sendBroadcast(i);
    }

    public Pebble(MainActivity context) {
        this.context = context;

        initConnection();
    }

    public boolean testConnection() {
        boolean connected = PebbleKit.isWatchConnected(context);
        //Log.i(MainActivity.TAG, "Pebble is " + (connected ? "connected" : "not connected"));
        return connected;
    }

}
