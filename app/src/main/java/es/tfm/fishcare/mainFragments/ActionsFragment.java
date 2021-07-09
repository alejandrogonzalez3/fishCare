package es.tfm.fishcare.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.tfm.fishcare.Action;
import es.tfm.fishcare.ActionsListAdapter;
import es.tfm.fishcare.R;
import es.tfm.fishcare.RestService;
import es.tfm.fishcare.SensorValue;
import es.tfm.fishcare.SensorValueState;
import es.tfm.fishcare.notifications.NotificationListAdapter;
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
    OkHttpClient client = RestService.getClient();
    ListView list;

    public ActionsFragment() {
        // Required empty public constructor
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
        getActuators();
    }

    private void getActuators() {
        HttpUrl.Builder urlBuilder;
        urlBuilder = RestService.getActuatorUrlBuilder();
        urlBuilder.addPathSegment("all");
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
}