package es.tfm.fishcare.main;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.tfm.fishcare.CustomMarkerView;
import es.tfm.fishcare.R;
import es.tfm.fishcare.RestService;
import es.tfm.fishcare.sensorValue.SensorValue;
import es.tfm.fishcare.Session;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    private LineChart doChart, phChart, temperatureChart, conductivityChart;
    private OkHttpClient client = RestService.getClient();
    private Session session;
    private String jwt;
    private String hatcheryId;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        doChart = getActivity().findViewById(R.id.doChart);
        phChart = getActivity().findViewById(R.id.pHChart);
        temperatureChart = getActivity().findViewById(R.id.temperatureChart);
        conductivityChart = getActivity().findViewById(R.id.conductivityChart);

        session = new Session(getContext());
        jwt = session.getJwt();
        hatcheryId = session.gethatcheryId();

        configChart(doChart, "dO (%)");
        renderData(120f, 15f, doChart);
        getSensorValues(doChart,"do", R.drawable.fade_blue);

        configChart(phChart, "pH");
        renderData(20f, 15f, phChart);
        getSensorValues(phChart,"ph", R.drawable.fade_green);

        configChart(temperatureChart, "Temperature");
        renderData(35f, 15f, temperatureChart);
        getSensorValues(temperatureChart,"temperature", R.drawable.fade_red);


        configChart(conductivityChart, "Conductivity");
        renderData(40f, 15f, conductivityChart);

        getSensorValues(conductivityChart,"conductivity", R.drawable.fade_yellow);
    }

    private void getSensorValues(LineChart chart, String sensorName, int drawableId) {
        HttpUrl.Builder urlBuilder = RestService.getSensorValueUrlBuilder();
        urlBuilder.addQueryParameter("sensorName", sensorName);
        urlBuilder.addQueryParameter("page", "0");
        urlBuilder.addQueryParameter("size", "15");
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
                ArrayList<Entry> newValues = new ArrayList<>();
                ArrayList<Date> dates = new ArrayList<>();
                int i = 0;
                for (SensorValue sensorValue : sensorValues) {
                    newValues.add(new Entry (i, sensorValue.getValue()));
                    dates.add(sensorValue.getDate());
                    i+=1;
                }
                float maximumLimit = sensorValues.get(0).getSensor().getMaxAllowedValue();
                float minimumLimit = sensorValues.get(0).getSensor().getMinAllowedValue();

                // UI update must be done on Ui Thread
                getActivity().runOnUiThread(() -> {
                    setData(newValues, dates, chart, drawableId, maximumLimit, minimumLimit);
                    chart.setVisibility(View.INVISIBLE);
                    chart.setVisibility(View.VISIBLE);
                });

            }
        });
    }

    // Get each chart Date Values (now used on each chart)
    public ArrayList<String> getDateValues(List<Date> dates) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMANY);
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++)

            labels.add(formatter.format(dates.get(i)));
        return labels;
    }

    public void configChart(LineChart chart, String description) {
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        Description des = chart.getDescription();
        des.setText(description);
    }

    public void renderData(float yAxisMax, float xAxisMax, LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(xAxisMax);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum(yAxisMax);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        chart.getAxisRight().setEnabled(false);
    }


    private void setData(ArrayList<Entry> values, List<Date> dates, LineChart chart, int drawableId, float maximumLimit, float minimumLimit) {
        LimitLine ll1 = new LimitLine(maximumLimit, "Maximum Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(minimumLimit, "Minimum Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);

/*        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDateValues(dates)));*/

        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Sample Data");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(getActivity(), drawableId);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }
}