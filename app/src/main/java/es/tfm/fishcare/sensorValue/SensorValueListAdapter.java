package es.tfm.fishcare.sensorValue;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import es.tfm.fishcare.R;

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

        convertView.setTag(sensorValues[position].getSensor().getId());

        TextView title = convertView.findViewById(R.id.sensor_value_item_title);
        TextView date = convertView.findViewById(R.id.sensor_value_item_date);
        TextView value = convertView.findViewById(R.id.sensor_value_item_value);
        TextView units = convertView.findViewById(R.id.sensor_value_item_units);

        title.setText(sensorValues[position].getSensor().getName());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMANY);
        date.setText(formatter.format(sensorValues[position].getDate()));
        value.setText(sensorValues[position].getValue().toString());
        if (!("").equals(sensorValues[position].getSensor().getUnits())) {
            units.setText("(" + sensorValues[position].getSensor().getUnits() + ")");
        }
        else {
            units.setText("-");
        }

        switch (sensorValues[position].getState()) {
            case OK :
                units.setTextColor(resolveColor(R.color.ok));
                value.setTextColor(resolveColor(R.color.ok));
                break;
            case WARNING :
                units.setTextColor(resolveColor(R.color.warning));
                value.setTextColor(resolveColor(R.color.warning));
                break;
            case DANGER :
                units.setTextColor(resolveColor(R.color.danger));
                value.setTextColor(resolveColor(R.color.danger));
                break;
        }

        return convertView;
    }

    private int resolveColor(int idColor) {
        return context.getResources().getColor(idColor);
    }
}