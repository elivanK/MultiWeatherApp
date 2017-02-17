package android.and09.multiweatherapp;

import android.widget.TextView;

import java.net.URLEncoder;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
public class ServletWeatherAPI implements IWeatherAPI {

    private static String baseURL = "http://192.168.2.1:8080/WeatherService/weather?";

    public static void setServer(String server) {
        ServletWeatherAPI.baseURL = server;
    }
    private JSONObject weatherdata;

    private ServletWeatherAPI(String queryString) throws
            IOException, JSONException {
        String result = HttpRequest.request(baseURL + queryString);
        weatherdata = new JSONObject(result);
    }

    public static IWeatherAPI fromLocationName(String locationName) throws IOException, JSONException {
        return new ServletWeatherAPI("q=" + URLEncoder.encode(locationName, "UTF-8"));
    }

    public static IWeatherAPI fromLatLon(double lat, double
            lon) throws IOException, JSONException {
        return new ServletWeatherAPI("lat=" + lat + "&lon=" + lon);
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
    public String getError() {
        return "";
    }

    @Override
    public String getProviderInfo() {
        return "ServletWeatherService";
    }

    @Override
    public String getIconPath() throws JSONException {
        return "serlvetweatherservice/" +
                weatherdata.getString("icon");
    }
}
