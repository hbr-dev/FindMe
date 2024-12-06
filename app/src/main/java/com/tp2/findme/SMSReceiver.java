package com.tp2.findme;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SMSReceiver extends BroadcastReceiver {

    private Map<String, String> received_msg = new HashMap<>();





    public SMSReceiver() {}



    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] sms = (Object[]) bundle.get("pdus");
            String format = bundle.getString("format");

            // For every SMS message received
            for (int i = 0; i < Objects.requireNonNull(sms).length; i++) {
                // Convert Object array
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody();
                SMS.sender_nbr = phone;
                SMS.received_sms = message;

                received_msg.put(phone, message);
                SMS.msgs_stack.add(received_msg);

                showNotification(context, phone, message);
            }
        }
    }


    private void showNotification(Context context, String msg_title, String msg_body) {
        final String CHANNEL_ID = "sms_channel_01";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_map_foreground)
                .setContentTitle(msg_title)
                .setContentText(msg_body)
                .setContentIntent(explicitIntent(context, msg_body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel(context);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, builder.build());

    }



    private void createNotificationChannel(Context context) {
        final String CHANNEL_NAME = "find_me_sms_channel";
        final String CHANNEL_DESC = "Channel description";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("sms_channel_01", CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private PendingIntent explicitIntent(Context context, String msg_body) {
        Intent intent;

        if (msg_body.equals("I'm asking you for your location.")) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra("action", "share_location");
        }
        else {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra("action", "find_location");
        }

        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



}
