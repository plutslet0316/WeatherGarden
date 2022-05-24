package com.example.weathergarden;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.weathergarden.weather.GPS;
import com.example.weathergarden.weather.LocationData;
import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;

public class WeatherActivityTest extends Activity {
    TextView tv;
    WeatherProc weatherProc;
    WeatherInfo weatherInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);

        tv = findViewById(R.id.text);

        weatherProc = new WeatherProc(this);
        weatherProc.getWeather();
        weatherInfo = weatherProc.getWeatherInfo();

        String text = "현재 온도는 " + weatherInfo.temp + "℃ 입니다.";
        tv.setText(text);
    }
}
