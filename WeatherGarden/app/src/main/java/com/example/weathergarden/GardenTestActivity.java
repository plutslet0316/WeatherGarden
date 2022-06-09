package com.example.weathergarden;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenDatabase;
import com.example.weathergarden.garden.GardenInfo;
import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.PlantInfo;
import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;

import java.util.ArrayList;
import java.util.List;

public class GardenTestActivity extends AppCompatActivity {

    Button g1, g2, g3, refresh;
    TextView textView;

    View.OnClickListener cl;

    GardenDatabase gardenDatabase;
    GardenDao gardenDao;
    GrowProc growProc;

    ArrayList<PlantInfo> plantInfoList;
    List<GardenInfo> gardenList;

    boolean check = true;

    // 액티비티 인텐트 핸들러
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == 10) {
                try {
                    Intent intentGet = result.getData();

                    // 받은 식물 심은 정보가 있다면 insert
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            GroundInfo groundInfoGet = (GroundInfo) intentGet.getSerializableExtra("ground_info");

                            if (groundInfoGet != null)
                                gardenDao.insertGroundInfo(groundInfoGet);
                        }
                    };
                    thread.start();
                    thread.join();

                    // 기다렸다가 정원 갱신
                    showGarden();
                } catch (Exception e) {
                    Log.d("test", e.getMessage());
                } finally {
                    return;
                }
            }
        });

    // 정원 정보 가져와서 가공한 후 넘겨준다.
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

    // 받은 정보 가공해서 표시하기
    void showInfo() {

        for (GardenInfo gardenInfo : gardenList) {
            GroundInfo groundInfo = gardenInfo.groundInfo;
            PlantInfo plantInfo = gardenInfo.plantInfo;
            String info;

            int plant = getResources().getIdentifier("plant" + groundInfo.groundNo, "id", getPackageName());

            info = "이름: " + plantInfo.name + "\n\n";

            info += "상태\n";
            info += "  - 수분:  " + groundInfo.water + "\n";
            info += "  - 영양:  " + groundInfo.nutrient + "\n";
            info += "  - 시듦:  " + groundInfo.wither;

            TextView textView = findViewById(plant);
            textView.setText("");
            textView.setText(info);
        }
    }

    boolean checkGround(int groundNo) {
        Intent intent = new Intent(this, PopupPlantSelectTest.class);
        intent.putExtra("plant_info", plantInfoList);
        check = true;

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (gardenDao.readGroundWithGroundNo(groundNo) == null) {
                    intent.putExtra("ground_no", groundNo);
                    mStartForResult.launch(intent);
                    check = false;
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return check;
    }

    void growing()
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                GrowProc gp = new GrowProc(getApplicationContext()).withDao(gardenDao);
                if(gp.startGrowing(getApplicationContext()) == 1){
                    textView.setText("식물을 성장시킵니다.");
                }else {
                    textView.setText("식물이 성장하기엔 시간이 이릅니다.");
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_test);
/*
        WeatherView weatherView = findViewById(R.id.weather_view);
        weatherView.setWeatherData(PrecipType.SNOW);
        weatherView.setAngle(15);
        weatherView.setScaleFactor(1f);
        weatherView.setEmissionRate(100f);
        weatherView.setFadeOutPercent(1f);
        weatherView.setSpeed(200);
*/
        g1 = findViewById(R.id.ground1);
        g2 = findViewById(R.id.ground2);
        g3 = findViewById(R.id.ground3);
        refresh = findViewById(R.id.refresh);
        textView = findViewById(R.id.info);

        // DB 연동
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    gardenDatabase = GardenDatabase.getInstance(getApplicationContext());
                    gardenDao = gardenDatabase.gardenDao();
                    growProc = new GrowProc().withDao(gardenDao);
                    growing();

                    plantInfoList = (ArrayList<PlantInfo>) gardenDao.readPlantsList();
                } catch (Exception e) {
                    Log.d("test", e.getMessage());
                }
            }
        };
        t.start();

        // 각 버튼 누르면 땅에 식물이 있는지 확인하고 없으면 식물 관리 팝업 띄움
        cl = v -> {
            int index = 0;
            try {
                textView.setText("");

                switch (v.getId()) {
                    case R.id.ground1:
                        index = 1;
                        break;
                    case R.id.ground2:
                        index = 2;
                        break;
                    case R.id.ground3:
                        index = 3;
                        break;
                    case R.id.refresh:
                        showGarden();
                        break;
                }
            } finally {
                if(index == 0) return;

                if(checkGround(index)) {
                    PopupCarePlantTest popupCarePlant = new PopupCarePlantTest(this, v, growProc, gardenDao, index);
                    popupCarePlant.displayPopupWindow();
                }
            }
        };

        g1.setOnClickListener(cl);
        g2.setOnClickListener(cl);
        g3.setOnClickListener(cl);
        refresh.setOnClickListener(cl);

        // 스레드 기다리고 끝나면 정원 보이기
        try {
            t.join();
            showGarden();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
