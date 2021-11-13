import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class GetWeather {

    public static Map<String, Object> jsonToMap(String str) {
        return new Gson().fromJson
                (str, new TypeToken<HashMap<String, Object>>(){}
                        .getType());
    }

    //TODO Изменить параметры функции для вызова из консоли с переданным значением.
    public static void main(String[] args) {

        String LOCATION = getLocation();
        String API_KEY = "";
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                + LOCATION + "&appid=" + API_KEY + "&units=metric";

        try {
            Map<String, Object> responseMap = jsonToMap(getConnectionData(urlString));
            Map<String, Object> mainInfo = jsonToMap(responseMap.get("main").toString());
            Map<String, Object> windInfo = jsonToMap(responseMap.get("wind").toString());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> weatherInfoList = (List<Map<String, Object>>) (responseMap.get("weather"));
            Map<String, Object> weatherInfo = weatherInfoList.get(0);

            String weatherDescription = weatherInfo.get("description").toString();
            int temperature = getInt(mainInfo.get("temp"));
            int temperatureFeels = getInt(mainInfo.get("feels_like"));
            int humidity = getInt(mainInfo.get("humidity"));
            int pressure = getPressure(getInt(mainInfo.get("pressure")));
            double windSpeed = getDouble(windInfo.get("speed"));
            double windGust = getDouble(windInfo.get("gust"));
            String windDirection = getWindDirection(getDouble(windInfo.get("deg")));

            System.out.println(
                    "\nIn " + LOCATION + " is " +
                            weatherDescription +
                            "\nTemperature " + temperature + "°C, " +
                            "feels like " + temperatureFeels + "°C\n" +
                            "Humidity " + humidity + "%, pressure " + pressure + " mm\n" +
                            "Wind " + windDirection + ", " + windSpeed +
                            " m/s, gusts up to " + windGust + "m/s\n"
            );

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConnectionData(String address) throws IOException {

        StringBuilder result = new StringBuilder();
        URL url = new URL(address);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public static double getDouble(Object obj) {

        double result = Double.parseDouble(obj.toString());
        return Math.ceil(result * 10) / 10;
    }

    public static int getInt(Object obj) {
        return (int) Math.round(Double.parseDouble(obj.toString()));
    }

    public static String getWindDirection(double angle) {

        String[] windDirections = {"northern", "northeast", "eastern",
                "southeast", "south", "southwest", "western", "northwest"};
        return windDirections[(int) Math.floor((angle % 360) / 45)];
    }

    public static String getLocation() {

        String userLocation = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter your city: ");
            userLocation = reader.readLine();
        } catch (IOException e) {
            e.getMessage();
        }
        return userLocation;
    }

    public static int getPressure(int pressure) {
        return (int) Math.round(pressure * 0.75);
    }
}