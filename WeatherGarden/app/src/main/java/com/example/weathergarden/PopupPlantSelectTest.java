package com.example.weathergarden;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.PlantInfo;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class PopupPlantSelectTest extends Activity {

    Button submit;
    View.OnClickListener cl;

    String plantCode = "";
    ArrayList<PlantInfo> plantInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_plant_test);

        submit = findViewById(R.id.plant_select);

        Intent intentGet = getIntent();
        int groundNo = intentGet.getIntExtra("ground_no", 0);

        plantInfoList = (ArrayList<PlantInfo>) intentGet.getSerializableExtra("plant_info");

        ChipGroup chipGroup = findViewById(R.id.plant_group);

        for (PlantInfo plantInfo : plantInfoList) {
            Chip chip = new Chip(this); // Must contain context in parameter
            chip.setText(plantInfo.name);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                plantCode = plantInfo.plantCode;
            });
            chipGroup.addView(chip);
        }


        cl = v -> {
            if(plantCode != "") {
                Intent intent = new Intent(this, GardenTestActivity.class);

                GroundInfo groundInfo = new GroundInfo();
                groundInfo.setGroundInfo(groundNo, plantCode, 0, 0, 0, 0, 0);
                intent.putExtra("ground_info", groundInfo);

                setResult(10, intent);
                finish();
            }
        };

        submit.setOnClickListener(cl);
    }
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
*/
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
