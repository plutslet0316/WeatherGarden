package com.example.weathergarden;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class WeatherTestActivity extends Activity {
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);

        tv = findViewById(R.id.text);
        WeatherTest t = new WeatherTest();

        tv.setText(t.getVillageWeather());
    }
}
