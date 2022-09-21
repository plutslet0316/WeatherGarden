package com.example.weathergarden;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenInfo;
import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.ShowGarden;

import java.util.List;

public class PopupCarePlant extends PopupWindow {
    private View anchorView;
    private int index;
    private Activity context;
    private GrowProc.CarePlant carePlant;
    ActivityResultLauncher<Intent> mStartForResult;
    GardenDao gardenDao;
    List<GardenInfo> gardenList;
    ShowGarden showGarden;
    int x, y;

    public PopupCarePlant(Activity context, View anchorView, GrowProc growProc, GardenDao gardenDao, ActivityResultLauncher<Intent> mStartForResult, int index, int x, int y) {
        this.context = context;
        this.anchorView = anchorView;
        this.index = index;
        this.gardenDao = gardenDao;
        this.x = x;
        this.y = y;
        this.mStartForResult = mStartForResult;
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
        ImageButton watering = layout.findViewById(R.id.watering_test);
        ImageButton nutrient = layout.findViewById(R.id.nutrient_test);
        ImageButton pulling = layout.findViewById(R.id.pulling_test);

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
                    Intent intent = new Intent(anchorView.getContext(), PopupPlantDelete.class);
                    mStartForResult.launch(intent);
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
        //Log.d("Popup", anchorView.getHeight()/4 + " " + anchorView.getWidth()/4);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        // Show anchored to button
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
    }

    private void addWater() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant.addWater(1000);

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

    public void removePlant() {
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
}