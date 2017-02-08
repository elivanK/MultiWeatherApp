package android.and09.multiweatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public class DummyWeatherAPI implements IWeatherAPI {
    private JSONObject weatherdata;
    private DummyWeatherAPI() throws JSONException {
        weatherdata = new JSONObject();
        weatherdata.put("tempC", 21.0);
        weatherdata.put("description", "Sonnig");
        weatherdata.put("icon", "wsymbol_0001_sunny.png");
    }
    public static IWeatherAPI fromLocationName(String locationName) throws JSONException {
        DummyWeatherAPI api = new DummyWeatherAPI();
        api.weatherdata.put("name", locationName);
        return (IWeatherAPI)api;
    }
    public static IWeatherAPI fromLatLon(double lat, double
            lon) throws JSONException {
        DummyWeatherAPI api = new DummyWeatherAPI();
        api.weatherdata.put("coord", new
                JSONObject().put("lat", lat).put("lon", lon));
        return (IWeatherAPI)api;
    }


    @Override
    public double getTemperature() throws JSONException {
        return weatherdata.getDouble("tempC");
    }
    @Override
    public String getDescription() throws JSONException {
        return weatherdata.getString("description");
    }

    @Override
    public String getError() throws JSONException {
        return null;
    }

    @Override
    public String getProviderInfo() {
        return null;
    }

    // getError, getProviderInfo wie vor.
    @Override
    public String getIconPath() throws JSONException {
        return "dummyweatherservice/" +
                weatherdata.getString("icon");
    }
}