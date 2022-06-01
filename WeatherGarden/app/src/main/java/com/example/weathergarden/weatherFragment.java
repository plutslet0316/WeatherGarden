package com.example.weathergarden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weathergarden.weather.LocationData;
import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link weatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class weatherFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView loc, day, tem, con;
    private ImageButton add;
    WeatherProc weatherProc = null;
    WeatherInfo weatherInfo = null;
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
        loc.setText(addresses.get(1).getLocality() + " " + (addresses.get(1).getSubLocality() != null ? addresses.get(1).getSubLocality():"") + " " +  (addresses.get(1).getThoroughfare() != null ? addresses.get(1).getThoroughfare(): ""));
        tem.setText(weatherInfo.temp+" ℃");

        switch (weatherInfo.rainType){
            case "0":
                rainType = "맑음";
                break;
            case "1":
                rainType = "비";
                break;
            case "2":
                rainType = "비/눈";
                break;
            case "3":
                rainType = "눈";
                break;
            case "5":
                rainType = "빗방울";
                break;
            case "6":
                rainType = "빗방울/눈날림";
                break;
            case "7":
                rainType = "눈날림";
                break;
        }
        con.setText(rainType);

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

        weatherProc = new WeatherProc(getContext());
        weatherProc.getWeather();
        weatherInfo = weatherProc.getWeatherInfo();
        setWeather();
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