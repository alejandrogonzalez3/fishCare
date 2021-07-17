package es.tfm.fishcare.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.tfm.fishcare.Action;
import es.tfm.fishcare.ActionsListAdapter;
import es.tfm.fishcare.R;
import es.tfm.fishcare.RestService;
import es.tfm.fishcare.Session;
import es.tfm.fishcare.notifications.Notification;
import es.tfm.fishcare.notifications.NotificationListAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationsFragment extends Fragment {
    private OkHttpClient client = RestService.getClient();
    private ListView list;
    private Session session;
    private String jwt;
    private String hatcheryId;

    public NotificationsFragment() {
        // Required empty public constructor
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
        session = new Session(getContext());
        jwt = session.getJwt();
        hatcheryId = session.gethatcheryId();

        getNotifications();
    }

    private void getNotifications() {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getNotificationUrlBuilder();
        urlBuilder.addPathSegment("notRead");
        urlBuilder.addQueryParameter("hatcheryId", hatcheryId);
        urlBuilder.addQueryParameter("page", "0");
        urlBuilder.addQueryParameter("size", "10");
        urlBuilder.addQueryParameter("sortBy", "id");
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

                Type listOfMyClassObject = new TypeToken<ArrayList<Notification>>() {}.getType();
                List<Notification> notifications = gson.fromJson(response.body().charStream(), listOfMyClassObject);
                Notification[] notificationsArray = new Notification[notifications.size()];
                int i = 0;
                for (Notification notification : notifications) {
                    notificationsArray[i] = notification;
                    list.setOnItemClickListener((parent, view1, position, id) -> {
                        markNotificationAsRead(view1, notification.getId());
                    });
                    i+=1;
                }

                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), notificationsArray);
                    list.setAdapter(adapter);
                });

            }
        });
    }

    private void markNotificationAsRead(View view, Long notificationId) {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getNotificationUrlBuilder();
        urlBuilder.addPathSegment("read");
        urlBuilder.addQueryParameter("notificationId", notificationId.toString());
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).post(RequestBody.create("", null)).header("Authorization", jwt).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    RelativeLayout layout = (RelativeLayout) view;
                    layout.setClickable(false);
                    ImageView image = (ImageView) layout.getChildAt(0);
                    TextView text = (TextView) layout.getChildAt(1);
                    image.setColorFilter(R.color.blue_400_gray_friend);
                    text.setTextColor(getResources().getColor(R.color.blue_400_gray_friend));
                });
            }
        });
    }

}