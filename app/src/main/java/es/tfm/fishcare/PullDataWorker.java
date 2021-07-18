package es.tfm.fishcare;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import es.tfm.fishcare.notifications.NotificationType;
import es.tfm.fishcare.notifications.NotificationUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PullDataWorker extends Worker {
    private OkHttpClient client = RestService.getClient();
    private NotificationUtils mNotificationUtils;
    private Context context;
    private Session session;
    private String jwt;
    private String hatcheryId;

    public PullDataWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        session = new Session(context);
        jwt = session.getJwt();
        hatcheryId = session.gethatcheryId();
        mNotificationUtils = new NotificationUtils(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Result doWork() {
        checkSensorValues();
        return Result.success();
    }

    private void checkSensorValues() {
        HttpUrl.Builder urlBuilder = RestService.getSensorValueUrlBuilder();
        urlBuilder.addPathSegment("last");
        urlBuilder.addQueryParameter("page", "0");
        urlBuilder.addQueryParameter("size", "10");
        urlBuilder.addQueryParameter("sortBy", "id");
        urlBuilder.addQueryParameter("hatcheryId", hatcheryId);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).header("Authorization", jwt).build();

        final Gson gson = new Gson();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                Type listOfMyClassObject = new TypeToken<ArrayList<SensorValue>>() {}.getType();
                List<SensorValue> sensorValues = gson.fromJson(response.body().charStream(), listOfMyClassObject);
                for (SensorValue sensorValue : sensorValues) {
                    Sensor sensor = sensorValue.getSensor();
                    final Intent intent = new Intent();
                    if (sensorValue.getValue() > sensor.getMaxAllowedValue()) {
                        if (sensorValue.getValue() > (sensor.getMaxAllowedValue() * 1.5)) {
                            String body = sensor.getName() + " ha superado mucho su límite superior.";
                            Notification.Builder nb = mNotificationUtils.createNotification("Danger", body, intent);
                            mNotificationUtils.getManager().notify(101, nb.build());
                            postNotification(sensorValue.getId(), NotificationType.HIGHER);
                        }
                        else {
                            String body = sensor.getName() + " ha superado su límite superior.";
                            Notification.Builder nb = mNotificationUtils.createNotification("Warning", body, intent);
                            mNotificationUtils.getManager().notify(101, nb.build());
                            postNotification(sensorValue.getId(), NotificationType.HIGHER);
                        }
                    }
                    if (sensorValue.getValue() < sensor.getMinAllowedValue()) {
                        if ((sensorValue.getValue() * 1.5) < sensor.getMinAllowedValue()) {
                            String body = sensor.getName() + " ha superado mucho su límite inferior.";
                            Notification.Builder nb = mNotificationUtils.createNotification("Danger", body, intent);
                            mNotificationUtils.getManager().notify(101, nb.build());
                            postNotification(sensorValue.getId(), NotificationType.LOWER);
                        }
                        else {
                            String body = sensor.getName() + " ha superado su límite inferior.";
                            Notification.Builder nb = mNotificationUtils.createNotification("Warning", body, intent);
                            mNotificationUtils.getManager().notify(101, nb.build());
                            postNotification(sensorValue.getId(), NotificationType.LOWER);
                        }
                    }
                }

            }
        });
    }

    private void postNotification(Long sensorValueId, NotificationType notificationType) {
        HttpUrl.Builder urlBuilder = RestService.getNotificationUrlBuilder();
        urlBuilder.addPathSegment("create");
        String url = urlBuilder.build().toString();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sensorValueId", sensorValueId.toString())
                .addFormDataPart("notificationType", notificationType.name())
                .build();

        Request request = new Request.Builder().url(url).post(requestBody).header("Authorization", jwt).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
            }
        });
    }

}
