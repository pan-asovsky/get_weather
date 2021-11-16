import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

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

    public static void main(String[] args) {

        try {
            getWeather();
        } catch (NullPointerException e) {
            try {
                getWeather();
            } catch (IOException ex) {
                System.out.println("!");
            }
        } catch (IOException e) {
            System.out.println("IO");
        }
    }

    public static void getWeather() throws NullPointerException, IOException {

        String LOCATION = getLocation();

        try {
            Map<String, Object> responseMap = jsonToMap(getConnectionData(getCorrectURL(LOCATION)));
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
                            " m/s, gusts up to " + windGust + " m/s\n"
            );
        } catch (NullPointerException e) {
            getWeather();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConnectionData(String urlString) throws IOException {

        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);

        try {
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error in writing the city or the city is not in the database.\n" +
                    "Please check the correctness of the input, or change the city to another one.");
        }
        return result.toString();
    }

    public static String getLocation() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter your city: ");
        String location = reader.readLine();
        if (location.isEmpty()) {
            System.out.print("Empty request. Try again: ");
            location = reader.readLine();
        }
        return location;
    }

    public static String getCorrectURL(String LOCATION) {

        String API_KEY = "6df5fdbc30838dd60a6e5cbe8f75ebf9";
        return "https://api.openweathermap.org/data/2.5/weather?q="
                + LOCATION + "&appid=" + API_KEY + "&units=metric";
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

    public static int getPressure(int pressure) {
        return (int) Math.round(pressure * 0.75);
    }

}