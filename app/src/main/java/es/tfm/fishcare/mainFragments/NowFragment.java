package es.tfm.fishcare.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Date;

import es.tfm.fishcare.R;
import es.tfm.fishcare.SensorValue;
import es.tfm.fishcare.SensorValueListAdapter;
import es.tfm.fishcare.SensorValueState;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NowFragment extends Fragment {

    ListView list;

    public NowFragment() {
        // Required empty public constructor
    }

    private SensorValue[] getSensorValues() {
        // Get real sensor values from REST request
        // Get maximum and minimum values allowed
        return new SensorValue[]{
                new SensorValue("dO", "95", new Date(), SensorValueState.OK, "%"),
                new SensorValue("pH", "7,5", new Date(), SensorValueState.WARNING, ""),
                new SensorValue("Conductivity", "10,5", new Date(), SensorValueState.OK, "mS/cm"),
                new SensorValue("Temperature", "20", new Date(), SensorValueState.OK, "ºC"),
                new SensorValue("Water flow", "yes", new Date(), SensorValueState.OK, ""),
        };
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_now, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        list = view.findViewById(R.id.nowList);
        SensorValueListAdapter adapter = new SensorValueListAdapter(getActivity(), getSensorValues());
        list.setAdapter(adapter);
    }
}