package com.tp2.findme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SMS {

    public static String received_sms = "";
    public static String receiver_nbr = "";
    public static String sms_to_send = "";
    public static String sender_nbr = "";
    public static String received_coords = "";
    public static ArrayList<Map<String, String>> msgs_stack = new ArrayList<>();
    private static Map<String, String> sent_msg = new HashMap<>();



    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public static void sendSMS(Context context, String phone, String msg) {

        //phone = "+1-555-123-4567";

        SMS.receiver_nbr = phone;
        SMS.sms_to_send = msg;
        sent_msg.put(phone, msg);

        final String[] SMS_SENT = {"SMS_SENT"};
        String SMS_DELIVERED = "SMS_DELIVERED";

        // for when the SMS has been sent
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String state = "";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        state = "SMS sent successfully";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        state = "Generic failure cause";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        state = "Service is currently unavailable";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        state = "No PDU provided";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        state = "Radio was explicitly turned off";
                        break;
                }
                //Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
            }
        }, new IntentFilter(SMS_SENT[0]));

        // for when the SMS has been delivered
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String state = "";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        state = "SMS delivered";
                        //sent_msg.put(phone, msg);
                        break;
                    case Activity.RESULT_CANCELED:
                        state = "SMS not delivered";
                        //sent_msg.put("Failed", msg);
                        break;
                }
                //Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
                SMS.msgs_stack.add(sent_msg);
            }
        }, new IntentFilter(SMS_DELIVERED));

        // unregisterReceiver()
        // get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT[0]), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED), PendingIntent.FLAG_IMMUTABLE);

        // send a text based SMS
        smsManager.sendTextMessage(phone, null, msg, sentPendingIntent, deliveredPendingIntent);
    }



    /*public boolean isSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
            return true;
        else {
            // we can inform user about sim state
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT:
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    break;
            }
            return false;
        }
    }*/

}
