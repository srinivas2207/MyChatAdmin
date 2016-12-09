package com.shree.mychatadmin.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shree.mychatadmin.R;
import com.shree.mychatadmin.activity.HomeActivity;

import org.json.JSONObject;

/**
 * Created by SrinivasDonapati on 10/19/2016.
 */

public class MyFirebaseMessagingService  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                String message = remoteMessage.getData().toString();

                System.out.println("GCm Message ==========> " +message);

                JSONObject messageObj = new JSONObject(message);
                JSONObject msgObj = messageObj.getJSONObject("message");

                String fullName = msgObj.getString("uName");
                sendNotification(fullName);

            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    private  void sendNotification(String fullName) {

        int notificationId = 1;
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        String notificationBody = "New registration request from " + fullName;

        String notificationTitle = "MyChat Admin";

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , notificationIntent,PendingIntent.FLAG_ONE_SHOT);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setDefaults(defaults)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
