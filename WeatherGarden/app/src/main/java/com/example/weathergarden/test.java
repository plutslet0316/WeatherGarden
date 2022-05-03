package com.example.weathergarden;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class test {
    //List<VillageWeather> datalist = new ArrayList<VillageWeather>();
    public void getVillageWeather() throws IOException, ParseException {
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);

        Log.d("test", "test");
        // JSON데이터를 요청하는 URLstr을 만듭니다.
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
        // 홈페이지에서 받은 키
        String serviceKey = "1wjkBPEuUg0DUfQ8PTd0%2BOsxEOlFMKIqQBd3LWAfMkWPs59R1RBbZKSPa9%2Fv4lhgoLMJlpg22h21l8zlQtle8g%3D%3D";
        String pageNo = "1";
        String numOfRows = "10"; // 한 페이지 결과 수
        String data_type = "JSON"; // 타입 xml, json 등등 ..
        String baseDate = tempDate; // "20200821"이런식으로 api에서 제공하는 형식 그대로 적으시면 됩니당.
        String baseTime = "1500"; // API 제공 시간을 입력하면 됨
        String nx = "60"; // 위도
        String ny = "120"; // 경도

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
        URL url = new URL(urlBuilder.toString());

        // 어떻게 넘어가는지 확인하고 싶으면 아래 출력분 주석 해제
        Log.d("test", url.toString());
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            //System.out.println("Response code: " + );

            BufferedReader rd;
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String result = sb.toString();
            Log.d("test", result);

        } catch (Exception e) {
            Log.d("test", e.getMessage() + "");

        }
    }
}

