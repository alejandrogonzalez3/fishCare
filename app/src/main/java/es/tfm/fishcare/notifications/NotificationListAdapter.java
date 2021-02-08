package es.tfm.fishcare.notifications;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import es.tfm.fishcare.R;

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

        TextView title = convertView.findViewById(R.id.notification_item_title);
        ImageView image = convertView.findViewById(R.id.notification_item_image);
        TextView body = convertView.findViewById(R.id.notification_item_body);

        title.setText(notifications[position].getTitle());
        image.setImageResource(notifications[position].getImgId());
        body.setText(notifications[position].getBody());

        return convertView;
    }
}
