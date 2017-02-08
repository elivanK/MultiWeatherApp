package android.and09.multiweatherapp;

import java.net.URLEncoder;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class WorldWeatherOnlineAPI {

    private final static String APIKey = "34457effcabf4896b5a175541170801";
    private final static String baseURL = "http://api.worldweatheronline.com/premium/v1/weather.ashx?num_of_days=1&lang=de&format=json&key=" + APIKey + "&q=";
    private JSONObject weatherdata;

    private WorldWeatherOnlineAPI(String queryString) throws IOException, JSONException {
        String result = HttpRequest.request(baseURL + queryString);
        weatherdata = new JSONObject(result);
    }
    public static WorldWeatherOnlineAPI fromLocationName(String locationName) throws IOException,JSONException {
        return new WorldWeatherOnlineAPI(URLEncoder.encode(locationName,"UTF-8"));
    }
    public static WorldWeatherOnlineAPI fromLatLon(double lat, double lon) throws IOException, JSONException {
        return new WorldWeatherOnlineAPI("" + lat + "," + lon);
    }
    public static void main(String args[]) {
        try {
            WorldWeatherOnlineAPI api = fromLocationName("San Francisco");
            System.out.println(api.weatherdata.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
