package com.example.weathergarden;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenInfo;
import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.PlantInfo;
import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PopupCarePlant extends PopupWindow {
    private View anchorView;
    private int index;
    private Activity context;
    private GrowProc.CarePlant carePlant;
    TextView textInfo;
    GardenDao gardenDao;
    List<GardenInfo> gardenList;

    public PopupCarePlant(Activity context, View anchorView, GrowProc growProc, GardenDao gardenDao, int index) {
        this.context = context;
        this.anchorView = anchorView;
        this.index = index;
        textInfo = context.findViewById(R.id.info);
        this.gardenDao = gardenDao;
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant = growProc.new CarePlant().withGroundNo(index);
                gardenList = gardenDao.readGardenInfoList();

            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 팝업 윈도우를 메뉴처럼 띄우는 부분
    void displayPopupWindow() {
        PopupWindow popup = new PopupWindow(context.getApplicationContext());
        View layout = context.getLayoutInflater().inflate(R.layout.popup_care_plant_test, null);
        popup.setContentView(layout);
        View.OnClickListener cl;

        // 기능
        Button watering = layout.findViewById(R.id.watering_test);
        Button nutrient = layout.findViewById(R.id.nutrient_test);
        Button pulling = layout.findViewById(R.id.pulling_test);

        cl = v ->{
            switch (v.getId()){
                case R.id.watering_test:
                    addWater();
                    popup.dismiss();
                    break;
                case R.id.nutrient_test:
                    addNutrient();
                    popup.dismiss();
                    break;
                case R.id.pulling_test:
                    removePlant();
                    popup.dismiss();
                    break;
            }
        };

        watering.setOnClickListener(cl);
        nutrient.setOnClickListener(cl);
        pulling.setOnClickListener(cl);

        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        // Show anchored to button
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.showAsDropDown(anchorView);
    }

    private void addWater() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant.addWater(1000);
                setInfoText(index + "번 땅에 물을 줍니다.");

            }
        };
        thread.start();

        try {
            thread.join();
            showGarden();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addNutrient() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant.addNutrient(1000);

                setInfoText(index + "번 땅에 영양제를 줍니다.");

            }
        };
        thread.start();

        try {
            thread.join();
            showGarden();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void removePlant() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant.removePlant(index);
            }
        };
        thread.start();

        try {
            thread.join();

            int plant = context.getResources().getIdentifier("plant" + index, "id", context.getPackageName());
            int plant_level = context.getResources().getIdentifier("plant" + index + "_level", "id", context.getPackageName());
            int plant_img = context.getResources().getIdentifier("plant" + index + "_img", "id", context.getPackageName());
            int plant_bar = context.getResources().getIdentifier("plant" + index +"_progressBar", "id", context.getPackageName());

            setInfoText(index + "번 땅의 식물을 뽑아냅니다.");

            TextView textView = context.findViewById(plant);
            TextView plantLevel = context.findViewById(plant_level);
            ImageView imageView = context.findViewById(plant_img);
            ProgressBar plantBar = context.findViewById(plant_bar);

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("");
                    plantLevel.setText("");
                    imageView.setImageResource(0);
                    plantBar.setVisibility(View.INVISIBLE);

                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void setInfoText(String text){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textInfo.setText(text);
            }
        });
    }
    void showGarden() {
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
        showInfo();
    }
    void showInfo() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (GardenInfo gardenInfo : gardenList) {
                    GroundInfo groundInfo = gardenInfo.groundInfo;
                    PlantInfo plantInfo = gardenInfo.plantInfo;
                    String info;

                    int plant = context.getResources().getIdentifier("plant" + groundInfo.groundNo, "id", context.getPackageName());
                    int plant_level = context.getResources().getIdentifier("plant" + groundInfo.groundNo + "_level", "id", context.getPackageName());
                    int plant_img = context.getResources().getIdentifier("plant" + groundInfo.groundNo + "_img", "id", context.getPackageName());
                    int plant_bar = context.getResources().getIdentifier("plant" + groundInfo.groundNo + "_progressBar", "id", context.getPackageName());

                    int growMax = 0;
                    int growMin = 0;

                    switch (groundInfo.growLevel) {
                        case 3:
                            growMax = plantInfo.growLimit;
                            break;
                        case 2:
                            growMax += plantInfo.flowerRequire;
                        case 1:
                            growMax += plantInfo.stemRequire;
                        case 0:
                            growMax += plantInfo.seedRequire;
                            break;
                    }
                    switch (groundInfo.growLevel) {
                        case 3:
                            growMin = plantInfo.flowerRequire;
                            break;
                        case 2:
                            growMin = plantInfo.stemRequire;
                            break;
                        case 1:
                            growMin = plantInfo.seedRequire;
                            break;
                        case 0:
                            growMin = 0;
                            break;
                    }

                    String plantState = "";
                    switch (groundInfo.growLevel) {
                        case 0:
                            plantState = "새싹";
                            break;
                        case 1:
                            plantState = "성장기";
                            break;
                        case 2:
                            plantState = "꽃봉우리";
                            break;
                        case 3:
                            plantState = "꽃";
                            break;
                        case 4:
                            plantState = "열매";
                            break;
                    }


                    info = "이름: " + plantInfo.name + "\n\n";

                    info += "상태\n";
                    info += "  - 수분이 " + check("Water", plantInfo, groundInfo) + "\n";
                    info += "  - 영양이 " + check("Nutrient", plantInfo, groundInfo) + "\n";
                    info += "  - " + checkWither(plantInfo, groundInfo);

                    TextView plantText = context.findViewById(plant);
                    TextView plantLevel = context.findViewById(plant_level);
                    ImageView plantImg = context.findViewById(plant_img);
                    ProgressBar plantBar = context.findViewById(plant_bar);

                    plantText.setText("");
                    plantText.setText(info);

                    plantLevel.setText(plantState);

                    plantBar.setVisibility(View.VISIBLE);
                    plantBar.getProgressDrawable().setTint(Color.GREEN);//.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    plantBar.setScaleY(2);
                    plantBar.setMax(growMax);
                    plantBar.setMin(growMin);
                    plantBar.setProgress((int) groundInfo.growPoint);

                    Log.d("Garden", growMax + " " + growMin + " " + (int) groundInfo.growPoint);
                    InputStream img = null;
                    try {
                        img = context.getResources().getAssets().open(plantInfo.img + "/" + (groundInfo.growLevel) + ".png");
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

        if (limit * 0.25 <= wither) {
            result = "시들고 있어요.";
        } else if (limit * 0.5 <= wither) {
            result = "조금 시들었어요.";
        }else if (limit * 0.75 <= wither) {
            result = "많이 시들었어요.";
        }else if (limit <= wither){
            result = "완전 시들었어요.";
        }

        return result;
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
                    WeatherInfo weatherInfo = new WeatherProc(context).getWeatherInfo();
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