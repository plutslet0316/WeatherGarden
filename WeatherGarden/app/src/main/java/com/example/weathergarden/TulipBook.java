package com.example.weathergarden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weathergarden.plantdb.DBManager;

public class TulipBook extends Activity {
    DBManager dbManager;
    SQLiteDatabase sqlDB;

    ImageButton tulip1, sunflower1;
    ImageView circle1, rectangle1, tul_img;
    TextView title2, diffi, flow, origin, light, temp, water, soil, earth, coun;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_tulip);

        tulip1 = (ImageButton) findViewById(R.id.tulip1);
        sunflower1 = (ImageButton) findViewById(R.id.sunflower1);
        circle1 = (ImageView) findViewById(R.id.circle1);
        rectangle1 = (ImageView) findViewById(R.id.rectangle1);
        tul_img = (ImageView) findViewById(R.id.tul_img);
        title2 = (TextView) findViewById(R.id.title2);

        diffi = (TextView) findViewById(R.id.diffi);
        flow = (TextView) findViewById(R.id.flow);
        origin = (TextView) findViewById(R.id.origin);
        light = (TextView) findViewById(R.id.light);
        temp = (TextView) findViewById(R.id.temp);
        water = (TextView) findViewById(R.id.water);
        soil = (TextView) findViewById(R.id.soil);
        earth = (TextView) findViewById(R.id.earth);
        coun = (TextView) findViewById(R.id.coun);


        dbManager = new DBManager(this);

        sunflower1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TulipBook.this, SunflowerBook.class);
                startActivity(intent);
                finish();
            }
        });


        dbManager.createDataBase();
        dbManager.openDataBase();
        dbManager.close();
        sqlDB = dbManager.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM plants ORDER BY ROWID DESC LIMIT 1;", null);

    /*
    데이터 전체 출력
    SELECT * FROM plants;
    마지막 데이터만 출력
    SELECT * FROM plants ORDER BY ROWID DESC LIMIT 1;   // 튤립
    첫 행만 출력
    SELECT * FROM plants ORDER BY ROWID LIMIT 1;    // 해바라기
     */

        String strDiff = "\n" + "난이도 : ";
        String strFlower = "개화시기 :  " ;
        String strOrigin = "\n" + "유래 : " ;
        String strLight = "햇빛 :  ";
        String strTemp = "온도 : " + "\r\n";
        String strWater = "물주기 : " + "\r\n";
        String strSoil = "흙 :  ";
        String strEarth = "비료 및 분갈이 : " + "\n";
        String strCoun = "원산지 : " ;

        while (cursor.moveToNext()) {

            strDiff += cursor.getString(3);
            strFlower += cursor.getString(4);
            strOrigin += cursor.getString(5) + "\r\n";
            strLight += cursor.getString(6);
            strTemp += cursor.getString(7) + "\r\n";
            strWater += cursor.getString(8) + "\r\n";
            strSoil += cursor.getString(9);
            strEarth += cursor.getString(10) + "\r\n";
            strCoun += cursor.getString(11) + "\r\n";
        }

        diffi.setText(strDiff);
        flow.setText(strFlower);
        origin.setText(strOrigin);
        light.setText(strLight);
        temp.setText(strTemp);
        water.setText(strWater);
        soil.setText(strSoil);
        earth.setText(strEarth);
        coun.setText(strCoun);

        cursor.close();
        sqlDB.close();

    }
}
