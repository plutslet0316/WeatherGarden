package com.example.weathergarden;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenDatabase;
import com.example.weathergarden.garden.GardenInfo;
import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.PlantInfo;

import java.util.List;

public class DBTestActivity extends AppCompatActivity {

    EditText gNoT, pCodeT;
    TextView t;
    Button plant, pull, show;
    Button water, fert, grow;
    View.OnClickListener cl;

    String s;
    GardenDatabase db;
    GardenDao dao;

    public class THandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            t.setText(s);
            gNoT.setText("");
            pCodeT.setText("");
        }
    }

    THandler h;

    public class PlantingPlant extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                GroundInfo g = new GroundInfo();
                GrowProc.CarePlant carePlant = new GrowProc().withDao(dao).new CarePlant();

                int groundNo = Integer.valueOf(gNoT.getText().toString());
                String plantCode = pCodeT.getText().toString();

                carePlant.planting(groundNo, plantCode);

                s = groundNo + "번 땅에 식물이 심어졌습니다.";

                h.sendEmptyMessage(1);
            } catch (Exception e) {
                s = e.getMessage();
                h.sendEmptyMessage(1);
            }
        }
    }
    public class RemovePlant extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                GrowProc.CarePlant carePlant = new GrowProc().withDao(dao).new CarePlant();

                int groundNo = Integer.valueOf(gNoT.getText().toString());

                carePlant.removePlant(groundNo);

                s = groundNo + "번 땅에 있는 식물을 뽑았습니다.";

                h.sendEmptyMessage(1);
            } catch (Exception e) {
                s = e.getMessage();
                h.sendEmptyMessage(1);
            }
        }
    }
    public class ShowGarden extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                s = "";

                List<GardenInfo> gardenInfo = dao.readGardenInfoList();
                if(gardenInfo != null) {
                    for (GardenInfo g : gardenInfo) {
                        GroundInfo groundInfo = g.groundInfo;
                        PlantInfo plantInfo = g.plantInfo;

                        s += "No." + groundInfo.groundNo;
                        s += ":" + plantInfo.name + " | ";
                        s += "Lv:" + groundInfo.growLevel + " | ";
                        s += "Wa:" + groundInfo.water + " | ";
                        s += "N:" + groundInfo.nutrient + " | ";
                        s += "GP" + groundInfo.growPoint + " | ";
                        s += "Wi" + groundInfo.wither + "\n";
                    }
                } else {
                    s = "현재 모든 땅이 비어있습니다.";
                }
                h.sendEmptyMessage(1);
            } catch (Exception e) {
                s = e.getMessage();
                h.sendEmptyMessage(1);
            }
        }
    }

    public class Watering extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                s = "";

                int groundNo = Integer.valueOf(gNoT.getText().toString());
                int value = Integer.valueOf(pCodeT.getText().toString());

                GrowProc.CarePlant carePlant =
                        new GrowProc().withDao(dao).
                        new CarePlant().withGroundNo(groundNo);

                if(carePlant.addWater(value) == 1)
                    s += groundNo + "번 땅에 " + value + "만큼 물을 줍니다.";
                else
                    s += groundNo + "번 땅은 물이 충분합니다.";


                h.sendEmptyMessage(1);
            } catch (Exception e) {
                s = e.getMessage();
                h.sendEmptyMessage(1);
            }
        }
    }

    public class Feeding extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                s = "";

                int groundNo = Integer.valueOf(gNoT.getText().toString());
                int value = Integer.valueOf(pCodeT.getText().toString());

                GrowProc.CarePlant carePlant =
                        new GrowProc(getApplicationContext()).withDao(dao).
                        new CarePlant().withGroundNo(groundNo);

                if(carePlant.addNutrient(value) == 1)
                    s += groundNo + "번 땅에 " + value + "만큼 영양제를 줍니다.";
                else
                    s += groundNo + "번 땅은 영양이 충분합니다.";
                
                h.sendEmptyMessage(1);
            } catch (Exception e) {
                s = e.getMessage();
                h.sendEmptyMessage(1);
            }
        }
    }

    public class Growing extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                s = "";

                GrowProc gp = new GrowProc(getApplicationContext()).withDao(dao);
                int check = gp.startGrowing(getApplicationContext());
                switch (check){
                    case 1:
                        s += "식물을 성장시킵니다.";
                        break;
                    case 0:
                        s += "식물이 성장하기엔 너무 이른 시간입니다.";
                        break;
                }
                h.sendEmptyMessage(1);
            } catch (Exception e) {
                s = e.getMessage();
                h.sendEmptyMessage(1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);

        gNoT = findViewById(R.id.ground_no);
        pCodeT = findViewById(R.id.plant_code);
        t = findViewById(R.id.text);

        plant = findViewById(R.id.planting);
        pull = findViewById(R.id.pulling);
        show = findViewById(R.id.showing);

        water = findViewById(R.id.water);
        fert = findViewById(R.id.fertilizer);
        grow = findViewById(R.id.growing);

        h = new THandler();
        
        // DB 연동
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    db = GardenDatabase.getInstance(getApplicationContext());
                    dao = db.gardenDao();
                } catch (Exception e) {
                    s = e.getMessage();
                    h.sendEmptyMessage(1);
                }
            }
        };
        t.start();

        cl = v -> {
            switch (v.getId()) {
                case R.id.planting:
                    PlantingPlant plantingPlant = new PlantingPlant();
                    plantingPlant.start();
                    break;
                case R.id.pulling:
                    RemovePlant pullingPlant = new RemovePlant();
                    pullingPlant.start();
                    break;
                case R.id.showing:
                    ShowGarden showGarden = new ShowGarden();
                    showGarden.start();
                    break;
                case R.id.water:
                    Watering watering = new Watering();
                    watering.start();
                    break;
                case R.id.fertilizer:
                    Feeding feeding = new Feeding();
                    feeding.start();
                    break;
                case R.id.growing:
                    Growing growing = new Growing();
                    growing.start();
                    break;
            }
        };

        plant.setOnClickListener(cl);
        pull.setOnClickListener(cl);
        show.setOnClickListener(cl);
        water.setOnClickListener(cl);
        fert.setOnClickListener(cl);
        grow.setOnClickListener(cl);
    }
}