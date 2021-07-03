package es.tfm.fishcare.mainFragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import es.tfm.fishcare.CustomMarkerView;
import es.tfm.fishcare.R;
import es.tfm.fishcare.SensorValueListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    LineChart doChart, phChart, temperatureChart, conductivityChart;

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

        configChart(doChart, "dO (%)");
        renderData(140f, 80f, 200f, 10f, doChart);
        setData(getDoValues(), doChart, R.drawable.fade_blue);

        configChart(phChart, "pH");
        renderData(8f, 4f, 10f, 10f, phChart);
        setData(getpHValues(), phChart, R.drawable.fade_green);

        configChart(temperatureChart, "Temperature");
        renderData(24f, 8f, 30f, 10f, temperatureChart);
        setData(getTemperatureValues(), temperatureChart, R.drawable.fade_red);

        configChart(conductivityChart, "Conductivity");
        renderData(1.5f, 50f, 60f, 10f, conductivityChart);
        setData(getConductivityValues(), conductivityChart, R.drawable.fade_yellow);
    }

    // Get real values for each Chart
    public ArrayList<Entry> getDoValues() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 50));
        values.add(new Entry(2, 100));
        values.add(new Entry(3, 80));
        values.add(new Entry(4, 120));
        values.add(new Entry(5, 110));
        values.add(new Entry(6, 120));
        values.add(new Entry(7, 110));
        values.add(new Entry(8, 100));

        return values;
    }

    public ArrayList<Entry> getpHValues() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 5));
        values.add(new Entry(2, 6));
        values.add(new Entry(3, 6.5f));
        values.add(new Entry(4, 7));
        values.add(new Entry(5, 7.5f));
        values.add(new Entry(6, 6));
        values.add(new Entry(7, 5));
        values.add(new Entry(8, 4));

        return values;
    }

    public ArrayList<Entry> getTemperatureValues() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 18));
        values.add(new Entry(2, 19));
        values.add(new Entry(3, 20));
        values.add(new Entry(4, 21));
        values.add(new Entry(5, 19));
        values.add(new Entry(6, 18.5f));
        values.add(new Entry(7, 19.2f));
        values.add(new Entry(8, 20));

        return values;
    }

    public ArrayList<Entry> getConductivityValues() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 10));
        values.add(new Entry(2, 15));
        values.add(new Entry(3, 20));
        values.add(new Entry(4, 21.5f));
        values.add(new Entry(5, 19));
        values.add(new Entry(6, 18.5f));
        values.add(new Entry(7, 19.2f));
        values.add(new Entry(8, 28));

        return values;
    }

    // Get each chart Date Values (now used on each chart)
/*    public ArrayList<String> getDateValues() {

        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            labels.add(i + " 15/06/21");
        return labels;
    }*/

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

    public void renderData(float maximumLimit, float minimumLimit, float yAxisMax, float xAxisMax, LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(xAxisMax);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);
/*        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDateValues()));*/

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
        leftAxis.setAxisMaximum(yAxisMax);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        chart.getAxisRight().setEnabled(false);
    }


    private void setData(ArrayList<Entry> values, LineChart chart, int drawableId) {
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