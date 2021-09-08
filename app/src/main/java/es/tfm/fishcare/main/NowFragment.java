package es.tfm.fishcare.main;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.tfm.fishcare.R;
import es.tfm.fishcare.RestService;
import es.tfm.fishcare.pojos.Sensor;
import es.tfm.fishcare.sensorValue.SensorValue;
import es.tfm.fishcare.sensorValue.SensorValueListAdapter;
import es.tfm.fishcare.sensorValue.SensorValueState;
import es.tfm.fishcare.Session;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NowFragment extends Fragment {
    private Session session;
    private OkHttpClient client = RestService.getClient();
    private ListView list;
    private TextView refreshDate;
    private Button refreshButton, addSensorButton;
    private FloatingActionButton addSensorFab;
    private EditText sensorName, maxAllowedSensorValue, minAllowedSensorValue, sensorUnits;

    private String jwt;
    private String hatcheryId;

    public NowFragment() {
    }

    public static NowFragment newInstance() {
        NowFragment fragment = new NowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        list = view.findViewById(R.id.nowList);
        refreshDate = view.findViewById(R.id.refresh_date);
        refreshButton = view.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(v -> {
            getSensorValues();
        });
        addSensorFab = view.findViewById(R.id.add_sensor_fab);
        addSensorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(false, null);
            }
        });

        session = new Session(getContext());
        jwt = session.getJwt();
        hatcheryId = session.gethatcheryId();
        getSensorValues();
    }

    public void showPopup(Boolean fromUpdate, Long sensorId){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.popup_layout, null);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        sensorName = popupView.findViewById(R.id.sensorName);
        maxAllowedSensorValue = popupView.findViewById(R.id.maxAllowedSensorValue);
        minAllowedSensorValue = popupView.findViewById(R.id.minAllowedSensorValue);
        sensorUnits = popupView.findViewById(R.id.sensorUnits);
        addSensorButton = popupView.findViewById(R.id.addSensorButton);
        if (fromUpdate) {
            addSensorButton.setText("Update Sensor");
        }
        addSensorButton.setOnClickListener(v -> {
            checkFields(fromUpdate, sensorId);
            popupWindow.dismiss();
        });

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    // TO-DO: EXTRACT TO UTILS, ITS REPEATED AT: SIGNUP (SEARCH FOR MORE REPETITIONS)
    private boolean isEmpty(EditText field) {
        String text = field.getText().toString();
        return (TextUtils.isEmpty(text));
    }

    private void checkFields(Boolean fromUpdate, Long sensorId) {
        if (!fromUpdate) {
            if (isEmpty(sensorName)) {
                sensorName.setError("sensor name is required");
            }

            if (isEmpty(maxAllowedSensorValue)) {
                maxAllowedSensorValue.setError("max allowed value is required");
            }

            if (isEmpty(minAllowedSensorValue)) {
                minAllowedSensorValue.setError("min allowed value is required");
            }

            if (isEmpty(sensorUnits)) {
                sensorUnits.setError("sensor units is required");
            }

            if (sensorName.getError() != null | maxAllowedSensorValue.getError() != null | minAllowedSensorValue.getError() != null | sensorUnits.getError() != null) {
                return;
            }

            createSensor(sensorName.getText().toString(), minAllowedSensorValue.getText().toString(), maxAllowedSensorValue.getText().toString(), sensorUnits.getText().toString());
        }
        else {
            if (sensorName.getText().toString().equals("") & maxAllowedSensorValue.getText().toString().equals("") & minAllowedSensorValue.getText().toString().equals("") & sensorUnits.getText().toString().equals("")) {
                return;
            }

            updateSensor(sensorId, sensorName.getText().toString(), minAllowedSensorValue.getText().toString(), maxAllowedSensorValue.getText().toString(), sensorUnits.getText().toString());
        }
    }

    private void getSensorValues() {
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
                SensorValue[] customSensorValues = new SensorValue[sensorValues.size()];
                int i = 0;
                for (SensorValue sensorValue : sensorValues) {
                    SensorValue tempSensorValue = new SensorValue();
                    tempSensorValue.setDate(sensorValue.getDate());
                    Sensor sensor = sensorValue.getSensor();
                    Sensor tempSensor = new Sensor(sensor.getId(), sensor.getName(), sensor.getUnits(), sensor.getMaxAllowedValue(), sensor.getMinAllowedValue());
                    tempSensorValue.setSensor(tempSensor);
                    if (sensorValue.getValue() > sensor.getMaxAllowedValue() | sensorValue.getValue() < sensor.getMinAllowedValue()) {
                        if (sensorValue.getValue() > (sensor.getMaxAllowedValue() * 1.5) | (sensorValue.getValue() * 1.5) < sensor.getMinAllowedValue()) {
                            tempSensorValue.setState(SensorValueState.DANGER);
                        }
                        else {
                            tempSensorValue.setState(SensorValueState.WARNING);
                        }
                    }
                    else {
                        tempSensorValue.setState(SensorValueState.OK);
                    }
                    tempSensorValue.setValue(sensorValue.getValue());
                    customSensorValues[i] = tempSensorValue;
                    i ++;
                }

                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    SensorValueListAdapter adapter = new SensorValueListAdapter(getActivity(), customSensorValues);
                    list.setAdapter(adapter);
                    list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                            Long sensorId = (Long) arg1.getTag();
                            showPopup(true, sensorId);
                            return true;
                        }
                    });

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMANY);
                    refreshDate.setText("Last data refresh: " + formatter.format(new Date()));
                });

            }
        });
    }

    private void createSensor(String sensorName, String minAllowedValue, String maxAllowedValue, String units) {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getSensorUrlBuilder();
        urlBuilder.addPathSegment("create");
        urlBuilder.addQueryParameter("name", sensorName);
        urlBuilder.addQueryParameter("minAllowedValue", minAllowedValue);
        urlBuilder.addQueryParameter("maxAllowedValue", maxAllowedValue);
        urlBuilder.addQueryParameter("units", units);
        urlBuilder.addQueryParameter("hatcheryId", hatcheryId);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).post(RequestBody.create("", null)).header("Authorization", jwt).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
            }
        });
    }

    private void updateSensor(Long sensorId, String sensorName, String minAllowedValue, String maxAllowedValue, String units) {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getSensorUrlBuilder();
        urlBuilder.addPathSegment("update");
        urlBuilder.addQueryParameter("id", sensorId.toString());
        urlBuilder.addQueryParameter("name", sensorName);
        urlBuilder.addQueryParameter("minAllowedValue", minAllowedValue);
        urlBuilder.addQueryParameter("maxAllowedValue", maxAllowedValue);
        urlBuilder.addQueryParameter("units", units);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).put(RequestBody.create("", null)).header("Authorization", jwt).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
            }
        });
    }
}