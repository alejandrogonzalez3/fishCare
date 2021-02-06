package es.tfm.fishcare.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import es.tfm.fishcare.R;
import es.tfm.fishcare.notifications.Notification;
import es.tfm.fishcare.notifications.NotificationListAdapter;

public class NotificationsFragment extends Fragment {

    ListView list;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    private Notification[] getNotifications() {
        String title = "Notification example";

        String body = "Notification Body example";

        Integer imgId = R.drawable.ic_alert_red;
        Integer imgId2 = R.drawable.ic_alert_orange;
        Integer imgId3 = R.drawable.ic_alert_yellow;

        return new Notification[]{new Notification(title, body, imgId), new Notification(title, body, imgId2), new Notification(title, body, imgId3)};
    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        list = view.findViewById(R.id.notificationsList);
        NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), getNotifications());
        list.setAdapter(adapter);
    }
}