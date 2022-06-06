package com.example.weathergarden.weather;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.weathergarden.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public ArrayList<TomorrowWeatherInfo> getTomorrowWeatherInfo(){
        String weatherString = preferences.getString("current_tomorrow_weather","");
        return gson.fromJson(weatherString, new TypeToken<ArrayList<TomorrowWeatherInfo>>(){}.getType());
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
            String serviceKey = context.getResources().getString(R.string.shot_weather_api);
            String pageNo = "1";
            String numOfRows = "10"; // 한 페이지 결과 수
            String data_type = "JSON"; // 타입 xml, json 등등 ..
            String baseDate = tempDate; // "20200821"이런식으로 api에서 제공하는 형식 그대로 적으시면 됩니당.
            String baseTime = tempTime + "00"; // API 제공 시간을 입력하면 됨
            String nx = "60"; // 위도
            String ny = "120"; // 경도

            String weatherString = preferences.getString("current_weather", "");

            Log.d("Weather", baseDate + baseTime);

            LocationData locationData = gson.fromJson(preferences.getString("location_data", ""), LocationData.class);
            if (locationData != null) {

                nx = locationData.nx;
                ny = locationData.ny;
                ;

            }
            Log.d("Weather", nx + " " + ny);
            weatherInfo = new WeatherInfo();
/*
            if (weatherString != "") {
                weatherInfo = gson.fromJson(weatherString, WeatherInfo.class);

                if (weatherInfo.time.equals(baseDate + baseTime)) {
                    Log.d("Weather", weatherInfo.time + " " + baseDate + baseTime);

                    return 2;
                }
            } else {
                weatherInfo = new WeatherInfo();
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
            //Log.d("Weather", result);

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

    public int getTomorrowWeather() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
            DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
            DateFormat timeFormat = new SimpleDateFormat("HH");

            sdFormat.setTimeZone(timeZone);
            timeFormat.setTimeZone(timeZone);

            Calendar calendar = Calendar.getInstance();

            // 제공 시간에 맞게 시간 바꾸기
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, -10);

            // 3시간 - 1시간 간격으로 제공함
            int hour = Integer.parseInt(timeFormat.format(calendar.getTime()));
            calendar.add(Calendar.HOUR, -((Integer.valueOf(hour) % 3) + 1) );

            String tempDate = sdFormat.format(calendar.getTime());
            String tempTime = timeFormat.format(calendar.getTime());


            //Log.d("TomorrowWeather", tempDate + tempTime);
            // JSON데이터를 요청하는 URLstr을 만듭니다.
            String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
            // 홈페이지에서 받은 키
            String serviceKey = context.getResources().getString(R.string.shot_weather_api);
            String pageNo = "1";
            String numOfRows = "1000"; // 한 페이지 결과 수
            String data_type = "JSON"; // 타입 xml, json 등등 ..
            String baseDate = tempDate; // "20200821"이런식으로 api에서 제공하는 형식 그대로 적으시면 됩니당.
            String baseTime = tempTime + "00"; // API 제공 시간을 입력하면 됨
            String nx = "60"; // 위도
            String ny = "120"; // 경도

            String weatherString = preferences.getString("current_tomorrow_weather","");

            Log.d("TomorrowWeather", baseDate+baseTime);

            LocationData locationData = gson.fromJson(preferences.getString("location_data", ""), LocationData.class);
            if(locationData != null){

                nx = locationData.nx;
                ny = locationData.ny;;

            }
            Log.d("TomorrowWeather", nx + " " + ny);

            // 제공 시간부터 내일 모래까지 알 수 있다.
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
            //Log.d("TomorrowWeather", url.toString());

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
            Log.d("TomorrowWeather", result);

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

            ArrayList<TomorrowWeatherInfo> tomorrowWeatherList = new ArrayList<>();
            jsonObj = jsonArray.getJSONObject(0);
            int i = 0;
            Log.d("TomorrowWeather", jsonArray.length()+"");

            do {
                TomorrowWeatherInfo tomorrowWeather = new TomorrowWeatherInfo();
                tomorrowWeather.fcstDate = jsonArray.getJSONObject(i).getString("fcstDate");
                tomorrowWeather.fcstTime = jsonArray.getJSONObject(i).getString("fcstTime");
                int k = i;
                String s = tomorrowWeather.fcstTime + " ";

                for(k = i; k < jsonArray.length(); k++) {
                    jsonObj = jsonArray.getJSONObject(k);

                    if(!tomorrowWeather.fcstTime.equals(jsonObj.getString("fcstTime"))) break;

                    String category = jsonObj.getString("category");
                    String fcstValue = jsonObj.getString("fcstValue");
                    s += category + " " + fcstValue +" ";

                    // Log.d("TomorrowWeather", category + " " + fcstValue);
                /*
                    POP 강수확률
                    PTY 강수형태
                    PCP 1시간 강수량
                    REH 습도
                    SNO 1시간 신적설
                    SKY 하늘상태
                    TMP 1시간 기온
                    TMN 일 최저기온
                    TMX 일 최고기온
                    UUU 풍속(동서성분)
                    VVV 풍속(남북성분)
                    WAV 파고
                    VEC 풍향
                    WSD 풍속
                 */
                    switch (category) {
                        case "PTY":
                            tomorrowWeather.rainType = fcstValue;
                            break;
                        case "TMP":
                            tomorrowWeather.temp = fcstValue;
                            break;
                        case "REH":
                            tomorrowWeather.humidity = fcstValue;
                            break;
                        case "SKY":
                            tomorrowWeather.sky = fcstValue;
                            break;
                    }

                }

                i = k;
                tomorrowWeatherList.add(tomorrowWeather);
                Log.d("TomorrowWeather", s);
            } while (i < jsonArray.length());

            String tomorrowWeatherListJson = gson.toJson(tomorrowWeatherList);

            editor.putString("current_tomorrow_weather", tomorrowWeatherListJson);
            editor.apply();

            //Log.d("TomorrowWeather", preferences.getString("current_tomorrow_weather", ""));

            return 1;
        } catch (UnsupportedEncodingException e) {
            Log.d("TomorrowWeather", e.getMessage());
        } catch (Exception e) {
            Log.d("TomorrowWeather", e.getMessage());
        }

        return 0;
    }
}
