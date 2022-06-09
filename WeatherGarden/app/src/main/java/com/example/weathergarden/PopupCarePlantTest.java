package com.example.weathergarden;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.ShowGarden;

import java.util.List;

public class PopupCarePlantTest extends PopupWindow {
    private View anchorView;
    private int index;
    private Activity context;
    private GrowProc.CarePlant carePlant;
    TextView textInfo;
    GardenDao gardenDao;
    List<GardenInfo> gardenList;
    ShowGarden showGarden;

    public PopupCarePlantTest(Activity context, View anchorView, GrowProc growProc, GardenDao gardenDao, int index) {
        this.context = context;
        this.anchorView = anchorView;
        this.index = index;
        textInfo = context.findViewById(R.id.info);
        this.gardenDao = gardenDao;
        showGarden = new ShowGarden(anchorView, context, gardenDao);

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
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            showGarden.show();
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
            showGarden.show();
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
}