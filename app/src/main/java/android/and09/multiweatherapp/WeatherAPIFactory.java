package android.and09.multiweatherapp;

//Page 33, how to access classes from shared preference object.
//First option is to create a swich case or a more elegant solution
//Using Reflection API to change between classes:

public class WeatherAPIFactory {
    private WeatherAPIFactory() {}

    public static IWeatherAPI fromLocationName(String className,String locationName) throws Exception {
        Class c = Class.forName("android.and09.multiweatherapp." + className);
        IWeatherAPI api = (IWeatherAPI)c.getMethod("fromLocationName",String.class).invoke(null, locationName);
        return api;
    }
    public static IWeatherAPI fromLatLon(String className, double lat, double lon) throws Exception {
        Class c = Class.forName("android.and09.multiweatherapp." + className);
        IWeatherAPI api = (IWeatherAPI)c.getMethod("fromLatLon",double.class, double.class).invoke(null, lat, lon);
        return api;
    }
}
