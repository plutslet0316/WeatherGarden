package com.example.weathergarden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weathergarden.weather.LocationData;
import com.example.weathergarden.weather.TomorrowWeatherInfo;
import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link weatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class weatherFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView loc, day, tem, con;
    private ImageView wea;
    private ImageButton add;
    WeatherProc weatherProc = null;
    WeatherInfo weatherInfo = null;
    ArrayList<TomorrowWeatherInfo> tomorrowWeatherList = null;

    /*
    <작성해야될 코드> // 날씨 메인 화면에서
    - 날씨에 따라 날씨 메인화면 배경색 바뀜 ex) 화창함-노란색, 비-파란색
      색상 코드 -> 맑음 : #ffccbc , 비 : #b2dfdb
      흐림 : #e0e0e0 ,바람 : #bbdefb, 번개 : #ffecb3
      눈 : #e0f7fa , 안개 : #efebe9
    - 날씨에 따라 날씨 이미지뷰 바뀜(drawable에 있는 아이콘으로 이미지뷰 임시 설정)
    - 날씨에 따라 아레 주간 날씨 아이콘도 바뀜..
    // 주소 구현시 -> 주소 옆 화살표 클릭하면 등록한 주소들 볼 수 있고 클릭시 이동 가능
    */



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public weatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment weatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static weatherFragment newInstance(String param1, String param2) {
        weatherFragment fragment = new weatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setWeather() {
        Gson gson = new Gson();
        Geocoder geocoder = new Geocoder(getContext(), Locale.KOREAN);
        List<Address> addresses = null;

        // 위치 가져오기
        SharedPreferences preferences = getContext().getSharedPreferences("player_data", Context.MODE_PRIVATE);
        LocationData locationData = gson.fromJson(preferences.getString("location_data", ""), LocationData.class);

        // 위치 주소로 변환하기
        try {
            addresses = geocoder.getFromLocation(
                    Double.parseDouble(locationData.x),
                    Double.parseDouble(locationData.y),
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
        }


        String rainType = "없음";

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        DateFormat sdFormat = new SimpleDateFormat("MM-dd hh:mm a");
        sdFormat.setTimeZone(timeZone);
        Date now = new Date(System.currentTimeMillis());

        String dayText = sdFormat.format(now);
        Log.d("WeatherFragment", dayText);
        day.setText(dayText);
        loc.setText((addresses.get(1).getLocality() != null ? addresses.get(1).getLocality() : addresses.get(1).getAdminArea()) + " " + (addresses.get(1).getSubLocality() != null ? addresses.get(1).getSubLocality():"") + " " +  (addresses.get(1).getThoroughfare() != null ? addresses.get(1).getThoroughfare(): ""));
        tem.setText(weatherInfo.temp+" ℃");

        /*
              색상 코드 -> 맑음 : #ffccbc , 비 : #b2dfdb
      흐림 : #e0e0e0 ,바람 : #bbdefb, 번개 : #ffecb3
      눈 : #e0f7fa , 안개 : #efebe9
         */
        switch (weatherInfo.rainType){
            case "0":
                rainType = "맑음";
                wea.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
                view.setBackgroundColor(Color.parseColor("#ffccbc"));
                break;
            case "1":
                rainType = "비";
                wea.setImageResource(R.drawable.ic_baseline_cloud_24);
                view.setBackgroundColor(Color.parseColor("#b2dfdb"));
                break;
            case "2":
                rainType = "비/눈";
                wea.setImageResource(R.drawable.ic_baseline_cloud_24);
                view.setBackgroundColor(Color.parseColor("#b2dfdb"));
                break;
            case "3":
                rainType = "눈";
                wea.setImageResource(R.drawable.ic_baseline_cloud_24);
                view.setBackgroundColor(Color.parseColor("#e0f7fa"));
                break;
            case "5":
                rainType = "빗방울";
                wea.setImageResource(R.drawable.ic_baseline_cloud_24);
                view.setBackgroundColor(Color.parseColor("#b2dfdb"));
                break;
            case "6":
                rainType = "빗방울/눈날림";
                wea.setImageResource(R.drawable.ic_baseline_cloud_24);
                view.setBackgroundColor(Color.parseColor("#b2dfdb"));
                break;
            case "7":
                rainType = "눈날림";
                wea.setImageResource(R.drawable.ic_baseline_cloud_24);
                view.setBackgroundColor(Color.parseColor("#e0f7fa"));
                break;
        }
        con.setText(rainType);

    }

    public void setTomorrowWeather() {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        DateFormat sdFormat = new SimpleDateFormat("MM-dd hh:mm a");
        sdFormat.setTimeZone(timeZone);
        Calendar calendar = Calendar.getInstance();
        Date now = new Date(System.currentTimeMillis());

        LinearLayout tomorrowFrame = view.findViewById(R.id.tomorrow_frame);
        tomorrowFrame.removeAllViews();
        int i = 0;
        int k = 0;
        Log.d("weatherFragment", tomorrowWeatherList.size() + "");

        do {
            String date = tomorrowWeatherList.get(i).fcstDate;
            View tomorrowView = view.inflate(view.getContext(), R.layout.tomorrow_weather, null);
            TextView weatherDate = tomorrowView.findViewById(R.id.tomorrow_date);
            LinearLayout tomorrowList = tomorrowView.findViewById(R.id.tomorrow_weather_list);

            weatherDate.setText(date.substring(4,5).replace("0","") + date.substring(5,6) +
                    "." + date.substring(6,7).replace("0", "") + date.substring(7));
            for (k = i; k < tomorrowWeatherList.size(); k++) {
                if(!date.equals(tomorrowWeatherList.get(k).fcstDate)) {
                    Log.d("weather", date);
                    break;
                }
                View tomorrowItem = View.inflate(view.getContext(), R.layout.item_weather, null);

                ImageView tomorrowImage = tomorrowItem.findViewById(R.id.weather_image);
                TextView time = tomorrowItem.findViewById(R.id.weather_date);
                TextView temp = tomorrowItem.findViewById(R.id.weather_temp);

                String timeText = tomorrowWeatherList.get(k).fcstTime;

                time.setText(timeText.substring(0,2) + ":" + timeText.substring(2));
                temp.setText(tomorrowWeatherList.get(k).temp + " ℃");

                switch (tomorrowWeatherList.get(k).rainType){
                    // 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
                    case "0":
                        switch (tomorrowWeatherList.get(k).sky){
                            // 맑음(1), 구름많음(3), 흐림(4)
                            case "1":
                                tomorrowImage.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
                                break;
                            case "3":
                                tomorrowImage.setImageResource(R.drawable.ic_baseline_cloud_24);
                                break;
                            case "4":
                                tomorrowImage.setImageResource(R.drawable.ic_baseline_cloud_24);
                                break;
                        }
                        break;
                    case "1":
                        tomorrowImage.setImageResource(R.drawable.ic_baseline_cloud_24);
                        break;
                    case "2":
                        tomorrowImage.setImageResource(R.drawable.ic_baseline_cloud_24);
                        break;
                    case "3":
                        tomorrowImage.setImageResource(R.drawable.ic_baseline_cloud_24);
                        break;
                    case "4":
                        tomorrowImage.setImageResource(R.drawable.ic_baseline_cloud_24);
                        break;
                }

                tomorrowList.addView(tomorrowItem);
            }

            tomorrowFrame.addView(tomorrowView);
            i = k;
        } while (i < tomorrowWeatherList.size());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        weatherProc = new WeatherProc(view.getContext());

        Handler handler = new Handler();
        tomorrowWeatherList = weatherProc.getTomorrowWeatherInfo();
        if(tomorrowWeatherList != null)
            setTomorrowWeather();

        new Thread() {
            @Override
            public void run() {
                super.run();
                weatherProc.getTomorrowWeather();
                tomorrowWeatherList = weatherProc.getTomorrowWeatherInfo();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setTomorrowWeather();
                    }
                });
            }
        }.start();
        
        new Thread() {
            @Override
            public void run() {
                super.run();
                weatherProc.getWeather();
                weatherInfo = weatherProc.getWeatherInfo();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setWeather();
                    }
                });
            }
        }.start();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_weather, container, false);
        view = (ViewGroup) inflater.inflate(R.layout.fragment_weather, container, false);
        loc = view.findViewById(R.id.location);
        day = view.findViewById(R.id.day);
        tem = view.findViewById(R.id.temperature);
        con = view.findViewById(R.id.condition);
        wea = view.findViewById(R.id.imageView5);

        add = (ImageButton) view.findViewById(R.id.add);

        add.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(),weather_gps.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}