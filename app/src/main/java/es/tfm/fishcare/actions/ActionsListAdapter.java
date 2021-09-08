package es.tfm.fishcare.actions;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import es.tfm.fishcare.R;
import es.tfm.fishcare.RestService;
import es.tfm.fishcare.Session;
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
    private OkHttpClient client = RestService.getClient();
    private Session session;
    private String jwt;
    private String hatcheryId;

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

        textAction = convertView.findViewById(R.id.textActionItem);
        actionOnButton = convertView.findViewById(R.id.buttonOnActionItem);
        actionOffButton = convertView.findViewById(R.id.buttonOffActionItem);

        session = new Session(context);
        jwt = session.getJwt();
        hatcheryId = session.gethatcheryId();

        textAction.setText(actions[position].getName());
        actionOnButton.setOnClickListener(v -> {
            // On
            doAction("on", actions[position].getName(), actionOnButton, actionOffButton);
        });
        actionOffButton.setOnClickListener(v -> {
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
        urlBuilder.addQueryParameter("hatcheryId", hatcheryId);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).post(RequestBody.create("", null)).header("Authorization", jwt).build();

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