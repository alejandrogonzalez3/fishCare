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

import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.tfm.fishcare.notifications.Notification;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActionsListAdapter extends BaseAdapter implements ListAdapter {
    private final Activity context;
    private final Action[] actions;
    OkHttpClient client = RestService.getClient();

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
        TextView textAction;
        Button actionOnButton;
        Button actionOffButton;

        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.actions_list_item, parent,false);
        }

        textAction = (TextView) convertView.findViewById(R.id.textActionItem);
        actionOnButton = (Button) convertView.findViewById(R.id.buttonOnActionItem);
        actionOffButton = (Button) convertView.findViewById(R.id.buttonOffActionItem);

        // Get Actions current state and print button colors
        // actionOnButton.setBackgroundColor(resolveColor(R.color.blue_400_gray_friend));
        // actionOffButton.setBackgroundColor(resolveColor(R.color.blue_400));

        textAction.setText(actions[position].getName());
        actionOnButton.setOnClickListener((View.OnClickListener) v -> {
            // On
            doAction("on", actions[position].getName(), actionOnButton, actionOffButton);
        });
        actionOffButton.setOnClickListener((View.OnClickListener) v -> {
            // Off
            doAction("off", actions[position].getName(), actionOnButton, actionOffButton);
        });

        return convertView;
    }

    // DUPLICATED ON SensorValueListAdapter -> Move to Utils
    private int resolveColor(int idColor) {
        return context.getResources().getColor(idColor);
    }

    private void doAction(String actionType, String sensorName, Button onButton, Button offButton) {
        HttpUrl.Builder urlBuilder;
        if ("on".equals(actionType)){
             urlBuilder = RestService.getActuatorOnUrlBuilder();
        }
        else {
            urlBuilder = RestService.getActuatorOffUrlBuilder();
        }
        urlBuilder.addQueryParameter("name", sensorName);
        // TEMPORAL: THIS MUST BE SET USING USER HATCHERY ID (WHEN LOGIN BE INTEGRATED)
        urlBuilder.addQueryParameter("hatcheryId", "1");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).post(RequestBody.create("", null)).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
                // UI update must be done on Ui Thread
                context.runOnUiThread(() -> {
                    if ("on".equals(actionType)) {
                        onButton.setBackgroundColor(resolveColor(R.color.blue_400_gray_friend));
                        offButton.setBackgroundColor(resolveColor(R.color.blue_400));
                        Toast.makeText(context, "On", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        offButton.setBackgroundColor(resolveColor(R.color.blue_400_gray_friend));
                        onButton.setBackgroundColor(resolveColor(R.color.blue_400));
                        Toast.makeText(context, "Off", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}