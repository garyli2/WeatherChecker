package club.ayjpcc.LibWeatherChecker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class WeatherInfo {
    public static final Logger LOGGER = Logger.getLogger(WeatherInfo.class.getName());
    private static String key = null;
    private String currentCondition;
    private String description;
    private String iconID;
    private int temperature;
    private int cloudPercentage;
    private int epochTime;

    private WeatherInfo(int epochTime, String currentCondition, String desc, int temp, int cloudPercentage, String iconID) {
        this.currentCondition = currentCondition;
        this.temperature = temp;
        this.description = desc;
        this.cloudPercentage = cloudPercentage;
        this.epochTime = epochTime;
        this.iconID = iconID;
    }

    public static void setApiKey(String apiKey) {
        key = apiKey;
    }

    public String getCurrentCondition() {
        return this.currentCondition;
    }

    public String getCurrentDescription() {
        return this.description;
    }

    public int getCurrentTemperature() {
        return this.temperature;
    }

    public int getCurrentCloudPercentage() {
        return this.cloudPercentage;
    }
    
    public String getIconID() {
    	return this.iconID;
    }
    public int getEpochTime() {
        return this.epochTime;
    }

    static WeatherInfo parse(String json) throws IOException {
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();
        JsonObject main = (JsonObject)root.get("main");
        int temp = (int)(main.get("temp").getAsDouble() - 273.15);
        JsonArray weather = root.getAsJsonArray("weather");
        JsonObject objInArray = weather.get(0).getAsJsonObject();
        String mainWeather = objInArray.get("main").getAsString();
        String descWeather = objInArray.get("description").getAsString();
        String iconID = objInArray.get("icon").getAsString();
        int cloudPercent = root.get("clouds").getAsJsonObject().get("all").getAsInt();
        int epochTime = root.get("dt").getAsInt();
        return new WeatherInfo(epochTime, mainWeather, descWeather, temp, cloudPercent, iconID);
    }
    public static WeatherInfo getCurrentWeather(String cityName) throws IOException {
        if (key == null) {
            throw new NullPointerException("Key is not present!");
        }
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid="+key);
        InputStream stream = ((HttpsURLConnection)(url.openConnection())).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = reader.readLine();
        LOGGER.info(line);
        return WeatherInfo.parse(line);
    }
}

