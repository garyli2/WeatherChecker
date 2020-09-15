package club.ayjpcc.weatherchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // listener
        EditText cityInput = findViewById(R.id.cityInput);
        Button confirmButton = findViewById(R.id.button);

        // on click listener to get city input value
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = ((EditText) findViewById(R.id.cityInput)).getText().toString();
                Intent transitionWeatherOverview = new Intent(MainActivity.this, WeatherOverview.class);

                // direct user to weather results page
                transitionWeatherOverview.putExtra("cityName", cityName);
                startActivity(transitionWeatherOverview);
            }
        });
    }
}
