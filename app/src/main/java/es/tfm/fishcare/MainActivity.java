package es.tfm.fishcare;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import es.tfm.fishcare.mainFragments.ActionsFragment;
import es.tfm.fishcare.mainFragments.HistoryFragment;
import es.tfm.fishcare.mainFragments.NotificationsFragment;
import es.tfm.fishcare.mainFragments.NowFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Create new fragment and transaction
        Fragment nowFragment = new NowFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.flFragment, nowFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.now:
                    selectedFragment = new NowFragment();
                    break;
                case R.id.history:
                    selectedFragment = new HistoryFragment();
                    break;
                case R.id.notifications:
                    selectedFragment = new NotificationsFragment();
                    break;
                case R.id.actions:
                    selectedFragment = new ActionsFragment();
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, selectedFragment)
                    .commit();
            return true;
        }
    };
}