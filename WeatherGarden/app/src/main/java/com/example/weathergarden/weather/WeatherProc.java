package com.example.weathergarden.weather;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class WeatherProc {
    Context context;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    WeatherInfo weatherInfo = null;

    Gson gson = new Gson();

    public WeatherProc(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("player_data", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    public WeatherInfo getWeatherInfo(){
        String weatherString = preferences.getString("current_weather","");
        return gson.fromJson(weatherString, WeatherInfo.class);
    }

    public int getWeather() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
            DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
            DateFormat timeFormat = new SimpleDateFormat("HH");

            sdFormat.setTimeZone(timeZone);
            timeFormat.setTimeZone(timeZone);

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, -40);

            String tempDate = sdFormat.format(calendar.getTime());
            String tempTime = timeFormat.format(calendar.getTime());


            //Log.d("Weather", tempDate + tempTime);
            // JSON데이터를 요청하는 URLstr을 만듭니다.
            String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
            // 홈페이지에서 받은 키
            String serviceKey = "1wjkBPEuUg0DUfQ8PTd0%2BOsxEOlFMKIqQBd3LWAfMkWPs59R1RBbZKSPa9%2Fv4lhgoLMJlpg22h21l8zlQtle8g%3D%3D";
            String pageNo = "1";
            String numOfRows = "10"; // 한 페이지 결과 수
            String data_type = "JSON"; // 타입 xml, json 등등 ..
            String baseDate = tempDate; // "20200821"이런식으로 api에서 제공하는 형식 그대로 적으시면 됩니당.
            String baseTime = tempTime + "00"; // API 제공 시간을 입력하면 됨
            String nx = "60"; // 위도
            String ny = "120"; // 경도

            String weatherString = preferences.getString("current_weather","");

            Log.d("Weather", baseDate+baseTime);

            LocationData locationData = gson.fromJson(preferences.getString("location_data", ""), LocationData.class);
            if(locationData != null){

                nx = locationData.nx;
                ny = locationData.ny;;

            }
            Log.d("Weather", nx + " " + ny);

            weatherInfo = new WeatherInfo();
/*
            if(weatherString != "") {
                weatherInfo = gson.fromJson(weatherString, WeatherInfo.class);

                if(weatherInfo.time.equals(baseDate+baseTime)) {
                    Log.d("Weather", weatherInfo.time + " " + baseDate+baseTime);

                    return 2;
                }
            } else {
            }

*/


            // 전날 23시 부터 153개의 데이터를 조회하면 오늘과 내일의 날씨를 알 수 있음

            StringBuilder urlBuilder = new StringBuilder(apiUrl);

            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8")); /* 한 페이지 결과 수 */
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(data_type, "UTF-8")); /* 타입 */
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜 */
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); // 경도
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8") + "&"); // 위도


            /* GET방식으로 전송해서 파라미터 받아오기*/

            // 어떻게 넘어가는지 확인하고 싶으면 아래 출력분 주석 해제
            URL url;
            String result;

            final BufferedReader[] rd = {null};
            HttpURLConnection conn = null;

            url = new URL(urlBuilder.toString());
            Log.d("Weather", url.toString());

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            HttpURLConnection finalConn = conn;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();

                    try {
                        rd[0] = new BufferedReader(new InputStreamReader(finalConn.getInputStream(), "UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            thread.join();

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = rd[0].readLine()) != null) {
                sb.append(line);
            }
            rd[0].close();
            conn.disconnect();
            result = sb.toString();
            Log.d("Weather", result);

            //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

            // 먼저 아이템만 배열로 가져온다
            JSONArray jsonArray = new JSONObject(result)
                    .getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");
            JSONObject jsonObj;

            /*
                결과예시

                PTY 0
                REH 32
                RN1 0
                T1H 25.7
                UUU 1.8
                VEC 202
                VVV 4.4
                WSD 4.7
             */

            jsonObj = jsonArray.getJSONObject(0);
            weatherInfo.time = jsonObj.getString("baseDate")+jsonObj.getString("baseTime");
            Log.d("Weather", weatherInfo.time);

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                String category = jsonObj.getString("category");
                String obsrValue = jsonObj.getString("obsrValue");
                // Log.d("Weather", category + " " + obsrValue);
                /*
                    강수형태 PTY 0 rainType
                    강수량 RN1 0 rainAmount
                    기온 T1H 25.7 temp
                    습도 REH 32 hum
                    풍속 WSD 4.7 wind
                 */
                switch (category) {
                    case "PTY":
                        weatherInfo.rainType = obsrValue;
                        break;
                    case "RN1":
                        weatherInfo.rainAmount = obsrValue;
                        break;
                    case "T1H":
                        weatherInfo.temp = obsrValue;
                        break;
                    case "REH":
                        weatherInfo.hum = obsrValue;
                        break;
                    case "WSD":
                        weatherInfo.wind = obsrValue;
                        break;
                }
            }
            // Log.d("Weather", weather + tmperature + "");


            String weatherJson = gson.toJson(weatherInfo);

            editor.putString("current_weather", weatherJson);
            editor.apply();

            //Log.d("Weather", preferences.getString("current_weather", ""));

            return 1;
        } catch (UnsupportedEncodingException e) {
            Log.d("Weather", e.getMessage());
        } catch (Exception e) {
            Log.d("Weather", e.getMessage());
        }

        return 0;
    }
}
