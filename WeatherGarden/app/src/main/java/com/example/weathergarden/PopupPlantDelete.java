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

public class PopupPlantDelete extends Activity {

    Button yes, no;
    View.OnClickListener cl;

    String plantCode = "";
    ArrayList<PlantInfo> plantInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_delete);

        yes = findViewById(R.id.delete_yes);
        no = findViewById(R.id.delete_no);

        Intent intent = new Intent(this, gardenFragment.class);

        cl = v -> {
            switch (v.getId())
            {
                case R.id.delete_yes:
                intent.putExtra("delete", 1);

                setResult(20, intent);
                finish();
                break;
                case R.id.delete_no:
                    finish();
                    break;
            }
        };

        yes.setOnClickListener(cl);
        no.setOnClickListener(cl);
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
