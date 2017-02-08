package android.and09.multiweatherapp;

import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.StringBuilder;

public class HttpRequest {

    public static String request(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        //We use BufferReader to read the API response into a StringBuilder:
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while((inputLine = in.readLine()) != null)
            builder.append(inputLine);
        in.close();
        return builder.toString();
    }


    public static void main(String args[]) {
        try {
            String result = request("http://api.openweathermap.org/data/2.5/weather?q=San+Francisco&lang=de&APPID=1dfd38b47bbc9f17f916b844a3022260");
            System.out.println(result);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}