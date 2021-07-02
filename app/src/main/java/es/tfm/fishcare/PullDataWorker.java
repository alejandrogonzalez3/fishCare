package es.tfm.fishcare;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.tfm.fishcare.notifications.NotificationUtils;

public class PullDataWorker extends Worker {

    private NotificationUtils mNotificationUtils;

    public PullDataWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        mNotificationUtils = new NotificationUtils(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Result doWork() {
        // Get Data from backend
            // All ok? -> Success
            // Some Error -> Notifications

        // Get sensor(s) data
        // Check if the data is normal
        Float[] values = {12.5f, 15.6f};

        for(Float value : values) {
            // Check value comparing with top and bottom values
            // If some value is lower than bottom or higher than top then...
            // ...notify on App and Post notification on backend

            final Intent intent = new Intent();
            Notification.Builder nb = mNotificationUtils.
                    createNotification("Sensor error", "Too much X sensor value", intent);

            mNotificationUtils.getManager().notify(101, nb.build());

/*            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));*/
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

}
