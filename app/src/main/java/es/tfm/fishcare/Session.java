package es.tfm.fishcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setJwt(String jwt) {
        prefs.edit().putString("jwt", jwt).apply();
    }

    public String getJwt() {
        return prefs.getString("jwt","");
    }

    public void setHatcheryId(String hatcheryId) {
        prefs.edit().putString("hatcheryId", hatcheryId).apply();
    }

    public String gethatcheryId() {
        return prefs.getString("hatcheryId","");
    }
}