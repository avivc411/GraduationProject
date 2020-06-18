package com.Project.project.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.Project.project.Activities.MainActivity;

/**
 * Handling notifications.
 */
public class AlarmReceiver extends BroadcastReceiver {

    Notification.Builder builder;
    @Override
    public void onReceive(Context context, Intent intent) {

        // Get id & message from intent.
        int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("userName");

        // When notification tapped, call relevant activity.
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        NotificationManager myNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Prepare notification.
        builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Emotion Analyzer")
                .setContentText("Hey " + message + "! It's time to fill out a questionnaire!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "id324";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Emotions",
                    NotificationManager.IMPORTANCE_HIGH);
            myNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        // Notify.
        myNotificationManager.notify(notificationId, builder.build());
    }

    public Notification.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Notification.Builder builder) {
        this.builder = builder;
    }
}
