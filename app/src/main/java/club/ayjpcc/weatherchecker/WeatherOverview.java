package club.ayjpcc.weatherchecker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import club.ayjpcc.LibWeatherChecker.WeatherForecast;
import club.ayjpcc.LibWeatherChecker.WeatherInfo;

public class WeatherOverview extends AppCompatActivity {
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // for anyone looking at this from github, put your openweathermap api key here
        WeatherInfo.setApiKey("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_overview);
        cityName = getIntent().getStringExtra("cityName");

        // thread to
        new Thread(){
            @Override
            public void run() {
                try {
                    // Current
                    final WeatherInfo current = WeatherInfo.getCurrentWeather(cityName);

                    // Forecast
                    // also put your openweathermap api key here
                    final WeatherForecast forecast = WeatherForecast.getCurrentForecast("Toronto", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCurrentUI(current);
                            updateForecastUI(forecast);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void updateCurrentUI(WeatherInfo w) {
        String currentCondition = w.getCurrentCondition();
        String currentDescription = w.getCurrentDescription();
        int currentTemp = w.getCurrentTemperature();
        int currentCloudPercentage = w.getCurrentCloudPercentage();
        String iconID = w.getIconID();

        // City intro
        TextView cityView = findViewById(R.id.introCity);
        String currentText = cityView.getText().toString();
        cityView.setText(currentText+cityName);

        // Condition
        TextView conditionView = findViewById(R.id.weatherStatus);
        conditionView.setText(currentCondition);

        // Description
        TextView descriptionView = findViewById(R.id.weatherDescription);
        descriptionView.setText(currentDescription);

        // Temperature
        TextView tempView = findViewById(R.id.temp);
        String preserveCelsius = tempView.getText().toString();
        tempView.setText(currentTemp+preserveCelsius);

        // Image
        ImageView indicatorIcon = findViewById(R.id.weatherIcon);
        Picasso.get().load("https://openweathermap.org/img/w/"+iconID+".png").fit().into(indicatorIcon);
    }

    private void updateForecastUI( WeatherForecast f) {
        // Forecast
        ScrollView scroll = findViewById(R.id.forecastScroll);
        TableLayout table = new TableLayout(this);

        // programmatically add forecast results tables
        ArrayList<WeatherInfo> array = f.getForecastList();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, h a");
        for (int i = 0; i < f.getForecastNum();i++) {
            TableRow row = new TableRow(this);
            final WeatherInfo info = array.get(i);

            // Image icon
            final ImageView icon = new ImageView(this);

            // thread to set each row's weather icon
            new Thread() {
                @Override
                public void run() {
                    try {
                        final Bitmap bitmap = Picasso.get().load("https://openweathermap.org/img/w/"+info.getIconID()+".png").get();
                        // modify ui for weather icon
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                icon.setImageBitmap(bitmap);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            row.addView(icon);

            // time
            TextView time = new TextView(this);
            long epochTime = info.getEpochTime();
            Date date = new Date(epochTime*1000); // because long milisecond precision, instead of second

            time.setText(format.format(date));
            row.addView(time);

            // temperature
            TextView temp = new TextView(this);
            temp.setText(info.getCurrentTemperature()+"°C");
            row.addView(temp);

            // description
            TextView desc = new TextView(this);
            desc.setText(info.getCurrentCondition());
            row.addView(desc);

            table.addView(row);

        }

        // make everything autoscale
        table.setColumnStretchable(0,true);
        table.setColumnStretchable(1,true);
        table.setColumnStretchable(2,true);
        table.setColumnStretchable(3,true);

        scroll.addView(table);
    }
}
