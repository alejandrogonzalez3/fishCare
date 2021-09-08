package es.tfm.fishcare.notifications;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import es.tfm.fishcare.R;
import es.tfm.fishcare.pojos.Sensor;
import es.tfm.fishcare.sensorValue.SensorValue;

public class NotificationListAdapter extends BaseAdapter {
    private final Activity context;
    private final Notification[] notifications;

    public NotificationListAdapter(Activity context, Notification[] notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.length;
    }

    @Override
    public Object getItem(int position) {
        return notifications[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.notification_list_item, parent,false);
        }

        ImageView image = convertView.findViewById(R.id.notification_item_image);
        TextView body = convertView.findViewById(R.id.notification_item_body);

        Notification notification = notifications[position];
        SensorValue sensorValue = notification.getSensorValue();
        Sensor sensor = sensorValue.getSensor();

        if (NotificationType.HIGHER.equals(notification.getNotificationType())) {
            if (sensorValue.getValue() > (sensor.getMaxAllowedValue() * 1.5)) {
                image.setImageResource(R.drawable.ic_alert_red);
                body.setText("El sensor: " + sensor.getName() + " ha alcanzado un valor (" + sensorValue.getValue() + ") muy superior a su límite (" + sensor.getMaxAllowedValue() + ").");
            }
            else {
                image.setImageResource(R.drawable.ic_alert_orange);
                body.setText("El sensor: " + sensor.getName() + " ha alcanzado un valor (" + sensorValue.getValue() + ") superior a su límite (" + sensor.getMaxAllowedValue() + ").");
            }
        }
        else {
            if ((sensorValue.getValue() * 1.5) < sensor.getMinAllowedValue()) {
                image.setImageResource(R.drawable.ic_alert_red);
                body.setText("El sensor: " + sensor.getName() + " ha alcanzado un valor (" + sensorValue.getValue() + ") muy inferior a su límite (" + sensor.getMinAllowedValue() + ").");
            }
            else {
                image.setImageResource(R.drawable.ic_alert_orange);
                body.setText("El sensor: " + sensor.getName() + " ha alcanzado un valor (" + sensorValue.getValue() + ") inferior a su límite (" + sensor.getMinAllowedValue() + ").");
            }
        }

        return convertView;
    }

}
