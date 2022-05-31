package com.example.weathergarden;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.weathergarden.garden.GrowProc;

public class PopupCarePlant extends PopupWindow {
    private View anchorView;
    private int index;
    private Activity context;
    private GrowProc.CarePlant carePlant;
    TextView textInfo;

    public PopupCarePlant(Activity context, View anchorView, GrowProc growProc, int index) {
        this.context = context;
        this.anchorView = anchorView;
        this.index = index;
        textInfo = context.findViewById(R.id.info);

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant = growProc.new CarePlant().withGroundNo(index);
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
                carePlant.addWater(100);
                textInfo.setText(index + "번 땅에 물을 줍니다.");
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addNutrient() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant.addNutrient(100);
                textInfo.setText(index + "번 땅에 영양제를 줍니다.");
            }
        };
        thread.start();

        try {
            thread.join();
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

            textInfo.setText(index + "번 땅의 식물을 뽑아냅니다.");

            TextView textView =  context.findViewById(plant);
            textView.setText("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}