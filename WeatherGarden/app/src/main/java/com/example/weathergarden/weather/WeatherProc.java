package com.example.weathergarden.weather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
import java.util.TimeZone;

public class WeatherProc {
    Activity context;
    WeatherInfo weatherInfo = null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();

    public WeatherProc(Activity context) {
        this.context = context;
        preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE);
        editor = preferences.edit();
        getWeather();
    }

    public WeatherInfo getWeatherInfo(){
        String weatherString = preferences.getString("current_weather","");
        return gson.fromJson(weatherString, WeatherInfo.class);
    }

    public int getWeather() {
        String weatherString = preferences.getString("current_weather","");

        if(weatherString != "")
            weatherInfo = gson.fromJson(weatherString, WeatherInfo.class);
        else
            weatherInfo = new WeatherInfo();

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

            Log.d("Weather", weatherInfo.time + " " + baseDate+baseTime);
            if(weatherInfo.time.equals(baseDate+baseTime))
                return 2;

            //Log.d("Weather", baseDate + baseTime);

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
            //Log.d("Weather", url.toString());

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
            //Log.d("Weather", result());

            //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

            String weather = null;
            String tmperature = null;

            // response 키를 가지고 데이터를 파싱
            JSONArray jsonArray = new JSONObject(result)
                    .getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");
            JSONObject jsonObj;

            /*
                PTY 0
                REH 32
                RN1 0
                T1H 25.7
                UUU 1.8
                VEC 202
                VVV 4.4
                WSD 4.7
             */

            weatherInfo.time = baseDate + baseTime;
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

            Log.d("Weather", preferences.getString("current_weather", ""));

            // 강수형태

            weather = "현재 날씨는 ";

            // 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
            switch (weatherInfo.rainType) {
                case "0":
                    weather += "맑은 상태로";
                    break;
                case "1":
                    weather += "비가 오는 상태로 ";
                    break;
                case "2":
                    weather += "비나 눈이 오는 상태로 ";
                    break;
                case "3":
                    weather += "눈이 오는 상태로 ";
                    break;
                case "5":
                    weather += "빗방울이 떨어지는 상태로 ";
                    break;
                case "6":
                    weather += "빗방울이나 눈이 날리는 상태로";
                    break;
                case "7":
                    weather += "눈이 날리는 상태로";
                    break;
            }


            tmperature = "기온은 " + weatherInfo.temp + "℃ 입니다.";

            return 1;
        } catch (UnsupportedEncodingException e) {
            Log.d("Weather", e.getMessage());
        } catch (Exception e) {
            Log.d("Weather", e.getMessage());
        }

        return 0;
    }
}
