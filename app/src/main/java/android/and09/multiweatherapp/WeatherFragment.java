package android.and09.multiweatherapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class WeatherFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_fragment,container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        ((WeatherActivity)getActivity()).fragmentReady.open();
    }
}

