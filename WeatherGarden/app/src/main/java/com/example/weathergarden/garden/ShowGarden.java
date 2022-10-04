package com.example.weathergarden.garden;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weathergarden.GardenFragment;
import com.example.weathergarden.R;
import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;
import com.example.weathergarden.weather.WeatherUltraFastInfo;
import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ShowGarden {
    View view;
    Activity activity;
    GardenDao gardenDao;
    List<GardenInfo> gardenList;
    WeatherUltraFastInfo weatherInfo;

    String info, plantState;
    int growMax, growMin, growPoint, limit;

    ShowDao showDao;

    public ShowGarden(View view, Activity activity, GardenDao gardenDao) {
        this.view = view;
        this.activity = activity;
        this.gardenDao = gardenDao;

        weatherInfo = new WeatherProc(activity).getWeatherUltraFastInfo().get(0);
        showDao = new ShowDao(activity);
    }
    public void show(){
        // 정원 정보 가져오기
        // 항상 스레드를 써야한다.
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                gardenList = gardenDao.readGardenInfoList();

            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 정보 표시.
        setWeather();
        showInfo();
    }
    
    // 받은 정보 가공해서 표시하기
    void showInfo() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (GardenInfo gardenInfo : gardenList) {
                    GroundInfo groundInfo = gardenInfo.groundInfo;
                    PlantInfo plantInfo = gardenInfo.plantInfo;

                    int plant = activity.getResources().getIdentifier("plant" + groundInfo.groundNo, "id", activity.getPackageName());
                    int plant_level = activity.getResources().getIdentifier("plant" + groundInfo.groundNo + "_level", "id", activity.getPackageName());
                    int plant_img = activity.getResources().getIdentifier("plant" + groundInfo.groundNo + "_img", "id", activity.getPackageName());
                    int plant_bar = activity.getResources().getIdentifier("plant" + groundInfo.groundNo + "_progressBar", "id", activity.getPackageName());

                    growMax = 0;
                    growMin = 0;
                    limit = 0;
                    growPoint = 0;
                    plantState = "";

                    switch (groundInfo.growLevel) {
                        case 0:
                            plantState = "새싹";
                            growMax += plantInfo.seedRequire;
                            growMin = 0;
                            break;
                        case 1:
                            plantState = "성장기";
                            growMax += plantInfo.stemRequire;
                            growMin = plantInfo.seedRequire;
                            break;
                        case 2:
                            plantState = "꽃봉우리";
                            growMax += plantInfo.flowerRequire;
                            growMin = plantInfo.stemRequire;
                            break;
                        case 3:
                            plantState = "꽃";
                            growMax = plantInfo.growLimit;
                            growMin = plantInfo.flowerRequire;
                            break;
                        case 4:
                            plantState = "열매";
                            growMax = plantInfo.growLimit;
                            growMin = plantInfo.flowerRequire;
                            break;
                    }

                    limit = (growMax - growMin) * 10;
                    growPoint = (int) ((groundInfo.growPoint - growMin) * 10);

                    info = "";
                    info = "이름: " + plantInfo.name + "\n\n";

                    info += "상태\n";
                    info += "  - 수분이 " + check("Water", plantInfo, groundInfo) + "\n";
                    info += "  - 영양이 " + check("Nutrient", plantInfo, groundInfo) + "\n";
                    info += "  - " + checkWither(plantInfo, groundInfo);

                    TextView plantText = view.findViewById(plant);
                    TextView plantLevel = view.findViewById(plant_level);
                    ImageView plantImg = view.findViewById(plant_img);
                    ProgressBar plantBar = view.findViewById(plant_bar);
                    if(plantText == null){
                        plantText = activity.findViewById(plant);
                        plantLevel = activity.findViewById(plant_level);
                        plantImg = activity.findViewById(plant_img);
                        plantBar = activity.findViewById(plant_bar);
                    }

                    plantText.setText("");
                    plantText.setText(info);

                    plantLevel.setText(plantState);

                    plantBar.setVisibility(View.VISIBLE);
                    plantBar.getProgressDrawable().setTint(Color.GREEN);//.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    plantBar.setScaleY(2);
                    plantBar.setMax(limit);
                    plantBar.setProgress(growPoint);

                    //Log.d("Garden", limit + " " + ((int) groundInfo.growPoint- growMin));
                    InputStream img = null;
                    try {
                        img = activity.getResources().getAssets().open("image/" + plantInfo.img + "/" + (groundInfo.growLevel + 1) + ".png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Drawable drawable = Drawable.createFromStream(img, null);

                    plantImg.setImageDrawable(drawable);
                }
            }
        });
    }
    private String checkWither(PlantInfo plantInfo, GroundInfo groundInfo) {
        String result = "건강해요.";

        int limit = plantInfo.witherLimit;
        int wither = groundInfo.wither;

        if (0 >= wither) return result;

        if (limit * 0.25 >= wither) {
            result = "시들고 있어요.";
        } else if (limit * 0.5 >= wither) {
            result = "조금 시들었어요.";
        } else if (limit * 0.75 >= wither) {
            result = "많이 시들었어요.";
        } else if (limit <= wither) {
            if (groundInfo.growLevel == 4)
                result = "모두 성장했어요.";
            else
                result = "완전 시들었어요.";
        }

        return result;
    }

    public void setWeather(){
        ShowInfo showInfo = showDao.getShowInfo();

        int weather = activity.getResources().getIdentifier("weather_view", "id", activity.getPackageName());
        WeatherView weatherView = view.findViewById(weather);

        // 웨더뷰를 찾지 못할 경우 다른 방식으로 가져옴
        if(weatherView == null){
            weatherView = activity.findViewById(weather);
        }

        //Log.d("ShowGarden", "" + weather);
        int color = 0;

        weatherView.setFadeOutPercent(1.5f);
        // 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
        switch (showInfo.weather){
            //비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
            case "1":
                color = Color.parseColor("#b2dfdb");
                weatherView.setWeatherData(PrecipType.RAIN);
                break;
            case "2":
                color = Color.parseColor("#b2dfdb");
                weatherView.setWeatherData(PrecipType.RAIN);
                break;
            case "3":
                color = Color.parseColor("#e0f7fa");
                weatherView.setWeatherData(PrecipType.SNOW);
                break;
            case "5":
            case "6":
                color = Color.parseColor("#b2dfdb");
                weatherView.setWeatherData(PrecipType.RAIN);
                weatherView.setEmissionRate(20f);
                break;
            case "7":
                color = Color.parseColor("#e0f7fa");
                weatherView.setWeatherData(PrecipType.SNOW);
                weatherView.setEmissionRate(2.5f);
                weatherView.setSpeed(150);
                break;
            default:
                color = Color.parseColor("#b3f3f3");
                weatherView.setWeatherData(PrecipType.CLEAR);
                break;
        }
        weatherView.setBackgroundColor(color);
    }
    public void setWeather(String weatherType){
        ShowInfo showInfo = showDao.getShowInfo();

        showInfo.weather = weatherType;
        showDao.setShowInfo(showInfo);

        int weather = activity.getResources().getIdentifier("weather_view", "id", activity.getPackageName());
        WeatherView weatherView = view.findViewById(weather);

        // 웨더뷰를 찾지 못할 경우 다른 방식으로 가져옴
        if(weatherView == null){
            weatherView = activity.findViewById(weather);
        }
        Log.d("ShowGarden", "" + weather);

        int color = 0;

        weatherView.setFadeOutPercent(1.5f);
        // 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
        switch (showInfo.weather){
            //비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
            case "1":
                color = Color.parseColor("#b2dfdb");
                weatherView.setWeatherData(PrecipType.RAIN);
                break;
            case "2":
                color = Color.parseColor("#b2dfdb");
                weatherView.setWeatherData(PrecipType.RAIN);
                break;
            case "3":
                color = Color.parseColor("#e0f7fa");
                weatherView.setWeatherData(PrecipType.SNOW);
                break;
            case "5":
            case "6":
                color = Color.parseColor("#b2dfdb");
                weatherView.setWeatherData(PrecipType.RAIN);
                weatherView.setEmissionRate(20f);
                break;
            case "7":
                color = Color.parseColor("#e0f7fa");
                weatherView.setWeatherData(PrecipType.SNOW);
                weatherView.setEmissionRate(2.5f);
                weatherView.setSpeed(150);
                break;
            default:
                color = Color.parseColor("#b3f3f3");
                weatherView.setWeatherData(PrecipType.CLEAR);
                break;
        }
        weatherView.setBackgroundColor(color);
    }
    private String check(String type, PlantInfo plantInfo, GroundInfo groundInfo) {
        String result = "없음";
        float var = 0;
        int require = 0;
        int min = 0;
        int max = 0;

        switch (type) {
            case "Temperature":
                try {
                    WeatherInfo weatherInfo = new WeatherProc(activity).getWeatherInfo();
                    var = Float.valueOf(weatherInfo.temp);
                    require = plantInfo.temperatureRequire;
                    min = plantInfo.temperatureMin;
                    max = plantInfo.temperatureMax;
                } catch (Exception e) {
                    Log.d("GrowProc", e.getMessage());
                }
                break;
            case "Water":
                var = groundInfo.water;
                require = plantInfo.waterRequire;
                min = plantInfo.waterMin;
                max = plantInfo.waterMax;
                break;
            case "Nutrient":
                var = groundInfo.nutrient;
                require = plantInfo.nutrientRequire;
                min = plantInfo.nutrientMin;
                max = plantInfo.nutrientMax;
                break;
        }

        float minRange = require - ((require - min) / 2);
        float maxRange = require + ((max - require) / 2);


        // 이탈
        if (max <= var) {
            result = "너무 많아요.";
            return result;
        } else if (var <= min) {
            result = "너무 부족해요.";
            return result;
        }


        // 범위 외
        if (var <= minRange) {
            result = "조금 부족해요.";
            return result;
        } else if (maxRange <= var) {
            result = "조금 많아요.";
            return result;
        } else

            // 범위 내
            if (var <= maxRange) {
                result = "적당해요.";
                return result;
            } else if (minRange <= var) {
                result = "적당해요.";
                return result;
            }

        return result;
    }

}
