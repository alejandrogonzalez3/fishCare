package es.tfm.fishcare.main;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.tfm.fishcare.actions.Action;
import es.tfm.fishcare.actions.ActionsListAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActionsFragment extends Fragment {
    private ListView list;
    private FloatingActionButton addActuatorFab;
    private Button addActuatorButton;
    private EditText actuatorName;
    private CheckBox waterPumpBehaviour, oxygenatorBehaviour;

    private OkHttpClient client = RestService.getClient();
    private Session session;
    private String jwt;
    private String hatcheryId;

    public ActionsFragment() {}

    public static ActionsFragment newInstance(String param1, String param2) {
        ActionsFragment fragment = new ActionsFragment();
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
        return inflater.inflate(R.layout.fragment_actions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        list = view.findViewById(R.id.actionsList);
        addActuatorFab = view.findViewById(R.id.add_sensor_fab);
        addActuatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });
        waterPumpBehaviour = view.findViewById(R.id.waterPumpBehaviour);
        waterPumpBehaviour.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setWaterPumpBehaviour(isChecked);
        });
        oxygenatorBehaviour = view.findViewById(R.id.oxygenatorBehaviour);
        oxygenatorBehaviour.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setOxygenatorBehaviour(isChecked);
        });

        session = new Session(getContext());
        jwt = session.getJwt();
        hatcheryId = session.gethatcheryId();
        getActuators();
        getCheckBoxDefaultValues();
    }

    public void showPopup(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.actuator_popup, null);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        actuatorName = popupView.findViewById(R.id.actuatorName);
        addActuatorButton = popupView.findViewById(R.id.addActuatorButton);
        addActuatorButton.setOnClickListener(v -> {
            checkFields();
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

    private void checkFields() {
        if (isEmpty(actuatorName)) {
            actuatorName.setError("actuator name is required");
        }

        if (actuatorName.getError() != null) {
            return;
        }

        createActuator(actuatorName.getText().toString());
    }

    private void getActuators() {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getActuatorUrlBuilder();
        urlBuilder.addPathSegment("all");
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

                Type listOfMyClassObject = new TypeToken<ArrayList<Action>>() {}.getType();
                List<Action> actions = gson.fromJson(response.body().charStream(), listOfMyClassObject);
                Action[] actionsArray = new Action[actions.size()];
                int i = 0;
                for (Action action : actions) {
                    actionsArray[i] = action;
                    i+=1;
                }

                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    ActionsListAdapter adapter = new ActionsListAdapter(getActivity(), actionsArray);
                    list.setAdapter(adapter);
                });

            }
        });
    }

    private void createActuator(String actuatorName) {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getActuatorUrlBuilder();
        urlBuilder.addPathSegment("create");
        urlBuilder.addQueryParameter("name", actuatorName);
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
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Actuator created successfully!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setOxygenatorBehaviour(Boolean behaviour) {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getHatcheryUrlBuilder();
        urlBuilder.addPathSegment("defaultOxygenator");
        urlBuilder.addQueryParameter("defaultBehaviour", behaviour.toString());
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

    private void setWaterPumpBehaviour(Boolean behaviour) {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getHatcheryUrlBuilder();
        urlBuilder.addPathSegment("defaultWaterPump");
        urlBuilder.addQueryParameter("defaultBehaviour", behaviour.toString());
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

    private void getCheckBoxDefaultValues() {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getHatcheryUrlBuilder();
        urlBuilder.addPathSegment("actuators");
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

                Type MyClassObject = new TypeToken<Map<String, Boolean>>() {}.getType();
                Map<String, Boolean> defaultValues = gson.fromJson(response.body().charStream(), MyClassObject);

                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    waterPumpBehaviour.setChecked(defaultValues.get("waterPump"));
                    oxygenatorBehaviour.setChecked(defaultValues.get("oxygenator"));
                });

            }
        });
    }
}