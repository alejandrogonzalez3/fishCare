package es.tfm.fishcare.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Date;

import es.tfm.fishcare.Action;
import es.tfm.fishcare.ActionsListAdapter;
import es.tfm.fishcare.R;
import es.tfm.fishcare.SensorValue;
import es.tfm.fishcare.SensorValueState;
import es.tfm.fishcare.notifications.NotificationListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActionsFragment extends Fragment {

    ListView list;

    public ActionsFragment() {
        // Required empty public constructor
    }

    private Action[] getActions() {
        // Get allowed actions of the system
        return new Action[]{new Action("Oxygenator", true), new Action("Water pump", false)};
    }

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
        ActionsListAdapter adapter = new ActionsListAdapter(getActivity(), getActions());
        list.setAdapter(adapter);
    }
}