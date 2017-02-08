package android.and09.multiweatherapp;

import java.net.URLEncoder;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;

public class OpenWeatherMapAPI implements IWeatherAPI {
    // OpenWeatherMapAPI api = fromLatLon (37.77, -122.42);
    private final static String APIKey = "1dfd38b47bbc9f17f916b844a3022260";
    private final static String baseURL = "http://api.openweathermap.org/data/2.5/weather?lang=de&APPID=" + APIKey + "&";

    //final static String getLatLog = "http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=1dfd38b47bbc9f17f916b844a3022260";
    private JSONObject weatherdata;

    private OpenWeatherMapAPI(String queryString) throws IOException, JSONException {
        String result = HttpRequest.request(baseURL + queryString);
        weatherdata = new JSONObject(result);
    }
    public static OpenWeatherMapAPI fromLatLon(double lat, double lon) throws IOException, JSONException {
        return new OpenWeatherMapAPI("lat=" + lat + "&lon=" + lon);
    }

    public static OpenWeatherMapAPI fromLocationName(String locationName) throws IOException, JSONException {
        return new OpenWeatherMapAPI("q=" + URLEncoder.encode(locationName, "UTF-8"));

    }

    @Override
    public double getTemperature() throws JSONException {
        JSONObject main = weatherdata.getJSONObject("main");
        double tempKelvin = main.getDouble("temp");
        //-273.15 since its in Kelvin 0c = -273.15:
        return tempKelvin - 273.15;
    }

    @Override
    public String getDescription() throws JSONException {
        JSONArray weather = weatherdata.getJSONArray("weather");
        String description = weather.getJSONObject(0).getString("description");
        return description;
    }

    @Override
    public String getError() throws JSONException {
        return weatherdata.getString("message");
    }

    @Override
    public String getProviderInfo() {
        return "http://www.openweathermap.org";
    }

    @Override
    public String getIconPath() throws JSONException {
        JSONArray weather = weatherdata.getJSONArray("weather");
        String icon = weather.getJSONObject(0).getString("icon");
        return "openweathermap/" + icon + ".png";
    }

    }