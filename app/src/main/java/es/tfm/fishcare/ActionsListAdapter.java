package es.tfm.fishcare;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import es.tfm.fishcare.notifications.Notification;

public class ActionsListAdapter extends BaseAdapter implements ListAdapter {
    private final Activity context;
    private final Action[] actions;

    public ActionsListAdapter(Activity context, Action[] actions) {
        this.context = context;
        this.actions = actions;
    }

    @Override
    public int getCount() {
        return actions.length;
    }

    @Override
    public Object getItem(int position) {
        return actions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.actions_list_item, parent,false);
        }

        TextView textAction = (TextView) convertView.findViewById(R.id.textActionItem);
        Button actionOnButton = (Button) convertView.findViewById(R.id.buttonOnActionItem);
        Button actionOffButton = (Button) convertView.findViewById(R.id.buttonOffActionItem);

        // Get Actions current state and print button colors
        // actionOnButton.setBackgroundColor(resolveColor(R.color.blue_400_gray_friend));
        // actionOffButton.setBackgroundColor(resolveColor(R.color.blue_400));

        textAction.setText(actions[position].getName());
        actionOnButton.setOnClickListener((View.OnClickListener) v -> {
            // On
            actionOnButton.setBackgroundColor(resolveColor(R.color.blue_400_gray_friend));
            actionOffButton.setBackgroundColor(resolveColor(R.color.blue_400));
            Toast.makeText(context, (String) actions[position].getName() + ": ON", Toast.LENGTH_SHORT).show();
        });
        actionOffButton.setOnClickListener((View.OnClickListener) v -> {
            // Off
            actionOffButton.setBackgroundColor(resolveColor(R.color.blue_400_gray_friend));
            actionOnButton.setBackgroundColor(resolveColor(R.color.blue_400));
            Toast.makeText(context, (String) actions[position].getName() + ": OFF", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    // DUPLICATED ON SensorValueListAdapter -> Move to Utils
    private int resolveColor(int idColor) {
        return context.getResources().getColor(idColor);
    }

}