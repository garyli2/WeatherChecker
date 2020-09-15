package club.ayjpcc.LibWeatherChecker;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class WeatherForecast {
    private String city;
    private int forecastNum;
    private ArrayList<WeatherInfo> forecast = new ArrayList<>();

    private WeatherForecast(String city) {
        this(city, 16);
    }

    private WeatherForecast(String city, int forecastsNum) {
        this.city = city;
        this.forecastNum = forecastsNum;
    }

    public ArrayList<WeatherInfo> getForecastList() {
        return this.forecast;
    }

    public static WeatherForecast getCurrentForecast(String city, String apiKey) throws IOException {
        if (apiKey == null) {
            throw new NullPointerException("Key is not present!");
        }
        URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid="+apiKey);
        InputStream stream = ((url.openConnection())).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String list = reader.readLine();
        JsonParser parser = new JsonParser();
        JsonArray listArray = parser.parse(list).getAsJsonObject().get("list").getAsJsonArray();
        WeatherForecast constructed = new WeatherForecast(city);
        for (int i = 0; i < 16; ++i) {
            constructed.getForecastList().add(WeatherInfo.parse(listArray.get(i).toString()));
            WeatherInfo.LOGGER.info("Added forecast for time "+constructed.getForecastList().get(i).getEpochTime());
        }
        return constructed;
    }

    public int getForecastNum() {
        return this.forecastNum;
    }

    public String getCity() {
        return this.city;
    }
}