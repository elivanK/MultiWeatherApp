package android.and09.multiweatherapp;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class WeatherPreferenceFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState,String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
