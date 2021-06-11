package es.tfm.fishcare;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import es.tfm.fishcare.notifications.Notification;

public class SensorValueListAdapter extends BaseAdapter {

    private final Activity context;
    private final SensorValue[] sensorValues;


    public SensorValueListAdapter(Activity context, SensorValue[] sensorValues) {
        this.context = context;
        this.sensorValues = sensorValues;
    }

    @Override
    public int getCount() {
        return sensorValues.length;
    }

    @Override
    public Object getItem(int position) {
        return sensorValues[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sensor_value_list_item, parent,false);
        }

        TextView title = convertView.findViewById(R.id.sensor_value_item_title);
        TextView value = convertView.findViewById(R.id.sensor_value_item_value);

        title.setText(sensorValues[position].getTitle());
        value.setText(sensorValues[position].getValue());

        switch (sensorValues[position].getState()) {
            case OK :
                title.setTextColor(resolveColor(R.color.ok));
                value.setTextColor(resolveColor(R.color.ok));
                break;
            case WARNING :
                title.setTextColor(resolveColor(R.color.warning));
                value.setTextColor(resolveColor(R.color.warning));
                break;
            case DANGER :
                title.setTextColor(resolveColor(R.color.danger));
                value.setTextColor(resolveColor(R.color.danger));
                break;
        }

        return convertView;
    }

    private int resolveColor(int idColor) {
        return context.getResources().getColor(idColor);
    }
}