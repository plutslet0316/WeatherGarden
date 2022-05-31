package com.example.weathergarden;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;


public class MainActivityWidget extends AppCompatActivity {

    Button start, stop;
    ImageButton timbtn;
    View.OnClickListener cl;
    TextView myText, upText;

    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_widget);

        start = (Button) findViewById(R.id.fore_start);
        stop = (Button) findViewById(R.id.fore_stop);
        timbtn = (ImageButton) findViewById(R.id.timebtn);
        myText = (TextView) findViewById(R.id.text);
        upText = (TextView) findViewById(R.id.text2);

        //myText.setText("최근 업데이트 " + formatDate()); // text에 시간 세팅
        WeatherProc weatherProc =new WeatherProc(this);
        weatherProc.getWeather();

        WeatherInfo weatherInfo = weatherProc.getWeatherInfo();

        myText.setText("현재" + weatherInfo.temp + "℃ 입니다.");


        cl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.fore_start:
                        startService();
                        Toast.makeText(getApplicationContext(), "서비스 시작", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.fore_stop:
                        Toast.makeText(getApplicationContext(), "서비스 중지", Toast.LENGTH_SHORT).show();
                        stopService();
                        break;
                    case R.id.timebtn:
                        upText.setText("업데이트" + formatDate());
                        break;
                }
            }
        };
        start.setOnClickListener(cl);
        stop.setOnClickListener(cl);
        timbtn.setOnClickListener(cl);

    }

    private String formatDate() {
        long mNow = System.currentTimeMillis(); // 현재 시간 가져옴
        Date mReDate = new Date(mNow); // Date 형식 적용
        SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd HH:mm"); // 시간을 원하는 포맷으로 변경
        String formatDate = mFormat.format(mReDate);

        return formatDate;
    }

    // 어플 종료 후에도 실행되도록 startForegroundService 이용
    // 어플 종류 후에도 실행 원치 않으면 startService 이용
    // 알림 서비스 실행
    public void startService() {
        serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
    }

    // 알림 서비스 중지, 바인딩만 구현하고 싶을 때는 이 메소드 구현 x
    public void stopService() {
        serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
    }

}
