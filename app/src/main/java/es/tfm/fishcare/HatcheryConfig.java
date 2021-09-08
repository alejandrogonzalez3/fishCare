package es.tfm.fishcare;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.tfm.fishcare.main.MainActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HatcheryConfig extends AppCompatActivity {
    private OkHttpClient client = RestService.getClient();
    private Button continueButton;
    private Spinner specie;
    private EditText pH_min_value, pH_max_value, do_min_value, do_max_value, temperature_min_value, temperature_max_value, salinity_min_value, salinity_max_value;
    private Session session;
    private String jwt;
    private String hatcheryId;

    Map<String, String> units = new HashMap<String, String>() {{
        put("pH", "");
        put("do", "mg/L");
        put("temperature", "ºC");
        put("salinity", "ppt");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hatchery_config);

        continueButton = findViewById(R.id.continueConfig);
        specie = findViewById(R.id.specie_spinner);
        pH_min_value = findViewById(R.id.pH_min_value);
        pH_max_value = findViewById(R.id.pH_max_value);
        do_min_value = findViewById(R.id.do_min_value);
        do_max_value = findViewById(R.id.do_max_value);
        temperature_min_value = findViewById(R.id.temperature_min_value);
        temperature_max_value = findViewById(R.id.temperature_max_value);
        salinity_min_value = findViewById(R.id.salinity_min_value);
        salinity_max_value = findViewById(R.id.salinity_max_value);

        session = new Session(this);
        jwt = session.getJwt();
        hatcheryId = session.gethatcheryId();

        String[] valores = {"", "crustáceos", "salmónidos"};

        specie.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, valores));
        specie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println(position);
                switch (position) {
                    case 0:
                        clearFields();
                        clearErrors();
                        break;
                    case 1:
                        setCrustaceos();
                        clearErrors();
                        break;
                    case 2:
                        setSalmonidos();
                        clearErrors();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        continueButton.setOnClickListener(v -> {
            checkFields();
        });
    }

    // TO-DO: EXTRACT TO UTILS, ITS REPEATED AT: SIGNUP (SEARCH FOR MORE REPETITIONS)
    private boolean isEmpty(EditText field) {
        String text = field.getText().toString();
        return (TextUtils.isEmpty(text));
    }

    private void checkFields() {
        if (isEmpty(pH_min_value)) {
            pH_min_value.setError("pH min is required");
        }

        if (isEmpty(pH_max_value)) {
            pH_max_value.setError("pH max is required");
        }

        if (isEmpty(do_min_value)) {
            do_min_value.setError("do min is required");
        }

        if (isEmpty(do_max_value)) {
            do_max_value.setError("do max is required");
        }

        if (isEmpty(temperature_min_value)) {
            temperature_min_value.setError("temperature min is required");
        }

        if (isEmpty(temperature_max_value)) {
            temperature_max_value.setError("temperature max is required");
        }

        if (isEmpty(salinity_min_value)) {
            salinity_min_value.setError("salinity min is required");
        }

        if (isEmpty(salinity_max_value)) {
            salinity_max_value.setError("salinity max is required");
        }

        if (pH_min_value.getError() != null | pH_max_value.getError() != null | do_min_value.getError() != null | do_max_value.getError() != null | temperature_min_value.getError() != null | temperature_max_value.getError() != null | salinity_min_value.getError() != null | salinity_max_value.getError() != null) {
            return;
        }

        createSensor("pH", pH_min_value.getText().toString(), pH_max_value.getText().toString(), units.get("pH"));
        createSensor("do", do_min_value.getText().toString(), do_max_value.getText().toString(), units.get("do"));
        createSensor("temperature", temperature_min_value.getText().toString(), temperature_max_value.getText().toString(), units.get("temperature"));
        createSensor("salinity", salinity_min_value.getText().toString(), salinity_max_value.getText().toString(), units.get("salinity"));

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void clearFields() {
        setFieldValues("","", "", "", "", "", "", "");
    }

    private void setCrustaceos() {
        setFieldValues("5","15", "20", "90", "6", "28", "10", "30");
    }

    private void setSalmonidos() {
        setFieldValues("4","14", "19", "89", "5", "27", "9", "29");
    }

    private void setFieldValues(String pH_min, String pH_max, String do_min, String do_max, String temperature_min, String temperature_max, String salinity_min, String salinity_max) {
        pH_min_value.setText(pH_min);
        pH_max_value.setText(pH_max);
        do_min_value.setText(do_min);
        do_max_value.setText(do_max);
        temperature_min_value.setText(temperature_min);
        temperature_max_value.setText(temperature_max);
        salinity_min_value.setText(salinity_min);
        salinity_max_value.setText(salinity_max);
    }

    private void clearErrors() {
        pH_min_value.setError(null);
        pH_max_value.setError(null);
        do_min_value.setError(null);
        do_max_value.setError(null);
        temperature_min_value.setError(null);
        temperature_max_value.setError(null);
        salinity_min_value.setError(null);
        salinity_max_value.setError(null);
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
                Toast.makeText(HatcheryConfig.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
            }
        });
    }

}