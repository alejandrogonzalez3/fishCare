package es.tfm.fishcare.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import es.tfm.fishcare.R;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String CHANNEL_ID = "es.tfm.fishCare.ANDROID";
    public static final String CHANNEL_NAME = "ANDROID CHANNEL";

    public NotificationUtils(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel() {

        // create channel
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        channel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        channel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        channel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lock-screen or not
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    //CHANNEL_ID is ignored in old API's
    @SuppressLint("NewApi")
    public Notification.Builder createNotification(String title, String body, Intent intent) {
        // Create an explicit intent for an Activity in your app

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Get the layouts to use in the custom notification
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_view);
        notificationLayout.setImageViewResource(R.id.notification_icon,R.drawable.ic_fish);
        notificationLayout.setTextViewText(R.id.notification_title,title);
        notificationLayout.setTextViewText(R.id.notification_text, body);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_view);
        notificationLayoutExpanded.setImageViewResource(R.id.notification_icon,R.drawable.ic_fish);
        notificationLayoutExpanded.setTextViewText(R.id.notification_title,title);
        notificationLayoutExpanded.setTextViewText(R.id.notification_text, body);

        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_fish)
                .setPriority(Notification.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.blue_400))
                .setColorized(true)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setAutoCancel(true);
    }
}