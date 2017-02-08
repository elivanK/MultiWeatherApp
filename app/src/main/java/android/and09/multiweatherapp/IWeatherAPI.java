package android.and09.multiweatherapp;

import org.json.JSONException;

public interface IWeatherAPI {


    double getTemperature() throws JSONException;
    String getDescription() throws JSONException;
    String getError() throws JSONException;
    String getProviderInfo();
    String getIconPath() throws JSONException;
}
