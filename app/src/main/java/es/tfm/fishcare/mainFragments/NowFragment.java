package es.tfm.fishcare.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.tfm.fishcare.R;
import es.tfm.fishcare.RestService;
import es.tfm.fishcare.Sensor;
import es.tfm.fishcare.SensorValue;
import es.tfm.fishcare.SensorValueListAdapter;
import es.tfm.fishcare.SensorValueState;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NowFragment extends Fragment {
    OkHttpClient client = RestService.getClient();
    ListView list;

    public NowFragment() {
        getSensorValues();
        // Required empty public constructor
    }

    public static NowFragment newInstance(String param1, String param2) {
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
    }

    private void getSensorValues() {
        HttpUrl.Builder urlBuilder = RestService.getSensorValueUrlBuilder();
        urlBuilder.addPathSegment("last");
        urlBuilder.addQueryParameter("page", "0");
        urlBuilder.addQueryParameter("size", "10");
        urlBuilder.addQueryParameter("sortBy", "id");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

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
                    Sensor tempSensor = new Sensor(sensor.getName(), sensor.getUnits(), sensor.getMaxAllowedValue(), sensor.getMinAllowedValue());
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
                });

            }
        });
    }
}