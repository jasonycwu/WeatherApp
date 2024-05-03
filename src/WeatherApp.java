/**
 * @Author: Jason Y. Wu
 * @Date:   2024-05-01 12:38:28
 * @Last Modified by:   Jason Y. Wu
 * @Last Modified time: 2024-05-03 00:13:52
 */
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @Author: Jason Y. Wu
 * @Date:   2024-05-01 12:38:28
 * @Last Modified by:   Jason Y. Wu
 * @Last Modified time: 2024-05-01 12:52:18
 */

 // retrive weather data from API - fetch latest weather data and return it
 // the GUI will display the data to the user
public class WeatherApp {
    // fetch weather data for given location
    @SuppressWarnings("unchecked")
    public static JSONObject getWeatherData(String locationName) {
        // get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        // extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL with lat, long
        String urlString = "https://api.open-meteo.com/v1/forecast?" + "latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try {
            // call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);
            // check for response status
            if (conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                // read and store into string builder
                resultJson.append(scanner.nextLine());
            }
            // close scanner
            scanner.close();
            // close url connection
            conn.disconnect();
            // parse through data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve all hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            
            // get current hour data
            JSONArray time = (JSONArray) hourly.get("time");
            // find index of current time
            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);
            
            // get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build weather json data obj for access by front end
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();;
        }
        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        // replace any whitespace in location name to '+' to adhere to  API format
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try {
            // call API and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check for response status
            // 200 = successful connection
            if (conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // store API result
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // read and stopre the resulting json data into string builder
                while (scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                // close scanner
                scanner.close();

                // close url connection
                conn.disconnect();

                // parse JSON string into JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // could not find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            // attempt to create connection
            @SuppressWarnings("deprecation")
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            // connect to API
            conn.connect();
            return conn;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // unable to make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // iterate through time list to see which one matches curr time
        for (int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                // return index
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime(){
        //get curr date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // API format: 2023-09-02T00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        
        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if (weatherCode == 0L){ // 0L = 0 of type long
            weatherCondition = "Clear";
        } else if (weatherCode <= 3L && weatherCode > 0L){
            weatherCondition = "Cloudy";
        } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)){
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L){
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
