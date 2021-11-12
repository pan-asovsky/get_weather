import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetWeather {

    public static Map<String, Object> jsonToMap(String str) {
        return new Gson().fromJson
                (str, new TypeToken<HashMap<String, Object>>() {}.getType());
    }

    public static void main(String[] args) {

        String API_KEY = "6df5fdbc30838dd60a6e5cbe8f75ebf9";
        String LOCATION = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Choice your city: ");
            LOCATION = reader.readLine();
        } catch (IOException e) {
            System.out.println("Wrong city or input!");
            e.printStackTrace();
        }

        String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                + LOCATION + "&appid=" + API_KEY + "&units=metric";

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            //System.out.println(result);

            Map<String, Object> responseMap = jsonToMap(result.toString());
            Map<String, Object> mainInfo = jsonToMap(responseMap.get("main").toString());
            Map<String, Object> windInfo = jsonToMap(responseMap.get("wind").toString());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> weatherInfoList = (List<Map<String, Object>>)(responseMap.get("weather"));
            Map<String, Object> weatherInfo = weatherInfoList.get(0);


            String weatherDescription = String.valueOf(weatherInfo.get("main"));
            int humidity = Integer.parseInt(new DecimalFormat("#").format(mainInfo.get("humidity")));
            int temperature = Integer.parseInt(new DecimalFormat("#").format(mainInfo.get("temp")));
            int temperatureFeels = Integer.parseInt(new DecimalFormat("#").format(mainInfo.get("feels_like")));
            double windSpeed = Double.parseDouble(new DecimalFormat("#").format(windInfo.get("speed")));
            String windDirection = getWindDirection(windInfo.get("deg"));

            System.out.println(
                    "\n" + LOCATION + ", " +
                    weatherDescription +
                    "\nTemperature: " + temperature + "°C\n" +
                    "Feels_like: " + temperatureFeels + "°C\n" +
                    "Humidity: " + humidity + "%" + "\n"  +
                    "Wind: " + windDirection + ", " + windSpeed + " m/s\n"
            );

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getWindDirection(Object angle) {

        double windAngle = Double.parseDouble(new DecimalFormat("#").format((Number) angle));
        String[] windDirections = {"northern", "northeast", "eastern",
                "southeast", "south", "southwest", "western", "northwest"};

        return windDirections[(int)Math.floor((windAngle % 360) / 45)];
    }
}