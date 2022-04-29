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
import com.example.weathergarden.garden.PlantInfo;

import java.util.List;

public class DBTestActivity extends AppCompatActivity {

    EditText gNoT, pCodeT;
    TextView t;
    Button plant, pull, show;
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
                g.SetGroundInfo(
                        Integer.valueOf(gNoT.getText().toString()),
                        pCodeT.getText().toString(),
                        0,0,0,0,0);

                dao.InsertGroundInfo(g);
                s = g.groundNo + "번 땅에 식물이 심어졌습니다.";

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
                int groundNo = Integer.valueOf(gNoT.getText().toString());
                dao.DeleteGroundByGroundNO(groundNo);
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

                List<GardenInfo> gardenInfo = dao.GardenInfoList();
                if(gardenInfo != null) {
                    for (GardenInfo g : gardenInfo) {
                        GroundInfo groundInfo = g.groundInfo;
                        PlantInfo plantInfo = g.plantInfo;

                        s += groundInfo.groundNo + " ";
                        s += plantInfo.name + " ";
                        s += "\n";
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
            }
        };

        plant.setOnClickListener(cl);
        pull.setOnClickListener(cl);
        show.setOnClickListener(cl);
    }
}